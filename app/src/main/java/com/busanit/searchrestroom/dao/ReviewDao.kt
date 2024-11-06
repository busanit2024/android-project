package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.searchrestroom.entity.Review

@Dao
interface ReviewDao {
  @Query("select * from review")
  fun getAll(): List<Review>

  @Query("select * from review where review_id = :id")
  fun getReviewById(id: Int): Review

  @Query("select * from review where member_id = :memberId")
  fun getReviewByMemberId(memberId: Int): List<Review>

  @Query("select * from review where restroom_id = :restroomId")
  fun getReviewByRestroomId(restroomId: Int): List<Review>

  @Insert
  fun insert(vararg review: Review)

  @Delete
  fun delete(review: Review)

}