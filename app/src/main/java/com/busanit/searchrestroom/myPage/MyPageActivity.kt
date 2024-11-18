package com.busanit.searchrestroom.myPage

import android.app.ComponentCaller
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
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
import com.kakao.sdk.user.model.User
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
    private lateinit var userRepository: UserRepository
    private var db: AppDatabase? = null

    companion object {
        private const val EDIT_INFO_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_INFO_REQUEST_CODE && resultCode == RESULT_OK) {
            loadUserInfo()  // 사용자 정보 새롭게 로드
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        db = AppDatabase.getDatabase(applicationContext)
        memberDao = db!!.memberDao()
        userRepository = UserRepository(memberDao, this)

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
                startActivityForResult(Intent(this, EditInfoActivity::class.java), EDIT_INFO_REQUEST_CODE)   // 요청 코드 전달
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

    private fun isLocalFileUri(uri: Uri): Boolean {
        return uri.scheme?.let { it == ContentResolver.SCHEME_FILE || it == ContentResolver.SCHEME_CONTENT } == true
    }

    private fun setProfileImage() {
        val uriString = db!!.memberDao().getProfilePic(AuthHelper.getMemberId())
        if (uriString != null) {
            val uri = Uri.parse(uriString)
            if (isLocalFileUri(uri)) {
                binding.profileImage.setImageURI(uri)
            } else {
                binding.profileImage.setImageResource(R.drawable.profile)
            }
        } else {
            binding.profileImage.setImageResource(R.drawable.profile)
        }
    }

    private fun updateUI() {
        val isAdmin = AuthHelper.isAdmin()
        val isLoggedIn = AuthHelper.isLoggedIn()
        setProfileImage()
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
        if (currentMember != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원 탈퇴")
            builder.setMessage("정말로 탈퇴하시겠습니까?")

            builder.setPositiveButton("확인") { dialog, which ->
                currentMember?.let { member ->
                    when {
                        !member.social -> {
                            // 일반 로그인 사용자
                            showPasswordConfirmationDialog(member.email)
                        }
                        member.email.contains("kakao") -> {
                            // 카카오 로그인 사용자
                            proceedWithDeletion(member.email, socialType = "KAKAO")
                        }
                        member.email.contains("naver") -> {
                            // 네이버 로그인 사용자
                            proceedWithDeletion(member.email, socialType = "NAVER")
                        }
                        else -> {
                            // 구글 로그인 사용자
                            proceedWithDeletion(member.email, isSocial = true)
                        }
                    }
                }
            }

            builder.setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun proceedWithDeletion(email: String, password: String? = null, isSocial: Boolean = false, socialType: String? = null) {
        userRepository.deleteUser(email, password, isSocial, socialType) { success, error ->
            runOnUiThread {
                if (success) {
                    showToast("회원 탈퇴가 완료되었습니다.")
                    AuthHelper.logout()
                    startActivity(Intent(this@MyPageActivity, LoginActivity::class.java))
                    finish()
                } else {
                    showToast("회원 탈퇴 실패: $error")
                }
            }
        }
    }

    private fun showPasswordConfirmationDialog(email: String) {
        val passwordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "비밀번호를 입력하세요"
        }

        AlertDialog.Builder(this)
            .setTitle("비밀번호 확인")
            .setView(passwordInput)
            .setPositiveButton("확인") { dialog, which ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    proceedWithDeletion(email, password)
                } else {
                    showToast("비밀번호를 입력해주세요")
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun proceedWithDeletion(email: String, password: String? = null) {
        userRepository.deleteUser(email, password) { success, error ->
            runOnUiThread {
                if (success) {
                    showToast("회원 탈퇴가 완료되었습니다.")
                    AuthHelper.logout()
                    startActivity(Intent(this@MyPageActivity, LoginActivity::class.java))
                    finish()
                } else {
                    showToast("회원 탈퇴 실패: $error")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}


