package com.busanit.teamproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit.teamproject.databinding.ActivityMypageBinding

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logout.setOnClickListener {
            // 로그아웃

        }

        binding.editInfo.setOnClickListener {
            // 회원정보 수정
            val intent = Intent(this, EditInfoActivity::class.java)
            startActivity(intent)
        }

        binding.deleteAccount.setOnClickListener {
            // 회원탈퇴

        }
    }
}