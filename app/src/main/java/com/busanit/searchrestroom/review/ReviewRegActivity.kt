package com.busanit.searchrestroom.review

import ReviewViewModel
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.databinding.ActivityReviewRegBinding
import com.busanit.searchrestroom.restroomDetail.RestroomDetailActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class ReviewRegActivity : AppCompatActivity() {
    private lateinit var viewModel: ReviewViewModel
    private var restroom: Restroom? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReviewRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ReviewViewModel::class.java)
        val sharedPreferences: SharedPreferences by lazy {
            getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        }

        val memberId = sharedPreferences.getInt("member_id", -1)
        val reviewId = intent.getIntExtra("reviewId", -1)

        // 건물 정보 출력
        restroom = intent.getParcelableExtra<Restroom>("restroom")

        restroom?.let {
            binding.UdpateRestroomName.text = it.restroomName
            binding.UpdateRestroomLocation.text = it.location
        }

        binding.writeReviewButton.setOnClickListener {
            // 옵션 선택 상태 저장
            val toiletPaperOption = if (binding.toiletPaperY.isChecked) 1 else 2
            val howManyOption = when {
                binding.howMany1.isChecked -> 1
                binding.howMany2.isChecked -> 2
                binding.howMany3.isChecked -> 3
                binding.howMany4.isChecked -> 4
                else -> 1
            }
            val cleanlinessOption = when {
                binding.cleanlinessClean.isChecked -> 1
                binding.cleanlinessSoso.isChecked -> 2
                binding.cleanlinessDirty.isChecked -> 3
                else -> 1
            }

            val review = Review(
                reviewId = 0,
                //restroomId = restroom?.restroomId ?: 0,
                restroomId = 1,
                memberId = memberId,
                regTime = System.currentTimeMillis().toString(),
                updateTime = System.currentTimeMillis().toString(),
                content = binding.reviewContent.text.toString(),
                toiletPaperOption = toiletPaperOption,
                howManyOption = howManyOption,
                cleanlinessOption = cleanlinessOption
            )

            viewModel.insertReview(review)

            Toast.makeText(this, "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show()
            finish()

        }
    }
}


