package com.busanit.searchrestroom

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient

class AuthHelper(private val auth: FirebaseAuth, private val googleSignInClient: GoogleSignInClient) {
  private var isFirebaseLoggedIn = false
  private var isKakaoLoggedIn = false
  private var authStateListener: FirebaseAuth.AuthStateListener? = null

  fun setAuthStateListener(onAuthStateChanged: (Boolean) -> Unit) {
    authStateListener = FirebaseAuth.AuthStateListener { auth ->
      val currentUser = auth.currentUser
      isFirebaseLoggedIn = currentUser != null
      checkLoginStatus(onAuthStateChanged)
    }
    auth.addAuthStateListener(authStateListener!!)

    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
      isKakaoLoggedIn = (error == null && tokenInfo != null)
      checkLoginStatus(onAuthStateChanged)
    }
  }

  private fun checkLoginStatus(onAuthStateChanged: (Boolean) -> Unit) {
    // Firebase와 카카오 로그인 상태를 OR 연산하여 로그인 여부를 결정
    val isLoggedIn = isFirebaseLoggedIn || isKakaoLoggedIn
    onAuthStateChanged(isLoggedIn)
  }



  fun removeAuthStateListener() {
    authStateListener?.let { auth.removeAuthStateListener(it) }
  }

  fun isLoggedIn(): Boolean {
    var loggedIn = false
    // Firebase 로그인 상태
    isFirebaseLoggedIn = auth.currentUser != null

    // 카카오 로그인 상태 확인
    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
      isKakaoLoggedIn = error == null && tokenInfo != null
    }

    loggedIn = isFirebaseLoggedIn || isKakaoLoggedIn
    return loggedIn
  }

  fun logoutKakao() {
    UserApiClient.instance.logout { error ->
      if (error != null) {
        Log.e("authHelper", "카카오 로그아웃 실패: $error")
      } else {
        Log.i("authHelper", "카카오 로그아웃 성공")
        isKakaoLoggedIn = false
      }
    }
  }

  fun logoutGmail() {
    googleSignInClient.signOut().addOnCompleteListener {
      Log.i("authHelper", "구글 로그아웃 성공")
      isFirebaseLoggedIn = false
    }
  }

  fun logoutFirebase() {
    auth.signOut()
    isFirebaseLoggedIn = false
  }

  fun getUserEmail(): String? {
    val currentUser = auth.currentUser
    Log.i("authHelper", "getUserEmail: ${currentUser?.email}")
    return currentUser?.email
  }

}