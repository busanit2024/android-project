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

        binding.myReview.setOnClickListener {
            // 나의 리뷰
        }

        binding.myRestroom.setOnClickListener {
            // 등록한 화장실
        }

        binding.myFavorite.setOnClickListener {
            // 즐겨찾기
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        binding.adminPage.setOnClickListener {
            // 관리자 페이지(단, 로그인 했을 때만 보이게 하기!)
        }

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