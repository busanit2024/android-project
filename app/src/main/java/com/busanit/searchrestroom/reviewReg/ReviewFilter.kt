package com.busanit.searchrestroom.reviewReg

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class FilterType(val order: Int) {
    toilet_paper(0),    // 휴지 여부
    how_many(1),          // 칸 개수
    cleanliness(2)          // 청결도
    //
}

@Parcelize
enum class FilterOption(val filterType: FilterType, val optionName: String) : Parcelable {
    toilet_paper_yes(FilterType.toilet_paper,"휴지 있음"),
    toilet_paper_no(FilterType.toilet_paper,"휴지 없음"),

    toilet_1(FilterType.how_many, "1칸"),
    toilet_2(FilterType.how_many, "2칸"),
    toilet_3(FilterType.how_many, "3칸"),
    toilet_4_over(FilterType.how_many, "4칸 이상"),

    cleanliness_good(FilterType.cleanliness, "깨끗함"),
    cleanliness_soso(FilterType.cleanliness, "무난함"),
    cleanliness_bad(FilterType.cleanliness, "더러움");

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

        fun findByTypeAndName(type: FilterType, name: String): FilterOption? {
            return entries.find { it.filterType == type && it.optionName == name }
        }
    }
}

