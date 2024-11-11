package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.busanit.searchrestroom.database.Member

@Dao
interface MemberDao {
  @Query("select * from member where email = :email")
  suspend fun getMemberByEmail(email: String): Member?

  @Query("select * from member")
  suspend fun getAll(): List<Member>

  @Query("select * from member where member_id = :id")
  suspend fun getMemberById(id: Int): Member

  @Insert
  suspend fun insert(vararg member: Member)

  @Delete
  suspend fun delete(member: Member)

  // 닉네임 업데이트
  @Query("UPDATE member SET nickname = :newNickname WHERE member_id = :memberId")
  fun updateNickname(memberId: Int, newNickname: String)

  // 비밀번호 업데이트
  @Query("UPDATE member SET password = :newPassword WHERE member_id = :memberId")
  fun updatePassword(memberId: Int, newPassword: String)

}