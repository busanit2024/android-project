package com.busanit.searchrestroom.reviewReg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ReviewFilterOptonAdaper : RecyclerView.Adapter<ReviewFilterOptonAdaper.ViewHolder>(){

    private var optionList = listOf<FilterOption>()

    fun addOption(optionList: List<FilterOption>) {
        this.optionList = optionList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFilterOptionBinding = ItemFilterOptionBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(optionList[position])
        holder.itemView.setOnClickListener {
            holder.updateSelection(selectedOption)
        }
    }

    override fun getItemCount(): Int = optionList.size

    inner class ViewHolder(val binding: ItemFilterOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(option: FilterOption) {
            binding.option = option
            updateSelection(option)
        }

        fun updateSelection(option: FilterOption) {
            // 옵션 아이템 클릭 여부에 따른 UI 업데이트
        }
    }
}{
}