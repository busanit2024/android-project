package com.busanit.searchrestroom.member

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.dao.MemberDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityFindPwBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindPwActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindPwBinding
    private lateinit var memberDao: MemberDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindPwBinding.inflate(layoutInflater)
        setContentView(binding.root)
        memberDao = AppDatabase.getDatabase(application)!!.memberDao()

        // 뒤로 가기 버튼 클릭 리스너 설정
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 찾기 버튼 클릭 시 이벤트
        binding.findButton.setOnClickListener {
            val email = binding.findEmail.text.toString().trim()

            // 이메일 입력 여부 확인
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {

                GlobalScope.launch {
                    var member = memberDao.getMemberByEmail(email)
                    withContext(Dispatchers.Main) {
                        when (member!!.social) {
                            true -> {
                                Toast.makeText(
                                    this@FindPwActivity,
                                    "소셜회원은 비밀번호를 찾을 수 없습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }

                            false -> // Firebase에서 비밀번호 재설정 이메일 보내기
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this@FindPwActivity,
                                                "가입하신 이메일로 비밀번호 재설정 메일을 전송했습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // 로그인 액티비티 이동
                                            startActivity(
                                                Intent(
                                                    this@FindPwActivity,
                                                    LoginActivity::class.java
                                                )
                                            )
                                        } else {
                                            // Firebase 예외처리
                                            try {
                                                throw task.exception ?: Exception("Unknown error")
                                            } catch (e: FirebaseAuthInvalidUserException) {
                                                Toast.makeText(
                                                    this@FindPwActivity,
                                                    "존재하지 않는 이메일입니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                                                Toast.makeText(
                                                    this@FindPwActivity,
                                                    "최근에 로그인한 기기가 아닙니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } catch (e: FirebaseAuthUserCollisionException) {
                                                Toast.makeText(
                                                    this@FindPwActivity,
                                                    "사용자 충돌이 발생했습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    this@FindPwActivity,
                                                    "비밀번호 재설정 이메일 전송에 실패했습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                        }
                    }
                }
            }


        }
    }
}