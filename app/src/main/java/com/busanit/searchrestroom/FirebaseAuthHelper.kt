package com.busanit.searchrestroom

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthHelper(private val auth: FirebaseAuth) {
  private var isLoggedIn = false
  private var authStateListener: FirebaseAuth.AuthStateListener? = null

  fun setAuthStateListener(onAuthStateChanged: (Boolean) -> Unit) {
    authStateListener = FirebaseAuth.AuthStateListener { auth ->
      val currentUser = auth.currentUser
      isLoggedIn = currentUser != null
      onAuthStateChanged(isLoggedIn)
    }
    auth.addAuthStateListener(authStateListener!!)
  }

  fun removeAuthStateListener() {
    authStateListener?.let { auth.removeAuthStateListener(it) }
  }

  fun isLoggedIn(): Boolean {
    return isLoggedIn
  }

}