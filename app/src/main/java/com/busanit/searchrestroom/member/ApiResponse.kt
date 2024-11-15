package com.busanit.searchrestroom.member

sealed class ApiResponse<out T : Any?> {
    data class Success<out T : Any?>(
        val data: T
    ) : ApiResponse<T>()

    data class Error(
        val errorCode: Int = 0,
        val errorMessage: String = "알 수 없는 오류"
    ) : ApiResponse<Nothing>()
}