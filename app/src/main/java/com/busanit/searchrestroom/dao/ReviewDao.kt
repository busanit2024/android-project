package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.review.ReviewWithMemberAndFilter

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

  @Query("DELETE FROM review WHERE review_id = :reviewId")
  suspend fun deleteReviewById(reviewId: Int)

  @Update
  fun update(review: Review)

  @Query("SELECT * FROM review WHERE restroom_id = :restroomId ORDER BY review_id DESC LIMIT 3")
  fun getLatestReviewsByRestroomId(restroomId: Int): List<Review>

  @Query("SELECT * FROM review WHERE restroom_id = :restroomId")
  fun getAllReviewsByRestroomId(restroomId: Int): List<Review>

  @Query("""
        SELECT 
            r.review_id as reviewId,
            r.restroom_id as restroomId,
            r.member_id as memberId,
            m.nickname as nickname,
            r.content as reviewText,
            r.reg_time as regDate,
            r.toilet_paper_option as toiletPaperOption,
            r.how_many_option as howManyOption,
            r.cleanliness_option as cleanlinessOption
        FROM review r
        INNER JOIN member m ON r.member_id = m.member_id
        WHERE r.restroom_id = :restroomId
        ORDER BY r.reg_time DESC
    """)
  suspend fun getReviewsWithMember(restroomId: Int): List<ReviewWithMemberAndFilter>

  // 최근 3개의 리뷰만 조회
  @Query("""
        SELECT 
            r.review_id as reviewId,
            r.restroom_id as restroomId,
            r.member_id as memberId,
            m.nickname as nickname,
            r.content as reviewText,
            r.reg_time as regDate,
            r.toilet_paper_option as toiletPaperOption,
            r.how_many_option as howManyOption,
            r.cleanliness_option as cleanlinessOption
        FROM review r
        INNER JOIN member m ON r.member_id = m.member_id
        WHERE r.restroom_id = :restroomId
        ORDER BY r.reg_time DESC
        LIMIT 3
    """)
  suspend fun getLatestReviewsWithMember(restroomId: Int): List<ReviewWithMemberAndFilter>


}
