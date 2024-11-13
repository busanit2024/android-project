package com.busanit.searchrestroom.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.database.ReviewFilterOption
import com.busanit.searchrestroom.reviewReg.FilterOptionState

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
  fun insert(vararg review: Review) : List<Long>

  @Delete
  fun delete(review: Review)

  @Insert
  fun insertReviewFilterOptions(options: List<ReviewFilterOption>) // ReviewFilterOption 삽입

  @Transaction
  @Query("""
    SELECT review.review_id, review.restroom_id, review.member_id, review.content, review.reg_time, 
           member.nickname 
    FROM review 
    JOIN member ON review.member_id = member.member_id
    LEFT JOIN review_filter_option ON review.review_id = review_filter_option.review_id
    WHERE review.restroom_id = :restroomId 
    ORDER BY review.reg_time DESC 
    LIMIT 3
""")
  fun getLatestReviewsWithFilterByRestroomId(restroomId: Int): List<ReviewWithFilter>

  data class ReviewWithFilter(
    @ColumnInfo(name = "review_id")
    var reviewId: Int,

    @ColumnInfo(name = "restroom_id")
    var restroomId: Int?,

    @ColumnInfo(name = "member_id")
    var memberId: Int?,

    var content: String?,

    @ColumnInfo(name = "reg_time")
    var regTime: String?,

    @ColumnInfo(name = "nickname")
    var nickname: String?

  )

  @Query("SELECT * FROM review_filter_option WHERE review_id = :reviewId")
  fun getFilterOptionsForReview(reviewId: Int): List<ReviewFilterOption>
}
