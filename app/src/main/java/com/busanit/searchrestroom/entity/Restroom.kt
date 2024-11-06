package com.busanit.searchrestroom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Restroom (
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "restroom_id")
  val restroomId: Int,
  @ColumnInfo(name = "restroom_name")
  val restroomName: String,
  val location: String,
  @ColumnInfo(name = "open_time")
  val openTime: String,
  @ColumnInfo(name = "full_time")
  val fullTime: Boolean,
  val unisex: Boolean,
  val diaper: Boolean,
  val accessible: Boolean,
  val memo: String
)