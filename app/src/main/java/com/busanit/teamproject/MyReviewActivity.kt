package com.busanit.teamproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.teamproject.databinding.ActivityMyReviewBinding
import java.sql.Timestamp

class MyReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 샘플 데이터
        val reviewItems = listOf(
            MyReview(1, "건물명", Timestamp.valueOf("2024-11-07 12:00:00"), "리뷰 내용1", listOf(R.drawable.empty_image, R.drawable.empty_image, R.drawable.empty_image)),
            MyReview(2, "건물명", Timestamp.valueOf("2024-11-06 09:22:00"), "리뷰 내용2", listOf(R.drawable.empty_image, R.drawable.empty_image, R.drawable.empty_image)),
            MyReview(3, "건물명", Timestamp.valueOf("2024-11-05 14:54:00"), "리뷰 내용3", listOf(R.drawable.empty_image, R.drawable.empty_image, R.drawable.empty_image))
        )

        // RecyclerView 설정
        binding.myReviewList.layoutManager = LinearLayoutManager(this)
        binding.myReviewList.adapter = ReviewAdapter(reviewItems)

        // 뒤로 가기 버튼 클릭 시
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}