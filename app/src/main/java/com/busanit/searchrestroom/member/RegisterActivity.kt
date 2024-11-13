package com.busanit.searchrestroom.member

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

lateinit var auth: FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(AppDatabase.getDatabase(application).memberDao(), this)
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(repository)).get(
            RegisterViewModel::class.java)

        auth = Firebase.auth

        // 이메일 중복 체크 결과 관찰
        viewModel.emailCheckResult.observe(this) { exists ->
            if (exists) {
                Toast.makeText(this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show()
            } else {
                registerUser()
            }
        }

        // 뒤로 가기 버튼 클릭 시
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 회원가입 버튼 클릭 시
        binding.registerBtn.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val password_check = binding.registerPasswordCheck.text.toString()
            val nickname = binding.registerNickname.text.toString()

            // 입력란 공란 체크
            if (email.isBlank() || password.isBlank() || password_check.isBlank() || nickname.isBlank()) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호 재확인
            if (password != password_check) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이메일 중복 체크
            viewModel.checkEmailExists(email)
        }

        // 회원가입 결과 관찰
        viewModel.registerResult.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
                // 회원가입 성공 시, 로그인 화면으로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            } else {
                Toast.makeText(this, "회원가입 실패: $message", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerUser() {
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val nickname = binding.registerNickname.text.toString()
        viewModel.registerUser(email, password, nickname)
    }
}