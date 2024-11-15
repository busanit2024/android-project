package com.busanit.searchrestroom.review

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.databinding.ItemReviewViewBinding
import java.text.SimpleDateFormat
import java.util.Locale

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

                val formattedDate = ReviewViewModel.formatDateForDisplay(review.regDate)
                reviewRegDate.text = formattedDate.ifEmpty { "작성일자" }

                reviewText.text = review.reviewText

                filterOptionsContainer.removeAllViews() // 기존 뷰 모두 제거

                if (review.filterOptions.isNotEmpty()) {
                    filterOptionsContainer.visibility = View.VISIBLE

                    review.filterOptions.forEach { option ->
                        val optionView = TextView(itemView.context).apply {
                            text = option
                            textSize = 12f
                            setPadding(15, 0, 16, 8)
                            background = ContextCompat.getDrawable(context, R.drawable.capsule_box)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                marginEnd = 8.dpToPx(context)  // 옵션 사이 간격
                            }
                        }
                        filterOptionsContainer.addView(optionView)
                    }
                } else {
                    filterOptionsContainer.visibility = View.GONE
                }

                // 현재 로그인한 사용자가 리뷰 작성자인 경우에만 수정/삭제 버튼 표시
                val isAuthor = review.memberId == currentMemberId
                btnEditReview.visibility = if (isAuthor) View.VISIBLE else View.GONE
                btnDeleteReview.visibility = if (isAuthor) View.VISIBLE else View.GONE
                divisionLine.visibility = if(isAuthor) View.VISIBLE else View.GONE

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

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}