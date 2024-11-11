package com.busanit.searchrestroom

import com.busanit.searchrestroom.dao.MemberDao
import com.busanit.searchrestroom.database.Member
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class UserRepository(private val memberDao: MemberDao) {

    // 이메일 중복 체크 기능
    fun checkIfEmailExists(email: String, callback: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods ?: emptyList()
                    callback(signInMethods.isNotEmpty())    // 이메일이 존재하면 true 반환
                } else {
                    callback(false) // 오류 발생 시 false 반환
                }
            }
    }

    // 일반 회원가입 : Firebase와 로컬 DB에 저장
    fun registerUser(email: String, password: String, nickname: String, onComplete: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    if (firebaseUser != null) {
                        // Firebase에서 UID 가져오기
                        val member = Member(
                            email = email,
                            password = password,
                            nickname = nickname,
                            profilePic = null,
                            regTime = Date().toString(),
                            updateTime = null,
                            social = false,
                            admin = false
                        )
                        GlobalScope.launch {
                            memberDao.insert(member)    // 로컬 DB 저장
                        }
                        onComplete(true, null)  // 회원가입 성공
                    } else {
                        onComplete(false, "회원 UID를 가져올 수 없습니다.")
                    }
                } else {
                    onComplete(false, task.exception?.message)  // 회원가입 실패 & 에러 메세지 포함
                }
            }
    }

    private fun Member(
        email: String,
        password: String,
        nickname: String,
        profilePic: Nothing?,
        regTime: String,
        updateTime: Nothing?,
        social: Boolean,
        admin: Boolean
    ): Member {

    }

    // 소셜 회원가입 : Firebase에만 가입
    fun registerSocialUser(email: String, onComplete: (Boolean, String?) -> Unit) {
        // 소셜 회원가입의 경우 Firebase에만 정보 저장 (로컬 DB에 저장 X)
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }
    // 일반 로그인 : Firebase 인증 후 로컬 DB에 존재하는지 확인
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)  // UID가 일치하면 로그인 성공

                } else {
                    onComplete(false, task.exception?.message)  // 로그인 실패
                }
            }
    }
    // 소셜 로그인 : Firebase 인증 후, 로컬 DB에 정보가 없는 경우 저장
    fun loginSocialUser(firebaseUser: FirebaseUser, onComplete: (Boolean, String?) -> Unit) {
        GlobalScope.launch {
            val email = firebaseUser.email ?: ""
            val existingMember = memberDao.getMemberByEmail(email)

            if (existingMember == null) {
                // 로컬 DB에 정보가 없으므로 저장
                val member = Member(
//                    firebaseUid = "",
                    email = email,
                    password = "",  // 소셜 로그인은 비밀번호가 없으므로 빈 문자열로 저장
                    nickname = firebaseUser.displayName ?: "",
                    profilePic = firebaseUser.photoUrl?.toString(),
                    regTime = Date().toString(),
                    updateTime = null,
                    social = true,
                    admin = false
                )
                memberDao.insert(member)
            }
            onComplete(true, null)
        }
    }
}