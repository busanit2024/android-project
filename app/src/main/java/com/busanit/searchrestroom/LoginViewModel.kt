package com.busanit.searchrestroom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    val loginResult = MutableLiveData<Pair<Boolean, String?>>()

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
}
class LoginViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}