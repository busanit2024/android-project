package com.busanit.searchrestroom.restroomDetail

import BookmarkRepository
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.dao.BookmarkDao
import com.busanit.searchrestroom.dao.ReviewDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Bookmark
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomDetailBinding
import com.busanit.searchrestroom.reviewReg.FilterOption
import com.busanit.searchrestroom.reviewReg.FilterOptionState
import com.busanit.searchrestroom.reviewReg.ReviewAdapter
import com.busanit.searchrestroom.reviewReg.ReviewRegActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class RestroomDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestroomDetailBinding
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var reviewDao: ReviewDao
    private lateinit var bookmarkRepository: BookmarkRepository
    private var isBookmarked = false
    private var memberId: Int = 0
    private var restroomId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestroomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(application)
        reviewDao = db!!.reviewDao()
        bookmarkDao = db.bookmarkDao()
        bookmarkRepository = BookmarkRepository(bookmarkDao, db.restroomDao())

        // 메인에서 인텐트로 데이터 받기
        val restroom: Restroom? = intent.getParcelableExtra("restroom")
        restroomId = intent.getIntExtra("restroomId", 0)

        // 현재 로그인한 사용자 정보 가져오기
        if (AuthHelper.isLoggedIn()) {
            memberId = AuthHelper.getMemberId()
            // 북마크 상태 체크 및 버튼 설정
            setupBookmarkButton()

            // 로그인한 경우 버튼들 보이기
            binding.restroomBookmark.visibility = View.VISIBLE
            binding.rewriteInfo.visibility = View.VISIBLE
            binding.writeReview.visibility = View.VISIBLE
        } else {
            // 로그인하지 않은 경우 버튼들 숨기기
            binding.restroomBookmark.visibility = View.GONE
            binding.rewriteInfo.visibility = View.GONE
            binding.writeReview.visibility = View.GONE

            Toast.makeText(this, "로그인 후 이용 가능합니다", Toast.LENGTH_SHORT).show()
        }

        // UI 설정
        setupUI(restroom)

        // 북마크 체크박스 클릭 이벤트
        binding.restroomBookmark.setOnCheckedChangeListener { _, isChecked ->
            if (!AuthHelper.isLoggedIn()) {
                Toast.makeText(this, "로그인이 필요한 서비스입니다", Toast.LENGTH_SHORT).show()
                binding.restroomBookmark.isChecked = !isChecked
                return@setOnCheckedChangeListener
            }
            toggleBookmark(isChecked)
        }

        //정보 수정 버튼 클릭 이벤트
        binding.rewriteInfo.setOnClickListener {
            val intent = Intent(this, RestroomUpdateActivity::class.java)
            intent.putExtra("restroom", restroom)
            startActivity(intent)
        }

        // 리뷰작성 버튼 클릭 이벤트
        binding.writeReview.setOnClickListener {
            val intent = Intent(this, ReviewRegActivity::class.java)
            intent.putExtra("restroomId", restroomId)
            intent.putExtra("restroom", restroom)
            startActivity(intent)
        }

        displayReviews(binding)
    }

    private fun setupUI(restroom: Restroom?) {
        restroom?.let {
            binding.restroomName.text = it.restroomName
            binding.location.text = it.location
            binding.openTime.text = it.openTime

            binding.unisexOrNot.apply {
                text = if (it.unisex == true) "남녀공용" else ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }
            binding.comfort.apply {
                text = if(it.diaper == true) "기저귀 교환대" else ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }
            binding.comfort.apply {
                text = if(it.accessible == true) "장애인 화장실" else ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }

            restroomId = it.restroomId
        }
    }

    private fun setupBookmarkButton() {
        lifecycleScope.launch {
            try {
                isBookmarked = bookmarkRepository.isBookmarked(memberId, restroomId)
                binding.restroomBookmark.isChecked = isBookmarked
            } catch (e: Exception) {
                Log.e("RestroomDetail", "Error checking bookmark status", e)
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
                val memberId = AuthHelper.getMemberId()
                if (isChecked) {
                    bookmarkRepository.addBookmark(memberId, restroomId)
                    Toast.makeText(this@RestroomDetailActivity,
                        "북마크에 추가되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    bookmarkRepository.removeBookmark(memberId, restroomId)
                    Toast.makeText(this@RestroomDetailActivity,
                        "북마크가 해제되었습니다", Toast.LENGTH_SHORT).show()
                }
                isBookmarked = isChecked
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
        // 로그인 여부에 따라 삭제, 수정 나오도록 하기



    // 리뷰 불러오기
    private fun displayReviews(binding: ActivityRestroomDetailBinding) {
        CoroutineScope(Dispatchers.IO).launch {
            val reviews = reviewDao.getLatestReviewsWithFilterByRestroomId(restroomId)

            // 각 리뷰에 대해 필터 옵션을 설정
            val reviewWithSelectedOptions = reviews.map { review ->
                val filterOptions = reviewDao.getFilterOptionsForReview(review.reviewId)
                val selectedOptions = filterOptions.mapNotNull { filterOption ->
                    val option = FilterOption.findByTypeAndName(filterOption.filterType, filterOption.optionName)
                    option?.let { FilterOptionState(option = it) }
                }
                review to selectedOptions
            }

            withContext(Dispatchers.Main) {
                // 어댑터에 매핑된 리뷰와 필터 옵션 리스트 전달
                val reviewAdapter = ReviewAdapter(reviewWithSelectedOptions)
                binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this@RestroomDetailActivity)
                binding.reviewRecyclerView.adapter = reviewAdapter
            }
        }
    }


    @SuppressLint("MissingInflatedId")
    private fun createReviewLayout(review: ReviewDao.ReviewWithFilter): View {
        val reviewLayout = layoutInflater.inflate(R.layout.item_review_view, null)

        // 리뷰 데이터 매핑
        val reviewTextView = reviewLayout.findViewById<TextView>(R.id.reviewText)
        reviewTextView.text = review.content

        val reviewerInfoTextView = reviewLayout.findViewById<TextView>(R.id.reviewerInfo)
        reviewerInfoTextView.text = "${review.nickname} / ${review.regTime}"

//        val filterOptionsTextView = reviewLayout.findViewById<TextView>(R.id.filterOptions)
//        filterOptionsTextView.text = review.selectedOptions.joinToString(", ") { it.optionName }

        return reviewLayout
    }
}