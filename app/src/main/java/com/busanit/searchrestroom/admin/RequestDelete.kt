package com.busanit.searchrestroom.admin

class RequestDelete(
    val review_id: Int,
    val buildingName: String,
    val buildingAddress: String,
    val delete_request_time: String,
    val delete_request_reason: String
)