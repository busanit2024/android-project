package com.busanit.searchrestroom.reviewReg

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.busanit.searchrestroom.activity.MainActivity
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.databinding.ActivityReviewRegBinding
import com.busanit.searchrestroom.restroomDetail.RestroomDetailActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class ReviewRegActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewRegBinding
    private val viewModel: FilterViewModel by viewModels() // ViewModel 연결

    private var restroom: Restroom? = null

    private lateinit var adapterList: List<ReviewFilterOptionAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 건물 정보 출력
        restroom = intent.getParcelableExtra<Restroom>("restroom")

        restroom?.let {
            binding.UdpateRestroomName.text = it.restroomName
            binding.UpdateRestroomLocation.text = it.location
        }

        // 옵션 화면출력
        // ViewModel 바인딩
        binding.apply {
            viewModel = this@ReviewRegActivity.viewModel  // ViewModel을 XML에 연결
            lifecycleOwner = this@ReviewRegActivity  // LiveData와 연결할 라이프사이클 소유자 설정
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

        binding.writeReviewButton.setOnClickListener {
            onWriteReviewClicked() // 별도 함수로 분리하여 호출
        }
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

    private fun onWriteReviewClicked() {
        val selectedOptions = mutableListOf<FilterOptionState>()
        viewModel.filterOptions.value?.forEach { optionState ->
            if (optionState.selected) {
                selectedOptions.add(optionState)
            }
        }

        val reviewContent = binding.reviewContent.text.toString()

        // sharedPreferences로 memberId 가져오기
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("memberId", 0) // 로그인한 사용자의 ID

        val review = Review(
            reviewId = 0,
            restroomId = restroom?.restroomId,
            memberId = memberId, // member 연결해야함
            content = reviewContent,
            regTime = System.currentTimeMillis().toString(),
            updateTime = System.currentTimeMillis().toString(),

        )

        // 선택된 필터옵션을 리스트로 저장해서 리뷰에 추가
        review.selectedOptions = selectedOptions

        viewModel.insertReview(review)

        Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, RestroomDetailActivity::class.java)
        startActivity(intent)
    }
}


