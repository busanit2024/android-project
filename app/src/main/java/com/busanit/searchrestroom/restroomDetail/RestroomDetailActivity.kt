package com.busanit.searchrestroom.restroomDetail

import ReviewViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.dao.BookmarkDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomDetailBinding
import com.busanit.searchrestroom.review.ReviewAdapter
import com.busanit.searchrestroom.review.ReviewListAllActivity
import com.busanit.searchrestroom.review.ReviewRegActivity
import com.busanit.searchrestroom.review.ReviewWithMemberAndFilter


class RestroomDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestroomDetailBinding
    private lateinit var bookmarkDao: BookmarkDao
    private var memberId: Int = 0
    private var restroomId: Int = 0
    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestroomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberId = sharedPreferences.getInt("member_id", -1)

        initializeData()
        setupRestroom()
        setupReviewRecyclerView()
        setupButtons()
    }

    private fun initializeData() {
        restroomId = intent.getIntExtra("restroomId", -1)
        memberId = sharedPreferences.getInt("member_id", -1)
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        bookmarkDao = AppDatabase.getDatabase(application).bookmarkDao()
    }

    private fun setupRestroom() {
        val restroom: Restroom? = intent.getParcelableExtra("restroom")

        restroom?.let {
            binding.unisexOrNot.apply {
                text = if (restroom?.unisex == true) "남녀공용" else ""
                visibility = if (restroom?.unisex == true) View.VISIBLE else View.GONE
            }

            binding.comfort.apply {
                text = if (restroom?.diaper == true) "기저귀 교환대" else ""
                visibility = if (restroom?.diaper == true) View.VISIBLE else View.GONE
            }

            binding.comfort.apply {
                text = if (restroom?.accessible == true) "장애인 화장실" else ""
                visibility = if (restroom?.accessible == true) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupReviewRecyclerView() {
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.loadLatestReviews(restroomId)
        viewModel.latestReviews.observe(this) { reviews ->
            reviewAdapter = ReviewAdapter(
                reviewList = reviews,
                currentMemberId = memberId,  // 현재 로그인한 사용자의 ID 전달
                listener = object : ReviewAdapter.ReviewActionListener {
                    override fun onReviewEdit(review: ReviewWithMemberAndFilter) {
                        // 추가 보안 체크
                        if (review.memberId == memberId) {
                            val intent = Intent(
                                this@RestroomDetailActivity,
                                ReviewRegActivity::class.java
                            ).apply {
                                putExtra("reviewId", review.reviewId)
                                putExtra("restroomId", restroomId)
                                putExtra("isEdit", true)
                                putExtra("content", review.reviewText)
                                putExtra("toiletPaperOption", review.toiletPaperOption)
                                putExtra("howManyOption", review.howManyOption)
                                putExtra("cleanlinessOption", review.cleanlinessOption)
                            }
                            startActivity(intent)
                        }
                    }

                    override fun onReviewDelete(review: ReviewWithMemberAndFilter) {
                        // memberId null 체크 추가
                        if (review.memberId == memberId) {
                            // reviewId null 체크 추가
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

        // 로그인 상태에 따른 버튼 표시
        binding.apply {
            rewriteInfo.visibility = if (memberId != -1) View.VISIBLE else View.GONE
            writeReview.visibility = if (memberId != -1) View.VISIBLE else View.GONE
        }

        // 정보 수정 버튼
        binding.rewriteInfo.setOnClickListener {
            val intent = Intent(this, RestroomUpdateActivity::class.java).apply {
                putExtra("restroom", restroom)
            }
            startActivity(intent)
        }

        // 리뷰 작성 버튼
        binding.writeReview.setOnClickListener {
            val intent = Intent(this, ReviewRegActivity::class.java).apply {
                putExtra("restroomId", restroomId)
                putExtra("restroom", restroom)
            }
            startActivity(intent)
        }

        // 전체 리뷰 보기 버튼
        binding.viewAllReviewsButton.setOnClickListener {
            val intent = Intent(this, ReviewListAllActivity::class.java).apply {
                putExtra("restroomId", restroomId)
            }
            startActivity(intent)
        }
    }
}