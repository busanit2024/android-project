package com.busanit.searchrestroom.member

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityFindIdBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FindIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindIdBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // AppDatabase를 통해 MemberDao 인스턴스를 가져와서 UserRepository 생성자에 전달
        val memberDao = AppDatabase.getDatabase(this).memberDao()
        userRepository = UserRepository(memberDao, this)

        // 뒤로 가기 버튼 클릭 시
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 확인하기 버튼 클릭 시 이벤트 발생
        binding.confirmButton.setOnClickListener {
            val email = binding.findEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                // 코루틴 사용하여 DB 작업 수행
                GlobalScope.launch {
                    val member = userRepository.memberDao.getMemberByEmail(email)
                    runOnUiThread {
                        if (member != null) {
                            Toast.makeText(
                                this@FindIdActivity,
                                "해당 이메일로 가입한 적이 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@FindIdActivity,
                                "해당 이메일로 가입한 기록이 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}