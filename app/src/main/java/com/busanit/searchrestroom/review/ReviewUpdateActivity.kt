package com.busanit.searchrestroom.review

import ReviewViewModel
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.databinding.ActivityReviewRegBinding
import kotlinx.coroutines.launch

class ReviewUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewRegBinding
    private lateinit var viewModel: ReviewViewModel
    private var reviewId: Int = 0
    private var restroomId: Int = 0
    private var memberId: Int = 0
    private var restroom: Restroom? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        // 인텐트에서 데이터 받아오기
        reviewId = intent.getIntExtra("reviewId", -1)
        restroomId = intent.getIntExtra("restroomId", -1)
        binding.apply {
            reviewContent.setText(intent.getStringExtra("content"))
            toiletPaperY.isChecked = intent.getBooleanExtra("toiletPaperY", false)
            howMany.check(intent.getIntExtra("howManyOption", -1))
            cleanliness.check(intent.getIntExtra("cleanlinessOption", -1))
        }

        // 화면 초기화
        setupUI()
        // 기존 리뷰 데이터 설정
        setupExistingReview()
    }

    private fun setupUI() {
        // 툴바 타이틀 변경
        binding.toolbar.findViewById<TextView>(R.id.toolbarTitle).text = "리뷰 수정하기"

        // 건물 정보 설정
        binding.UdpateRestroomName.text = intent.getStringExtra("restroomName") ?: "건물명"
        binding.UpdateRestroomLocation.text = intent.getStringExtra("location") ?: "상세주소"

        // 작성완료 버튼 텍스트 변경 및 클릭 리스너 설정
        binding.writeReviewButton.text = "수정완료"
        binding.writeReviewButton.setOnClickListener {
            updateReview()
        }
    }

    private fun setupExistingReview() {
        // 기존 리뷰 내용 설정
        binding.apply {
            reviewContent.setText(intent.getStringExtra("content"))

            toiletPaperY.isChecked = intent.getBooleanExtra("toiletPaperY", false)

            val selectedHowMany = intent.getIntExtra("selectedHowMany", 0)
            howMany.check(selectedHowMany)

            val selectedCleanliness = intent.getIntExtra("selectedCleanliness", 0)
            cleanliness.check(selectedCleanliness)
        }
    }

    private fun updateReview() {
        val content = binding.reviewContent.text.toString()

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
            restroomId = restroom?.restroomId ?: 0,
            memberId = memberId,
            regTime = System.currentTimeMillis().toString(),
            updateTime = System.currentTimeMillis().toString(),
            content = binding.reviewContent.text.toString(),
            toiletPaperOption = toiletPaperOption,
            howManyOption = howManyOption,
            cleanlinessOption = cleanlinessOption
        )
        viewModel.insertReview(review)


        lifecycleScope.launch {
            try {
                viewModel.updateReview(review)  // Review 객체 전체를 전달
                Toast.makeText(this@ReviewUpdateActivity, "리뷰가 수정되었습니다", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@ReviewUpdateActivity, "리뷰 수정 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}