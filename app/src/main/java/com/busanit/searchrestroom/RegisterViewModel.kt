package com.busanit.searchrestroom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    val registerResult = MutableLiveData<Pair<Boolean, String?>>()
    val emailCheckResult = MutableLiveData<Boolean>()

    // 이메일 중복 체크
    fun checkEmailExists(email: String) {
        repository.checkIfEmailExists(email) { exists ->
            emailCheckResult.postValue(exists)  // 중복 여부 결과를 LiveData로 전달
        }
    }

    // 회원가입 메서드
    fun registerUser(email: String, password: String, nickname: String) {
        repository.registerUser(email, password, nickname) { success, message ->
            registerResult.postValue(Pair(success, message))
        }
    }
}

class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}