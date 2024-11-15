package com.busanit.searchrestroom.review

import androidx.room.ColumnInfo

data class ReviewWithMemberAndFilter(
    @ColumnInfo(name = "reviewId")
    val reviewId: Int?,

    @ColumnInfo(name = "restroomId")
    val restroomId: Int?,

    @ColumnInfo(name = "memberId")
    val memberId: Int?,

    @ColumnInfo(name = "nickname")
    val nickname: String?,

    @ColumnInfo(name = "reviewText")
    val reviewText: String?,

    @ColumnInfo(name = "regDate")
    val regDate: String?,

    @ColumnInfo(name = "toiletPaperOption")
    val toiletPaperOption: Int?,

    @ColumnInfo(name = "howManyOption")
    val howManyOption: Int?,

    @ColumnInfo(name = "cleanlinessOption")
    val cleanlinessOption: Int?
) {
    val filterOptions: List<String>
        get() {
            val options = mutableListOf<String>()

            toiletPaperOption?.let {
                when (it) {
                    1 -> options.add("휴지 있음")
                    2 -> options.add("휴지 없음")
                    else -> 1
                }
            }

            howManyOption?.let {
                when (it) {
                    1 -> options.add("1칸")
                    2 -> options.add("2칸")
                    3 -> options.add("3칸")
                    4 -> options.add("4칸 이상")
                    else -> 1
                }
            }

            cleanlinessOption?.let {
                when (it) {
                    1 -> options.add("깨끗함")
                    2 -> options.add("보통")
                    3 -> options.add("지저분함")
                    else -> 1
                }
            }

            return options
        }
    
}