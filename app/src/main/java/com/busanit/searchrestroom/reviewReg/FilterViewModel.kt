package com.busanit.searchrestroom.reviewReg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {

    // 필터 옵션을 ReviewFilter로 관리 (FilterOption + selected)
    private val _filterOptions = MutableLiveData<List<FilterOptionState>>()
    val filterOptions: LiveData<List<FilterOptionState>> get() = _filterOptions

    init {
        // 초기 옵션 리스트 설정 (FilterType 순서대로)
        _filterOptions.value = FilterOption.getOptionsSortedByFilterType().flatten().map {
            FilterOptionState(option = it) // ReviewFilter로 변환
        }
    }

    // 필터 옵션을 선택하거나 해제하는 메서드
    fun toggleFilterOption(option: FilterOption) {
        // 기존 필터 옵션의 선택 상태를 반영
        _filterOptions.value = _filterOptions.value?.map {
            if (it.option == option) {
                it.copy(selected = !it.selected) // 선택 상태 반전
            } else {
                it
            }
        }
    }
}

