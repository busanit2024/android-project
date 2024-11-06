package com.busanit.teamproject

import java.sql.Timestamp

data class MyReview(
    val review_id: Int,
    val buildingName: String,
    val reg_time: Timestamp,
    val content: String,
)