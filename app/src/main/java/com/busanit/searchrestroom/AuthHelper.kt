package com.busanit.searchrestroom

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK

object AuthHelper {
  private lateinit var preferences: SharedPreferences
  private lateinit var googleSignInClient: GoogleSignInClient
  private lateinit var auth: FirebaseAuth



  fun initialize(context: Context, googleSignInClient: GoogleSignInClient) {
    preferences = context.applicationContext.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    auth = FirebaseAuth.getInstance()
    this.googleSignInClient = googleSignInClient
  }


  fun isLoggedIn(): Boolean {
    return preferences.getInt("member_id", -1) != -1
  }

  fun logoutKakao() {
    UserApiClient.instance.logout { error ->
      if (error != null) {
        Log.e("authHelper", "카카오 로그아웃 실패: $error")
      } else {
        Log.i("authHelper", "카카오 로그아웃 성공")
      }
    }
  }

  fun logoutGmail() {
    googleSignInClient.signOut().addOnCompleteListener {
      Log.i("authHelper", "구글 로그아웃 성공")

    }
  }

  fun logoutFirebase() {
    auth.signOut()
    Log.i("authHelper", "파이어베이스 로그아웃 성공")
  }

  fun logoutNaver() {
    NaverIdLoginSDK.logout()
    Log.i("authHelper", "네이버 로그아웃 성공")
  }

  fun logout() {
    logoutFirebase()
    logoutKakao()
    logoutGmail()
    logoutNaver()
    preferences.edit().apply {
      remove("member_id")
      remove("email")
      remove("nickname")
      apply()
    }
    Log.i("authHelper", "로그아웃 완료")
  }

  fun saveUserInfoToPreferences(memberId: Int, email: String, nickname: String) {
    preferences.edit().apply {
      putInt("member_id", memberId)
      putString("email", email)
      putString("nickname", nickname)
      apply()
    }
  }

  fun getMemberId() : Int {
    return preferences.getInt("member_id", -1)
  }

  fun getEmail() : String {
    return preferences.getString("email", "") ?: ""
  }

  fun getNickname() : String {
    return preferences.getString("nickname", "") ?: ""
  }

  fun isAdmin() : Boolean {
    return preferences.getBoolean("admin", false)
  }

}