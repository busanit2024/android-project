package com.busanit.searchrestroom.member

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.busanit.searchrestroom.mainPage.MainActivity
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User

class LoginActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(AppDatabase.getDatabase(application)!!.memberDao(), this)
        viewModel = ViewModelProvider(this, LoginViewModelFactory(repository)).get(LoginViewModel::class.java)

        auth = Firebase.auth

        // 로그인 버튼 클릭 시

        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            // 로그인 시도
            viewModel.loginUser(email, password)
        }


        // 구글 버튼 클릭 시
        binding.loginGoogle.setOnClickListener {
            val intent = Intent(this, GoogleLogin::class.java)
            startActivity(intent)
            finish()
        }

        // 회원가입 버튼 클릭 시
        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 카카오 버튼 클릭 시
        binding.loginKakao.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                // 카카오톡이 설치된 경우 카카오톡 앱으로 로그인
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡 로그인 실패", error)
                        // 오류가 발생하면 카카오 계정으로 로그인 시도
                        loginWithKaKaoAccount()
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공: ${token.accessToken}")
                        requestKakaoUserInfo()
                    }
                }
            } else {
                // 카카오톡이 설치되어 있지 않은 경우 웹 로그인 진행
                loginWithKaKaoAccount()
            }
        }
        
        // 로그인 결과 관찰
        viewModel.loginResult.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                // 로그인 성공 시 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            } else {
                Toast.makeText(this, "로그인 실패: $message", Toast.LENGTH_SHORT).show()
            }
        }

        var keyHash = Utility.getKeyHash(this)
        Log.i("kjwTest", "keyHash: $keyHash")
    }
    private fun loginWithKaKaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오 계정으로 로그인 성공: ${token.accessToken}")
                requestKakaoUserInfo()
            }
        }
    }

    private fun requestKakaoUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                // 사용자 정보 요청 실패 처리
                Log.e(TAG, "사용자 정보 요청 실패: ${error.message}")
                return@me
            } else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공: ${user.kakaoAccount?.email}")
                // 이메일 인증 여부 확인 및 ViewModel을 통해 로컬 DB에 저장
                val isEmailVerified = user.kakaoAccount?.isEmailVerified ?: false

                // 이메일 미인증 시 동의창 띄우기
                if (isEmailVerified) {
                   // 이미 인증된 경우 바로 ViewModel을 통해 저장
                    saveUserInfo(user)
                } else {
                    // 인증되지 않은 경우 추가 동의 요청
                    requestEmailConsent(user)
                }
            }
        }
    }
    // 이미 인증된 경우 ViewModel을 통해 저장하는 함수
    private fun saveUserInfo(user: User) {
        viewModel.loginKakaoUser(user)
    }

    // 이메일 인증 동의 요청을 위한 함수
    private fun requestEmailConsent(user: User) {
        UserApiClient.instance.loginWithNewScopes(this, listOf("account_email")) { _, consentError ->
            if (consentError != null) {
                Log.e(TAG, "동의 실패: ${consentError.message}")
            } else {
                Log.i(TAG, "동의 성공")
                // 동의 완료된 후 사용자 정보 저장
                saveUserInfo(user)
            }
        }
    }
}