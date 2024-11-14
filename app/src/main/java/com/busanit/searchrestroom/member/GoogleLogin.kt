package com.busanit.searchrestroom.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityLoginBinding

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GoogleLogin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var viewModel: LoginViewModel

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(AppDatabase.getDatabase(application)!!.memberDao(), this)
        viewModel = ViewModelProvider(this, LoginViewModelFactory(repository)).get(LoginViewModel::class.java)

        // FirebaseAuth 초기화
        mAuth = Firebase.auth

        // GoogleSignInOptions 구성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // GoogleSignInClient 초기화
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googlelogin()  // 구글 로그인 시작
    }

    private fun googlelogin() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // 구글 로그인 후 Firebase 인증 처리
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                // 로그인 실패 처리
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공
                        Log.d(TAG, "signInWithCredential:success")
                        Toast.makeText(applicationContext, "성공했습니다.(구글 로그인)", Toast.LENGTH_LONG).show()
                        val user: FirebaseUser? = mAuth.currentUser
                        viewModel.loginGoogleUser(user)
                        val intent = Intent(this, com.busanit.searchrestroom.mainPage.MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // 로그인 실패
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(applicationContext, "실패했습니다.(구글 로그인)", Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
        }
    }
}