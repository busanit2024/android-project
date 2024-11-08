package com.busanit.searchrestroom

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // 로그인 버튼 클릭 시 이벤트 발생
        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "이메일이나 비밀번호가 틀렸습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}