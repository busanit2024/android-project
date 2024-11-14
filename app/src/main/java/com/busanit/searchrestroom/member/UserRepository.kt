package com.busanit.searchrestroom.member

import android.content.Context
import android.content.SharedPreferences
import com.busanit.searchrestroom.dao.MemberDao
import com.busanit.searchrestroom.database.Member
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class UserRepository(val memberDao: MemberDao, private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

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
    // 로그인 성공 시 member_id를 SharedPreferences에 저장하는 메서드
    private fun saveUserInfoToPreferences(memberId: Int, email: String, nickname: String, admin: Boolean) {
        sharedPreferences.edit().apply {
            putInt("member_id", memberId)
            putString("email", email)
            putString("nickname", nickname)
            putBoolean("admin", admin)
            apply()
        }
    }
    // 일반 로그인 : Firebase 인증 후 로컬 DB에 존재하는지 확인
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 파이어베이스 로그인 성공 후, 로컬 DB에 있는 사용자 정보를 조회하여 member_id를 저장
                    GlobalScope.launch {
                        var member = memberDao.getMemberByEmail(email)
                        if (member == null) {
                            // 로컬 DB에 사용자가 없으면 새롭게 저장
                            member = Member(
                                email = email,
                                password = password,
                                nickname = "Unknown", // 닉네임이 없는 경우 임시로 설정
                                profilePic = null,
                                regTime = Date().toString(),
                                updateTime = null,
                                social = false,
                                admin = false
                            )
                            memberDao.insert(member)
                        }
                        saveUserInfoToPreferences(member.memberId, member.email, member.nickname ?: "", admin = member.admin)  // member_id, email, nickname 저장

                        onComplete(true, null)  // UID가 일치하면 로그인 성공
                    }
                } else {
                    onComplete(false, task.exception?.message)  // 로그인 실패
                }
            }
    }
    // 소셜 로그인 : Firebase 인증 후, 로컬 DB에 정보가 없는 경우 저장
    fun loginGoogleUser(firebaseUser: FirebaseUser?, onComplete: (Boolean, String?) -> Unit) {
        GlobalScope.launch {
            val email = firebaseUser?.email ?: ""
            val existingMember = memberDao.getMemberByEmail(email)
            if (existingMember == null) {
                // 로컬 DB에 정보가 없으므로 저장
                val member = Member(
                    email = email,
                    password = "",  // 소셜 로그인은 비밀번호가 없으므로 빈 문자열로 저장
                    nickname = firebaseUser?.displayName ?: "",
                    profilePic = firebaseUser?.photoUrl?.toString(),
                    regTime = Date().toString(),
                    updateTime = null,
                    social = true,
                    admin = false
                )
                memberDao.insert(member)
                val searchMember = memberDao.getMemberByEmail(email)
                if (searchMember != null) {
                    saveUserInfoToPreferences(searchMember.memberId, email, member.nickname ?: "", member.admin)
                }
            } else {
                saveUserInfoToPreferences(existingMember.memberId, existingMember.email, existingMember.nickname ?: "", existingMember.admin)
            }
            onComplete(true, null)
        }
    }
    fun loginKakaoUser(kakaoUser: User, onComplete: (Boolean, String?) -> Unit) {
        GlobalScope.launch {
            val email = kakaoUser.kakaoAccount?.email ?: ""
            val existingMember = memberDao.getMemberByEmail(email)
            if (existingMember == null) {
                // 로컬 DB에 정보가 없으므로 저장
                val member = Member(
                    email = email,
                    password = "",  // 소셜 로그인은 비밀번호가 없으므로 빈 문자열로 저장
                    nickname = kakaoUser.kakaoAccount?.profile?.nickname ?: "",
                    profilePic = kakaoUser.kakaoAccount?.profile?.profileImageUrl,
                    regTime = Date().toString(),
                    updateTime = null,
                    social = true,
                    admin = false
                )
                memberDao.insert(member)
                val searchMember = memberDao.getMemberByEmail(email)
                if (searchMember != null) {
                    saveUserInfoToPreferences(searchMember.memberId, email, member.nickname ?: "", member.admin)
                }
            } else {
                saveUserInfoToPreferences(existingMember.memberId, existingMember.email, existingMember.nickname ?: "", existingMember.admin)
            }
            onComplete(true, null)
        }
    }

    fun loginNaverUser(loginResponse: LoginResponse, onComplete: (Boolean, String?) -> Unit) {
        GlobalScope.launch {
            val email = getEmailFromNaverApi(loginResponse.accessToken) ?: run {
                onComplete(false, "이메일 정보를 가져올 수 없습니다.")
                return@launch
            }

            val existingMember = memberDao.getMemberByEmail(email)

            if (existingMember == null) {
                // 새로운 사용자일 경우 로컬 DB에 정보 저장
                val member = Member(
                    email = email,
                    password = "", // 소셜 로그인은 비밀번호 없음
                    nickname = "네이버 사용자", // 별도의 닉네임 설정
                    profilePic = null,
                    regTime = Date().toString(),
                    updateTime = null,
                    social = true,
                    admin = false
                )
                memberDao.insert(member)
                saveUserInfoToPreferences(member.memberId, email, member.nickname ?: "")
            } else {
                saveUserInfoToPreferences(existingMember.memberId, existingMember.email, existingMember.nickname ?: "")
            }
            onComplete(true, null)
        }
    }

}