package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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

}