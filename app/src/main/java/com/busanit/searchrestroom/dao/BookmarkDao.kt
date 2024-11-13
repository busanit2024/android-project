package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.searchrestroom.database.Bookmark
@Dao
interface BookmarkDao {
  @Query("select * from bookmark")
  suspend fun getAll(): List<Bookmark>

  @Query("select * from bookmark where bookmark_id = :id")
  suspend fun getBookmarkById(id: Int): Bookmark

  @Query("select * from bookmark where member_id = :memberId")
  suspend fun getBookmarkByMemberId(memberId: Int): List<Bookmark>

  @Query("SELECT * FROM bookmark WHERE restroom_id = :restroomId AND member_id = :memberId LIMIT 1")
  suspend fun getBookmarkByRestroomIdAndMemberId(restroomId: Int, memberId: Int): Bookmark?

  @Insert
  suspend fun insert(vararg bookmark: Bookmark)

  @Delete
  suspend fun delete(bookmark: Bookmark)
}