package com.busanit.searchrestroom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Member (
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "member_id")
  val memberId: Int,
  val nickname: String,
  val password: String,
  @ColumnInfo(name = "profile_pic")
  val profilePic: String,
  @ColumnInfo(name = "reg_time")
  val regTime: Date = Date(System.currentTimeMillis()),
  @ColumnInfo(name = "update_time")
  val updateTime: Date = Date(System.currentTimeMillis()),
  val social: Boolean = false,
  val admin: Boolean = false
)