package com.busanit.searchrestroom

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.EditInfoActivity
import com.busanit.searchrestroom.FavoriteActivity
import com.busanit.searchrestroom.MainActivity
import com.busanit.searchrestroom.MyReviewActivity
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.databinding.ActivityMypageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        updateUI()

        // 각 버튼의 클릭 리스너 설정
        binding.myReview.setOnClickListener {
            startActivity(Intent(this, MyReviewActivity::class.java))
        }

        binding.myRestroom.setOnClickListener {
            // 등록한 화장실 화면으로 이동
        }

        binding.myFavorite.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        binding.adminPage.setOnClickListener {
            if (isLoggedIn() && sharedPreferences.getString("userRole", "") == "USER") {
                // 관리자 페이지 이동 (관리자로 로그인한 경우에만 보이게 함)
//                startActivity(Intent(this, AdminPageActivity::class.java))
            }
        }

        binding.logout.setOnClickListener {
            if (isLoggedIn()) {
                logout()  // 로그아웃 처리
            } else {
                showToast("로그인 화면으로 이동합니다.")
//                startActivity(Intent(this, LoginActivity::class.java))  // 로그인 화면으로 이동
            }
        }

        binding.editInfo.setOnClickListener {
            if (isLoggedIn()) {
                startActivity(Intent(this, EditInfoActivity::class.java))
            } else {
                showToast("로그인이 필요합니다.")
//                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding.editIcon.setOnClickListener {
            if (isLoggedIn()) {
                startActivity(Intent(this, EditInfoActivity::class.java))
            } else {
                showToast("로그인이 필요합니다.")
//                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding.deleteAccount.setOnClickListener {
            if (isLoggedIn()) {
                // 회원 탈퇴 처리
                deleteAccount()
            } else {
//                startActivity(Intent(this, SignupActivity::class.java))  // 회원가입 화면으로 이동
            }
        }

        // BottomNavigationView 설정
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.my_favorite -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    true
                }
                R.id.activity_mypage -> {
                    // 현재 페이지 유지(지금이 마이페이지)
                    true
                }
                else -> false
            }
        }
    }

    private fun updateUI() {
        val isAdmin = sharedPreferences.getString("userRole", "") == "ADMIN"
        val isLoggedIn = isLoggedIn()

        binding.logout.text = if (isLoggedIn) "로그아웃" else "로그인"
        binding.deleteAccount.text = if (isLoggedIn) "회원탈퇴" else "회원가입"

        binding.adminPage.visibility = if (isLoggedIn && isAdmin) View.VISIBLE else View.GONE
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }
        updateUI()
    }

    private fun deleteAccount() {
        // 회원탈퇴 로직 구현
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
