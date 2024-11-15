package com.busanit.searchrestroom.member

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NaverLoginManager {

    // 초기 상태로 ApiResponse.Error 사용 (기본 메시지와 코드 설정)
    private val _loginResult = MutableStateFlow<ApiResponse<LoginResponse>>(
        ApiResponse.Error(errorMessage = "초기 상태: 로그인 대기 중")
    )
    val loginResult: StateFlow<ApiResponse<LoginResponse>> = _loginResult

    private val oauthLoginCallback = object : OAuthLoginCallback {
        override fun onSuccess() {
            _loginResult.value = ApiResponse.Success(
                LoginResponse(
                    accessToken = NaverIdLoginSDK.getAccessToken() ?: "",
                    refreshToken = NaverIdLoginSDK.getRefreshToken() ?: ""
                )
            )
        }

        override fun onFailure(httpStatus: Int, message: String) {
            _loginResult.value = ApiResponse.Error(
                errorCode = httpStatus,
                errorMessage = message
            )
        }

        override fun onError(errorCode: Int, message: String) {
            onFailure(errorCode, message)
        }
    }

    fun login(context: Context) {
        NaverIdLoginSDK.authenticate(context, oauthLoginCallback)
    }
}

