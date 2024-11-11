package com.busanit.searchrestroom.reviewReg

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.databinding.ActivityReviewRegBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class ReviewRegActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewRegBinding
    private val viewModel: FilterViewModel by viewModels() // ViewModel 연결

    private lateinit var adapterList: List<ReviewFilterOptionAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_review_reg)
        binding = ActivityReviewRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel을 바인딩합니다.
        binding.apply {
            //viewModel = this@ReviewRegActivity.viewModel // viewModel을 XML에 바인딩
            var lifecycleOwner = this@ReviewRegActivity // LiveData와 연결하기 위한 lifecycleOwner 설정
        }

        setFilterOptions()

        // 필터 옵션이 변경되면 UI를 갱신
        viewModel.filterOptions.observe(this, Observer { filterOptionStates ->
            // FilterOptionState 객체를 각 Adapter에 전달
            filterOptionStates.forEachIndexed { index, filterOptionState ->
                val filterType = filterOptionState.option.filterType
                val adapter = adapterList[filterType.ordinal] // FilterType에 맞는 어댑터를 선택
                adapter.addOption(filterOptionStates.filter { it.option.filterType == filterType })
            }
        })
    }

    private fun setFilterOptions() {
        // FilterOption을 FilterType 순서대로 정렬
        val filterOptionList = FilterOption.getOptionsSortedByFilterType() // FilterType에 따라 옵션 정렬

        // 각 FilterType에 대해 Adapter를 생성
        adapterList = filterOptionList.map { filterOptions ->
            ReviewFilterOptionAdapter { filterOptionState ->
                viewModel.toggleFilterOption(filterOptionState.option) // FilterOptionState에서 필터 옵션을 전달하여 선택 상태 토글
            }
        }

        binding.apply {
            // 각 RecyclerView와 Adapter를 연결
            val recyclerViewList = listOf(
                filterQuestion1OpenTime,
                filterQuestion2Comfort,
                filterQuestion3Unisex
            )

            // RecyclerView에 각각 Adapter 설정
            recyclerViewList.forEachIndexed { index, recyclerView ->
                recyclerView.apply {
                    adapter = adapterList[index]
                    layoutManager = FlexboxLayoutManager(context).apply {
                        flexWrap = FlexWrap.WRAP
                        flexDirection = FlexDirection.ROW
                    }
                }
            }
        }
    }
}


