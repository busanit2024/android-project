package com.busanit.searchrestroom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "review_image",
  foreignKeys = [
    ForeignKey(
      entity = Review::class,
      parentColumns = ["review_id"],
      childColumns = ["review_id"],
      onDelete = ForeignKey.CASCADE
    )]
)
data class ReviewImage (
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "image_id")
  val imageId: Int,
  @ColumnInfo(name = "review_id")
  val reviewId: Int,
)