package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.searchrestroom.entity.Bookmark

@Dao
interface BookmarkDao {
  @Query("select * from bookmark")
  fun getAll(): List<Bookmark>

  @Query("select * from bookmark where bookmark_id = :id")
  fun getBookmarkById(id: Int): Bookmark

  @Query("select * from bookmark where member_id = :memberId")
  fun getBookmarkByMemberId(memberId: Int): List<Bookmark>

  @Insert
  fun insert(vararg bookmark: Bookmark)

  @Delete
  fun delete(bookmark: Bookmark)

}