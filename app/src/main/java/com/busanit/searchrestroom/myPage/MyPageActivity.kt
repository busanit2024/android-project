package com.busanit.searchrestroom.myPage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.dao.MemberDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Member
import com.busanit.searchrestroom.admin.AdminActivity
import com.busanit.searchrestroom.mainPage.MainActivity
import com.busanit.searchrestroom.databinding.ActivityMypageBinding
import com.busanit.searchrestroom.member.LoginActivity
import com.busanit.searchrestroom.member.RegisterActivity
import com.busanit.searchrestroom.member.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var memberDao: MemberDao
    private var currentMember: Member? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val db = AppDatabase.getDatabase(applicationContext)
        memberDao = db!!.memberDao()

        // 사용자 정보 불러오기
        loadUserInfo()

        // 각 버튼의 클릭 리스너 설정
        binding.myReview.setOnClickListener {
            startActivity(Intent(this, MyReviewActivity::class.java))
        }

        binding.myFavorite.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        if (AuthHelper.isLoggedIn() && AuthHelper.isAdmin()) {
            binding.adminPage.visibility = View.VISIBLE
        } else {
            binding.adminPage.visibility = View.GONE
        }

        binding.adminPage.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            if (AuthHelper.isLoggedIn() && sharedPreferences.getBoolean("admin", false)) {
                // 관리자 페이지 이동 (관리자로 로그인한 경우에만 보이게 함)
                startActivity(Intent(this, AdminActivity::class.java))
            }
        }

        binding.logout.setOnClickListener {
            if (AuthHelper.isLoggedIn()) {
                logout()  // 로그아웃 처리
                showToast("로그아웃 되었습니다.")
            } else {
                showToast("로그인 화면으로 이동합니다.")
                startActivity(Intent(this, LoginActivity::class.java))  // 로그인 화면으로 이동
            }
        }

        binding.editIcon.setOnClickListener {
            if (AuthHelper.isLoggedIn()) {
                startActivity(Intent(this, EditInfoActivity::class.java))
            } else {
                showToast("로그인이 필요합니다.")
                // 로그인 화면으로 이동
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding.deleteAccount.setOnClickListener {
            if (AuthHelper.isLoggedIn()) {
                // 회원 탈퇴 처리
                deleteAccount()
            } else {
                startActivity(Intent(this, RegisterActivity::class.java))  // 회원가입 화면으로 이동
            }
        }
    }

    private fun loadUserInfo() {
        val email = sharedPreferences.getString("email", null)

        // DB에서 사용자 정보를 로드(코루틴 활용)
        if (email != null) {
            CoroutineScope(Dispatchers.IO).launch {
                currentMember = memberDao.getMemberByEmail(email)
                withContext(Dispatchers.Main) {
                    if (currentMember != null) {
                        binding.username.text = currentMember?.nickname ?: ""
                        binding.email.text = currentMember?.email ?: ""
                        updateUI()
                    } else {
                        showToast("사용자 정보를 불러오는 데 실패했습니다.")
                    }
                }
            }
        } else {
            showToast("로그인 정보가 없습니다.")
        }

    }

    private fun updateUI() {
        val isAdmin = AuthHelper.isAdmin()
        val isLoggedIn = AuthHelper.isLoggedIn()

        binding.username.text = if (isLoggedIn) currentMember?.nickname ?: "" else "Unknown"    // 닉네임
        binding.email.text = if (isLoggedIn) currentMember?.email ?: "" else "Unknown@email.com"    // 이메일
        binding.logout.text = if (isLoggedIn) "로그아웃" else "로그인"
        binding.deleteAccount.text = if (isLoggedIn) "회원탈퇴" else "회원가입"

        binding.adminPage.visibility = if (isLoggedIn && isAdmin) View.VISIBLE else View.GONE
    }

    private fun handelLoginSuccess(email: String) {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", true)
            putString("email", email)
            apply()
        }

        // 로그인 성공 후 사용자 정보를 불러오기
        loadUserInfo()
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun logout() {
        AuthHelper.logout()
        updateUI()
    }

    private fun deleteAccount() {
        // 회원탈퇴
        if (currentMember != null) {
            // 확인용 다이얼로그
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원 탈퇴")
            builder.setMessage("정말로 탈퇴하시겠습니까?")

            // 확인 버튼
            builder.setPositiveButton("확인") { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    memberDao.delete(currentMember!!) // 회원 정보 삭제
                    withContext(Dispatchers.Main) {
                        showToast("회원 탈퇴가 완료되었습니다.")
                        AuthHelper.logout() // 로그아웃 처리
                        startActivity(Intent(this@MyPageActivity, LoginActivity::class.java))   // 로그인 화면으로 이동
                        finish()
                    }
                }
            }

            // 취소 버튼
            builder.setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            // 다이얼로그 표시
            val alertDialog = builder.create()
            alertDialog.show()

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


