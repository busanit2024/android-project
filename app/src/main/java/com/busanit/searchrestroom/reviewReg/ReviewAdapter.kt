package com.busanit.searchrestroom.reviewReg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.dao.ReviewDao

class ReviewAdapter(
    private val reviewsWithSelectedOptions: List<Pair<ReviewDao.ReviewWithFilter, List<FilterOptionState>>>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    // ViewHolder 정의
    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nickname: TextView = view.findViewById(R.id.reviewerInfo)
        val content: TextView = view.findViewById(R.id.reviewText)
        val filterOptionsTextView: TextView = view.findViewById(R.id.filterOptions)
    }

    // onCreateViewHolder - ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_view, parent, false)
        return ReviewViewHolder(view)
    }

    // onBindViewHolder - 데이터와 뷰를 연결
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val (review, selectedOptions) = reviewsWithSelectedOptions[position]
        holder.nickname.text = review.nickname ?: "알 수 없음"
        holder.content.text = review.content

        // 필터 옵션 텍스트 설정
        val filterOptionsText = selectedOptions.joinToString(", ") { it.name }
        holder.filterOptionsTextView.text = if (filterOptionsText.isNotEmpty()) filterOptionsText else "옵션 없음"
    }

    // getItemCount - 아이템 개수 반환
    override fun getItemCount(): Int = reviewsWithSelectedOptions.size
}
