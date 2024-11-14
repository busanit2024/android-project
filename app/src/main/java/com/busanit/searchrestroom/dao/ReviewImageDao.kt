package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.searchrestroom.database.ReviewImage

@Dao
interface ReviewImageDao {
  @Query("select * from review_image")
  fun getAll(): List<ReviewImage>

  @Query("select * from review_image where review_id = :id")
  fun getReviewImageById(id: Int): List<ReviewImage>
  // List<ReviewImage>로 바뀌었습니다!
  // - 리뷰 이미지를 하나만 한다면 몰라도 여러 개 넣을 수도 있어서 List 걸었습니다.


  @Query("select * from review_image where image_id = :id")
  fun getReviewImageByImageId(id: Int): ReviewImage

  @Insert
  fun insert(vararg reviewImage: ReviewImage)

  @Delete
  fun delete(reviewImage: ReviewImage)
}