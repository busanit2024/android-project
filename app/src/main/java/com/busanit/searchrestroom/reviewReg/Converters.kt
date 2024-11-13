package com.busanit.searchrestroom.reviewReg

import androidx.room.TypeConverter
import com.busanit.searchrestroom.database.ReviewFilterOption

class Converters {

    @TypeConverter
    fun fromReviewFilterOptionList(options: List<ReviewFilterOption>?): String? {
        return options?.joinToString(",") { option ->
            "${option.reviewId},${option.filterType},${option.optionName}"
        }
    }

    @TypeConverter
    fun toReviewFilterOptionList(data: String?): List<ReviewFilterOption> {
        return data?.split(",")?.chunked(3) {
            val (reviewId, filterType, optionName) = it

            // FilterType과 optionName을 매핑하여 ReviewFilterOption 생성
            val filterTypeEnum = FilterType.values().find { it.ordinal == filterType.toInt() }
            val option = FilterOption.values().find { it.optionName == optionName }

            // 필터 타입과 옵션이 존재할 경우에만 ReviewFilterOption을 생성
            if (filterTypeEnum != null && option != null) {
                ReviewFilterOption(
                    reviewId = reviewId.toInt(),
                    filterType = filterTypeEnum,  // FilterType을 그대로 사용
                    optionName = option.optionName // optionName을 String으로 그대로 사용
                )
            } else {
                // 유효하지 않은 값이 있다면 빈 객체 반환
                ReviewFilterOption(reviewId.toInt(), FilterType.toilet_paper.ordinal, FilterType.how_many,"")
            }
        } ?: emptyList()
    }
}
