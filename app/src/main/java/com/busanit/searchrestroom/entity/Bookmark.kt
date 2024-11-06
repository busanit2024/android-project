package com.busanit.searchrestroom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "bookmark",
  foreignKeys = [
    ForeignKey(
      entity = Restroom::class,
      parentColumns = ["restroom_id"],
      childColumns = ["restroom_id"],
      onDelete = ForeignKey.SET_NULL),
  ForeignKey(
    entity = Member::class,
    parentColumns = ["member_id"],
    childColumns = ["member_id"],
    onDelete = ForeignKey.CASCADE)
  ]
)
data class Bookmark(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "bookmark_id")
  val bookmarkId: Int,
  @ColumnInfo(name = "restroom_id")
  val restroomId: Int,
  @ColumnInfo(name = "member_id")
  val memberId: Int
)