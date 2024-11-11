package com.busanit.searchrestroom

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.databinding.ActivityMyReviewBinding
import com.busanit.searchrestroom.MyReview
import com.busanit.searchrestroom.ReviewAdapter
import com.busanit.searchrestroom.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.sql.Timestamp

class MyReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyReviewBinding
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()

        // 샘플 데이터
        val reviewItems = listOf(
            MyReview(1, "건물명", Timestamp.valueOf("2024-11-07 12:00:00"), "리뷰 내용1", listOf(R.drawable.empty_image, R.drawable.empty_image, R.drawable.empty_image)),
            MyReview(2, "건물명", Timestamp.valueOf("2024-11-06 09:22:00"), "리뷰 내용2", listOf(R.drawable.empty_image, R.drawable.empty_image, R.drawable.empty_image)),
            MyReview(3, "건물명", Timestamp.valueOf("2024-11-05 14:54:00"), "리뷰 내용3", listOf(R.drawable.empty_image, R.drawable.empty_image, R.drawable.empty_image))
        )

        // RecyclerView 설정
        binding.myReviewList.layoutManager = LinearLayoutManager(this)
        binding.myReviewList.adapter = ReviewAdapter(
            reviewItems,
            onEditClick = { position ->
                val review = reviewItems[position]
                updateReview(review.review_id, review.reviewContent)
            },
            onDeleteClick = { position ->
                // 삭제 버튼 클릭 시 해당 리뷰 삭제
                val review = reviewItems[position]
                deleteReview(review.review_id)
            }
        )

        // 뒤로 가기 버튼 클릭 시
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun deleteReview(reviewId: Int) {
        // 코루틴 사용 -> 비동기적으로 삭제
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