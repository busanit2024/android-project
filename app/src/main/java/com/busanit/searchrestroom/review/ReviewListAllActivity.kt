package com.busanit.searchrestroom.review

import ReviewViewModel
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.databinding.ActivityReviewListAllBinding
import com.busanit.searchrestroom.databinding.ActivityReviewListBinding
import com.busanit.searchrestroom.databinding.ActivityReviewRegBinding
import kotlinx.coroutines.launch

class ReviewListAllActivity : AppCompatActivity(), ReviewAdapter.ReviewActionListener {
    private lateinit var binding: ActivityReviewListAllBinding
    private lateinit var viewModel: ReviewViewModel
    private var restroomId: Int = 0
    private var currentMemberId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewListAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restroomId = intent.getIntExtra("restroomId", 0)
        currentMemberId = AuthHelper.getMemberId()

        setupViewModel()
        observeReviews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        viewModel.loadAllReviews(restroomId)
    }

    private fun observeReviews() {
        viewModel.reviews.observe(this) { reviews ->
            setupRecyclerView(reviews)
        }
    }

    private fun setupRecyclerView(reviews: List<ReviewWithMemberAndFilter>) {
        val adapter = ReviewAdapter(reviews, currentMemberId, this)
        binding.reviewListAllRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReviewListAllActivity)
            this.adapter = adapter
        }
    }

    override fun onReviewEdit(review: ReviewWithMemberAndFilter) {
        review.reviewId?.let { reviewId ->
            val intent = Intent(this, ReviewUpdateActivity::class.java).apply {
                putExtra("reviewId", reviewId)
                putExtra("restroomId", review.restroomId)
                putExtra("content", review.reviewText)
                putExtra("toiletPaperOption", review.toiletPaperOption)
                putExtra("howManyOption", review.howManyOption)
                putExtra("cleanlinessOption", review.cleanlinessOption)
            }
            startActivity(intent)
        }
    }

    override fun onReviewDelete(review: ReviewWithMemberAndFilter) {
        review.reviewId?.let { reviewId ->
            AlertDialog.Builder(this)
                .setTitle("리뷰 삭제")
                .setMessage("리뷰를 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    lifecycleScope.launch {
                        viewModel.deleteReview(reviewId)
                        viewModel.loadAllReviews(restroomId)
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }
}