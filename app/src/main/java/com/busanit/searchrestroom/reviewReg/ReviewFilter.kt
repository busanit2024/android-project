package com.busanit.searchrestroom.reviewReg

enum class FilterType(val order: Int) {
    open_time(0),       // 상시개방 여부
    unisex(1),          // 공용화장실 여부
    comfort(2)          // 편의시설
}

enum class FilterOption(val filterType: FilterType, val optionName: String) {
    open_always(FilterType.open_time,"상시 개방"),

    unisex_yes(FilterType.unisex, "남녀공용"),

    comfort_diaper(FilterType.comfort, "기저귀 교환대"),
    comfort_accessible(FilterType.comfort, "장애인 화장실");

    companion object {
        // 필터 유형에 해당하는 선택지 리스트 반환
        fun findOptionsByFilterType(type: FilterType): List<FilterOption> {
            return entries.filter { it.filterType == type }
        }

        // 필터 유형 순서에 따라 정렬된 필터 옵션 리스트 반환
        fun getOptionsSortedByFilterType(): List<List<FilterOption>> {
            return FilterType.entries.sortedBy { it.order }.map { type ->
                findOptionsByFilterType(type)
            }
        }
    }
}