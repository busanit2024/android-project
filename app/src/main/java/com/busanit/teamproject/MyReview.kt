package com.busanit.teamproject

import java.sql.Timestamp

data class MyReview(
    val review_id: Int,
    val buildingName: String,
    val reg_time: Timestamp,
    val reviewContent: String,
    val reviewImages: List<Int> // 리뷰 이미지 ID 목록
)