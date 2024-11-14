package com.busanit.searchrestroom.myPage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.busanit.searchrestroom.databinding.ActivityMyReviewBinding
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.database.ReviewImage
import com.busanit.searchrestroom.member.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp

class MyReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyReviewBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "search-restroom"
        ).build()

        // 로그인한 사용자의 member_id 가져오기
        val memberId = sharedPreferences.getInt("member", -1)
        if (memberId == -1) {
            Toast.makeText(this, "로그인이 필요합니다!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 리뷰 로드
        loadReviews(memberId)

        // 뒤로 가기 버튼 클릭 시
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadReviews(memberId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val reviewDao = appDatabase.reviewDao()
            val reviews = reviewDao.getReviewByMemberId(memberId)

            withContext(Dispatchers.Main) {
                // RecyclerView에 데이터 설정
                setupRecyclerView(reviews)
            }
        }
    }

    private suspend fun setupRecyclerView(reviews: List<Review>) {
        binding.myReviewList.layoutManager = LinearLayoutManager(this)
        binding.myReviewList.adapter = ReviewAdapter(
            reviews.map { review ->
                // 리뷰에 해당하는 이미지 가져오기
                val reviewImages = getReviewImages(review.reviewId)
                // 건물명 가져오기
                val restroomName = getRestroomName(review.restroomId)

                Review(
                    review.reviewId,
                    review.restroomId,
                    review.memberId,
                    restroomName ?: "건물명 없음", // 건물명이 없을 때 대체 텍스트
                    (review.regTime?.let { Timestamp.valueOf(it) } ?: Timestamp(System.currentTimeMillis())).toString(),
                    review.content ?: ""
                )
            }
        )
    }

    private fun getReviewImages(reviewId: Int): List<ReviewImage> {
        val reviewImageDao = appDatabase.reviewImageDao()
        return reviewImageDao.getReviewImageById(reviewId) // 메소드 이름 수정
    }

    private suspend fun getRestroomName(restroomId: Int?): String? {
        restroomId?.let {
            val restroomDao = appDatabase.restroomDao()
            val restroom = restroomDao.getRestroomById(it)
            return restroom.restroomName
        }
        return null
    }

}