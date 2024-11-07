package com.busanit.teamproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit.teamproject.databinding.ActivityMypageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myReview.setOnClickListener {
            // 나의 리뷰
            val intent = Intent(this, MyReviewActivity::class.java)
            startActivity(intent)
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

        // 회원정보 수정 버튼
        binding.editInfo.setOnClickListener {
            // 회원정보 수정
            val intent = Intent(this, EditInfoActivity::class.java)
            startActivity(intent)
        }

        // 편집 아이콘
        binding.editIcon.setOnClickListener {
            // 회원정보 수정
            val intent = Intent(this, EditInfoActivity::class.java)
            startActivity(intent)
        }

        binding.deleteAccount.setOnClickListener {
            // 회원탈퇴

        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_home -> {
                    // 홈 화면으로 이동
                    val intent = Intent(this, MyPageActivity::class.java)
                    // 지금은 MyPageActivity로 이동하게 되어있지만 나중에 합칠 땐 이걸 MainActivity로 수정해야 함!
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.my_favorite -> {
                    // 즐겨찾기 화면으로 이동
                    val intent = Intent(this, FavoriteActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.activity_mypage -> {
                    // 마이페이지 화면으로 이동
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }
}