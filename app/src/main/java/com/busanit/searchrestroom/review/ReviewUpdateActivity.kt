//package com.busanit.searchrestroom.review
//
//import ReviewViewModel
//import android.os.Bundle
//import android.widget.EditText
//import android.widget.RadioGroup
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.busanit.searchrestroom.AuthHelper
//import com.busanit.searchrestroom.databinding.ActivityReviewUpdateBinding
//
//class ReviewUpdateActivity : AppCompatActivity() {
//
//  private lateinit var binding: ActivityReviewUpdateBinding
//  private lateinit var viewModel: ReviewViewModel
//
//  private lateinit var reviewEditText: EditText
//  private lateinit var toiletPaperRadioGroup: RadioGroup
//  private lateinit var howManyRadioGroup: RadioGroup
//  private lateinit var cleanlinessRadioGroup: RadioGroup
//
//  private var reviewId: Int = 0
//  private var reviewText: String = ""
//  private var toiletPaperOption: Int = 0
//  private var howManyOption: Int = 0
//  private var cleanlinessOption: Int = 0
//
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    binding = ActivityReviewUpdateBinding.inflate(layoutInflater)
//    setContentView(binding.root)
//
//    // Intent로 전달된 데이터 받기
//    val intent = intent
//    reviewId = intent.getIntExtra("reviewId", 0)
//    reviewText = intent.getStringExtra("reviewText") ?: ""
//    toiletPaperOption = intent.getIntExtra("toiletPaperOption", 0)
//    howManyOption = intent.getIntExtra("howManyOption", 0)
//    cleanlinessOption = intent.getIntExtra("cleanlinessOption", 0)
//
////    // UI 컴포넌트 초기화
////    reviewEditText = binding.updateReviewContent
////    toiletPaperRadioGroup = binding.toiletPaperRadioGroup
////    howManyRadioGroup = binding.howManyRadioGroup
////    cleanlinessRadioGroup = binding.cleanlinessRadioGroup
//
//    // 기존 리뷰 데이터로 UI 설정
//    reviewEditText.setText(reviewText)
//
//    // 라디오 버튼 초기화
//    when (toiletPaperOption) {
//      0 -> toiletPaperRadioGroup.check(binding.toiletPaperY.id)
//    }
//
//    when (howManyOption) {
//      1 -> howManyRadioGroup.check(binding.howMany1.id)
//      2 -> howManyRadioGroup.check(binding.howMany2.id)
//      3 -> howManyRadioGroup.check(binding.howMany3.id)
//      4 -> howManyRadioGroup.check(binding.howMany4.id)
//    }
//
//    when (cleanlinessOption) {
//      0 -> cleanlinessRadioGroup.check(binding.cleanlinessClean.id)  // 깨끗함
//      1 -> cleanlinessRadioGroup.check(binding.cleanlinessSoso.id) // 무난함
//      2 -> cleanlinessRadioGroup.check(binding.cleanlinessDirty.id)  // 더러움
//    }
//
//    // "수정 완료" 버튼 클릭 리스너
//    binding.updateReviewButton.setOnClickListener {
//      updateReview()
//    }
//  }
//
//  private fun updateReview() {
//    val updatedReviewText = reviewEditText.text.toString()
//    val updatedToiletPaperOption = if (toiletPaperRadioGroup.checkedRadioButtonId == binding.toiletPaperY.id) 0 else 1
//    val updatedHowManyOption = when (howManyRadioGroup.checkedRadioButtonId) {
//      binding.howMany1.id -> 1
//      binding.howMany2.id -> 2
//      binding.howMany3.id -> 3
//      binding.howMany4.id -> 4
//      else -> 0
//    }
//    val updatedCleanlinessOption = when (cleanlinessRadioGroup.checkedRadioButtonId) {
//      binding.cleanlinessClean.id -> 0
//      binding.cleanlinessSoso.id -> 1
//      binding.cleanlinessDirty.id -> 2
//      else -> 0
//    }
//
//    updateReview()
//
//    Toast.makeText(this, "리뷰가 수정되었습니다", Toast.LENGTH_SHORT).show()
//    finish()
//  }
//}
