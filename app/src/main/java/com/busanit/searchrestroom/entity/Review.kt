package com.busanit.searchrestroom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(
  tableName = "review",
  foreignKeys = [
    ForeignKey(
      entity = Restroom::class,
      parentColumns = ["restroom_id"],
      childColumns = ["restroom_id"],
      onDelete = ForeignKey.CASCADE
), ForeignKey(
      entity = Member::class,
  parentColumns = ["member_id"],
  childColumns = ["member_id"],
  onDelete = ForeignKey.SET_NULL
)]
)
data class Review (
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "review_id")
  val reviewId: Int,
  @ColumnInfo(name = "restroom_id")
  val restroomId: Int,
  @ColumnInfo(name = "member_id")
  val memberId: Int,
  val content: String,
  @ColumnInfo(name = "reg_time")
  val regTime: Date = Date(System.currentTimeMillis()),
  @ColumnInfo(name = "update_time")
  val updateTime: Date = Date(System.currentTimeMillis())
)