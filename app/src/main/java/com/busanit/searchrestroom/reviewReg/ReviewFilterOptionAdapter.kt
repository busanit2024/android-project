package com.busanit.searchrestroom.reviewReg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.databinding.ReviewFilterOptionBinding

class ReviewFilterOptionAdapter(private val onOptionClicked: (FilterOptionState) -> Unit) : RecyclerView.Adapter<ReviewFilterOptionAdapter.ViewHolder>() {
    private var optionList = listOf<FilterOptionState>()  // 필터 옵션 리스트

    // 옵션 목록을 추가하는 메서드
    fun addOption(options: List<FilterOptionState>) {
        optionList = options
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ReviewFilterOptionBinding =
            ReviewFilterOptionBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val optionState = optionList[position]
        holder.bind(optionState)

        // 클릭 시 옵션의 선택 상태 변경
        holder.itemView.setOnClickListener {
            onOptionClicked(optionState)  // 클릭된 옵션을 전달
        }
    }

    override fun getItemCount(): Int = optionList.size

    inner class ViewHolder(private val binding: ReviewFilterOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(optionState: FilterOptionState) {
            // binding에서 FilterOptionState를 설정
            binding.option = optionState.option  // FilterOptionState에서 option을 사용

            // 선택 상태에 따른 UI 업데이트
            binding.optionTextView.setBackgroundResource(
                if (optionState.selected) R.drawable.review_option_item_selected else R.drawable.review_option_item
            )
            binding.optionTextView.setTextColor(
                if (optionState.selected) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            )
        }
    }
}



