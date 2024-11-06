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
  val location: String, // 한글 주소
  val latitude: Double, // 위도
  val longitude: Double, // 경도
  @ColumnInfo(name = "open_time")
  val openTime: String, // 개방 시간
  @ColumnInfo(name = "full_time")
  val fullTime: Boolean, // 24시간 개방 여부
  val unisex: Boolean, // 남녀공용 여부
  val diaper: Boolean, // 기저귀 교환대 유무
  val accessible: Boolean, // 장애인 화장실 유무
  val memo: String
)