package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.searchrestroom.database.Member

@Dao
interface MemberDao {
    @Query("select * from member")
    fun getAll(): List<Member>

    @Query("select * from member where member_id = :id")
    fun getMemberById(id: Int): Member

    @Insert
    fun insert(vararg member: Member)

    @Delete
    fun delete(member: Member)

}