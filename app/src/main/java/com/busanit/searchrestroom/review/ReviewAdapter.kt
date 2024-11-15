package com.busanit.searchrestroom.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.databinding.ItemReviewViewBinding

class ReviewAdapter(
    private val reviewList: List<ReviewWithMemberAndFilter>,
    private val currentMemberId: Int,
    private val listener: ReviewActionListener
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    interface ReviewActionListener {
        fun onReviewEdit(review: ReviewWithMemberAndFilter)
        fun onReviewDelete(review: ReviewWithMemberAndFilter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount() = reviewList.size

    inner class ReviewViewHolder(private val binding: ItemReviewViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: ReviewWithMemberAndFilter) {
            binding.apply {
                reviewNickname.text = review.nickname
                reviewRegDate.text = review.regDate
                reviewText.text = review.reviewText

                // 필터 옵션 표시
                if (review.filterOptions.isNotEmpty()) {
                    filterOptions.text = review.filterOptions.joinToString(", ")
                    filterOptions.visibility = View.VISIBLE
                } else {
                    filterOptions.visibility = View.GONE
                }

                // 현재 로그인한 사용자가 리뷰 작성자인 경우에만 수정/삭제 버튼 표시
                val isAuthor = review.memberId == currentMemberId
                btnEditReview.visibility = if (isAuthor) View.VISIBLE else View.GONE
                btnDeleteReview.visibility = if (isAuthor) View.VISIBLE else View.GONE

                // 버튼 클릭 리스너는 버튼이 보일 때만 동작
                if (isAuthor) {
                    btnEditReview.setOnClickListener {
                        listener.onReviewEdit(review)
                    }
                    btnDeleteReview.setOnClickListener {
                        listener.onReviewDelete(review)
                    }
                }
            }
        }
    }
}