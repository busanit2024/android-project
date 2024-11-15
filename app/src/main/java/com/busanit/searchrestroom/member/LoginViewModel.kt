package com.busanit.searchrestroom.member

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    val loginResult = MutableLiveData<Pair<Boolean, String?>>()

    init {
        observeNaverLoginResult()
    }

    // 네이버 로그인 결과를 수신하고 로컬 DB에 저장
    private fun observeNaverLoginResult() {
        viewModelScope.launch {
            NaverLoginManager.loginResult.collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        val loginResponse = response.data
                        repository.loginNaverUser(loginResponse) { success, message ->
                            loginResult.postValue(Pair(success, message))
                        }
                    }
                    is ApiResponse.Error -> {
                        loginResult.postValue(Pair(false, response.errorMessage))
                    }
                }
            }
        }
    }

    // 로그인 메서드
    fun loginUser(email: String, password: String) {
        repository.loginUser(email, password) { success, message ->
            loginResult.postValue(Pair(success, message))
        }
    }

    // 구글 로그인 메서드
    fun loginGoogleUser(firebaseUser: FirebaseUser?) {
        repository.loginGoogleUser(firebaseUser) { success, message ->
            loginResult.postValue(Pair(success, message))
        }
    }

    // 카카오 로그인 메서드
    fun loginKakaoUser(kakaoUser: User) {
        repository.loginKakaoUser(kakaoUser) { success, message ->
            loginResult.postValue(Pair(success, message))
        }
    }

    // 네이버 로그인 요청
    fun loginNaver(context: Context) {
        NaverLoginManager.login(context)
    }
}



class LoginViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}