package com.busanit.teamproject.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.teamproject.database.ReviewImage

@Dao
interface ReviewImageDao {
    @Query("select * from review_image")
    fun getAll(): List<ReviewImage>

    @Query("select * from review_image where review_id = :id")
    fun getReviewImageById(id: Int): ReviewImage


    @Query("select * from review_image where image_id = :id")
    fun getReviewImageByImageId(id: Int): ReviewImage

    @Insert
    fun insert(vararg reviewImage: ReviewImage)

    @Delete
    fun delete(reviewImage: ReviewImage)
}