package com.busanit.searchrestroom.reviewReg

data class FilterOptionState(
    val option: FilterOption,
    val selected: Boolean = false
) {
    val name: String
        get() = option.optionName

    // 선택 상태를 변경하는 함수
    fun toggleSelected() = copy(selected = !selected)
}