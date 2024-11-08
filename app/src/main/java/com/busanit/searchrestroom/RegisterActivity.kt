package com.busanit.searchrestroom

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

lateinit var auth: FirebaseAuth

class RegisterActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth


        // 회원가입 버튼 클릭 시
        binding.registerBtn.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val password_check = binding.registerPasswordCheck.text.toString()
            val nickname = binding.registerNickname.text.toString()

            // 회원가입 시 입력 공란으로 두지 않게
            if (email == "" || password == "" || password_check == "" || nickname == "")
                Toast.makeText(this, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            else if (password != password_check){
                // 비밀번호가 일치하지 않는 경우
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // Firebase 회원가입 요청
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }

    }
}