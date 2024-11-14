package com.busanit.searchrestroom.member

data class LoginResponse(
    val accessToken: String = "",
    val refreshToken: String = "",
)