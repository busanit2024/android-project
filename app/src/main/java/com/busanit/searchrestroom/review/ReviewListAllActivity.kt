package com.busanit.searchrestroom.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.databinding.ActivityReviewListAllBinding
import com.busanit.searchrestroom.databinding.ActivityReviewListBinding
import com.busanit.searchrestroom.databinding.ActivityReviewRegBinding

class ReviewListAllActivity : AppCompatActivity() {
//    private lateinit var reviewViewModel: ReviewViewModel
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ReviewAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = ActivityReviewListAllBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        recyclerView = binding.reviewListAllRecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        reviewViewModel = ViewModelProvider(this).get(ReviewViewModel::class.java)
//        val restroomId = intent.getIntExtra("restroomId", -1)
//        reviewViewModel.loadReviewsByRestroomId(restroomId)
//
//        reviewViewModel.reviews.observe(this, Observer { reviews ->
//            adapter = ReviewAdapter(reviews)
//            recyclerView.adapter = adapter
//        })
//    }
}