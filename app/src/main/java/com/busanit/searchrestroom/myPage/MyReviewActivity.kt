package com.busanit.searchrestroom.myPage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.R
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
    private var appDatabase: AppDatabase? = null
  private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

      appDatabase = AppDatabase.getDatabase(applicationContext)

        // 로그인한 사용자의 member_id 가져오기
        val memberId = sharedPreferences.getInt("member_id", -1)
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
            val reviewDao = appDatabase!!.reviewDao()
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

                Review(
                    review.reviewId,
                    review.restroomId,
                    review.memberId,
                    review.content ?: "",
                    (review.regTime?.let { Timestamp.valueOf(it) } ?: Timestamp(System.currentTimeMillis())).toString(),
                ).apply {
                    // 각 리뷰 항목에 대한 클릭 리스너 설정
                    binding.root.findViewById<TextView>(R.id.delete).setOnClickListener {
                        deleteReview(review.reviewId)
                    }
                    binding.root.findViewById<TextView>(R.id.update).setOnClickListener {
                        val newContent = "새로운 리뷰 내용"    // 사용자 입력 받기
                        if (newContent != null) {
                            updateReview(review.reviewId, newContent)
                        }
                    }
                }
            }
        )
    }

    private fun getReviewImages(reviewId: Int): List<ReviewImage> {
        val reviewImageDao = appDatabase!!.reviewImageDao()
        return reviewImageDao.getReviewImageById(reviewId) // 메소드 이름 수정
    }



    private fun deleteReview(reviewId: Int) {
        // 코루틴 사용 -> 비동기적으로 삭제
        AlertDialog.Builder(this)
            .setTitle("리뷰 삭제")
            .setMessage("정말로 리뷰를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    val reviewDao = appDatabase.reviewDao()
                    reviewDao.getReviewById(reviewId)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MyReviewActivity, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        // 삭제 후 화면 갱신
                        finish()
                    }
                }
            }
            .setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateReview(reviewId: Int, newContent: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val reviewDao = appDatabase.reviewDao()
            val review = reviewDao.getReviewById(reviewId)

            if (review != null) {
                review.content = newContent
                reviewDao.getReviewById(reviewId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MyReviewActivity, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    // 수정 후 화면 갱신
                    finish()
                }
            }
        }
    }







}
