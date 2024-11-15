package com.busanit.searchrestroom.restroomDetail

import BookmarkRepository
import ReviewViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.dao.BookmarkDao
import com.busanit.searchrestroom.dao.ReviewDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.review.ReviewAdapter
import com.busanit.searchrestroom.review.ReviewListAllActivity
import com.busanit.searchrestroom.review.ReviewRegActivity
import com.busanit.searchrestroom.review.ReviewUpdateActivity
import com.busanit.searchrestroom.review.ReviewWithMemberAndFilter


class RestroomDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestroomDetailBinding
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var reviewDao: ReviewDao
    private lateinit var bookmarkRepository: BookmarkRepository
    private var isBookmarked = false
    private var memberId: Int = 0
    private var restroomId: Int = 0
    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    private var db: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestroomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(application)
        reviewDao = db!!.reviewDao()
        bookmarkDao = db!!.bookmarkDao()
        bookmarkRepository = BookmarkRepository(bookmarkDao, db!!.restroomDao())

        initializeData()
        setupRestroom()
        setupReviewRecyclerView()
        setupButtons()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }


    private fun initializeData() {
        restroomId = intent.getIntExtra("restroomId", -1)
        memberId = AuthHelper.getMemberId()
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
    }

    private fun setupRestroom() {
        val restroom: Restroom? = intent.getParcelableExtra("restroom")
        restroomId = intent.getIntExtra("restroomId", 0)

        if (AuthHelper.isLoggedIn()) {
            memberId = AuthHelper.getMemberId()
            setupBookmarkButton()
            binding.apply {
                restroomBookmark.visibility = View.VISIBLE
                rewriteInfo.visibility = View.VISIBLE
                writeReview.visibility = View.VISIBLE

            }
        } else {
            binding.apply {
                restroomBookmark.visibility = View.GONE
                rewriteInfo.visibility = View.GONE
                writeReview.visibility = View.GONE
            }
        }

        setupUI(restroom)

        binding.restroomBookmark.setOnCheckedChangeListener { _, isChecked ->
            if (!AuthHelper.isLoggedIn()) {
                Toast.makeText(this, "로그인이 필요한 서비스입니다", Toast.LENGTH_SHORT).show()
                binding.restroomBookmark.isChecked = !isChecked
                return@setOnCheckedChangeListener
            }
            toggleBookmark(isChecked)
        }
    }

    private fun setupUI(restroom: Restroom?) {
        restroom?.let {
            binding.apply {
                restroomName.text = it.restroomName
                location.text = it.location
                openTime.text = it.openTime

                chipFullTime.isChecked = it.fullTime == true
                chipDiaper.isChecked = it.diaper == true
                chipAccessible.isChecked = it.accessible == true
                chipUnisex.isChecked = it.unisex == true

                memoText.text = it.memo ?: "기타 정보가 없습니다."
                memoText.visibility = if (it.memo.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            restroomId = it.restroomId
        }
    }

    private fun setupReviewRecyclerView() {
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.loadLatestReviews(restroomId)
        viewModel.latestReviews.observe(this) { reviews ->
            reviewAdapter = ReviewAdapter(
                reviewList = reviews,
                currentMemberId = memberId,
                listener = object : ReviewAdapter.ReviewActionListener {
                    override fun onReviewEdit(review: ReviewWithMemberAndFilter) {
                        if (review.memberId == memberId) {
                            val intent = Intent(this@RestroomDetailActivity, ReviewUpdateActivity::class.java).apply {
                                putExtra("reviewId", review.reviewId)
                                putExtra("restroomId", restroomId)
                                putExtra("memberId",memberId)
                                putExtra("content", review.reviewText)
                                putExtra("toiletPaperOption", review.toiletPaperOption)
                                putExtra("howManyOption", review.howManyOption)
                                putExtra("cleanlinessOption", review.cleanlinessOption)
                                putExtra("restroomName", binding.restroomName.text.toString())
                                putExtra("location", binding.location.text.toString())
                            }
                            startActivity(intent)
                        }
                    }

                    override fun onReviewDelete(review: ReviewWithMemberAndFilter) {
                        if (review.memberId == memberId) {
                            review.reviewId?.let { reviewId ->
                                AlertDialog.Builder(this@RestroomDetailActivity)
                                    .setTitle("리뷰 삭제")
                                    .setMessage("이 리뷰를 삭제하시겠습니까?")
                                    .setPositiveButton("삭제") { _, _ ->
                                        viewModel.deleteReview(reviewId)
                                    }
                                    .setNegativeButton("취소", null)
                                    .show()
                            }
                        }
                    }
                }
            )
            binding.reviewRecyclerView.adapter = reviewAdapter
        }
    }

    private fun setupButtons() {
        val restroom: Restroom? = intent.getParcelableExtra("restroom")

        binding.rewriteInfo.setOnClickListener {
            val intent = Intent(this, RestroomUpdateActivity::class.java).apply {
                putExtra("restroom", restroom)
            }
            startActivity(intent)
        }

        binding.writeReview.setOnClickListener {
            val intent = Intent(this, ReviewRegActivity::class.java).apply {
                putExtra("restroomId", restroomId)
                putExtra("restroom", restroom)
            }
            startActivity(intent)
        }

        binding.viewAllReviewsButton.setOnClickListener {
            val intent = Intent(this, ReviewListAllActivity::class.java).apply {
                putExtra("restroomId", restroomId)
            }
            startActivity(intent)
        }
    }

    private fun setupBookmarkButton() {
        lifecycleScope.launch {
            try {
                isBookmarked = withContext(Dispatchers.IO) {
                    bookmarkRepository.isBookmarked(memberId, restroomId)
                }
                binding.restroomBookmark.isChecked = isBookmarked
            } catch (e: Exception) {
                Log.e("RestroomDetail", "Error checking bookmark status", e)
                Toast.makeText(
                    this@RestroomDetailActivity,
                    "북마크 상태 확인 중 오류가 발생했습니다",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun toggleBookmark(isChecked: Boolean) {
        if (!AuthHelper.isLoggedIn()) {
            Toast.makeText(this, "로그인이 필요한 서비스입니다", Toast.LENGTH_SHORT).show()
            binding.restroomBookmark.isChecked = !isChecked
            return
        }

        lifecycleScope.launch {
            try {
                val currentBookmarkStatus = withContext(Dispatchers.IO) {
                    bookmarkRepository.isBookmarked(memberId, restroomId)
                }

                if (currentBookmarkStatus != isChecked) {
                    withContext(Dispatchers.IO) {
                        if (isChecked) {
                            bookmarkRepository.addBookmark(memberId, restroomId)
                        } else {
                            bookmarkRepository.removeBookmark(memberId, restroomId)
                        }
                    }

                    isBookmarked = isChecked
                    val message = if (isChecked) "북마크에 추가되었습니다" else "북마크가 해제되었습니다"
                    Toast.makeText(this@RestroomDetailActivity, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RestroomDetail", "Error toggling bookmark", e)
                binding.restroomBookmark.isChecked = !isChecked
                Toast.makeText(
                    this@RestroomDetailActivity,
                    "북마크 처리 중 오류가 발생했습니다",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val updatedRestroom = db?.restroomDao()?.getRestroomById(restroomId)
                withContext(Dispatchers.Main) {
                    updatedRestroom?.let {
                        intent.removeExtra("restroom")  // 기존 데이터 제거
                        intent.putExtra("restroom", it)  // 새 데이터 추가

                        setupUI(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("RestroomDetail", "Error loading updated restroom info", e)
            }
        }
        if (AuthHelper.isLoggedIn()) {
            setupBookmarkButton()
        }

        viewModel.loadLatestReviews(restroomId)

    }
}