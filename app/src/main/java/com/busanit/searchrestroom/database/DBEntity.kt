package com.busanit.searchrestroom.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "restroom")
data class Restroom(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "restroom_id")
    val restroomId: Int,
    @ColumnInfo(name = "restroom_name")
    val restroomName: String?,
    val location: String?, // 한글 주소
    val latitude: Double?, // 위도
    val longitude: Double?, // 경도
    @ColumnInfo(name = "open_time")
    val openTime: String?, // 개방 시간
    @ColumnInfo(name = "full_time")
    val fullTime: Boolean?, // 24시간 개방 여부
    val unisex: Boolean?, // 남녀공용 여부
    val diaper: Boolean?, // 기저귀 교환대 유무
    val accessible: Boolean?, // 장애인 화장실 유무
    val memo: String?
)

@Entity(tableName = "member")
data class Member (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "member_id")
    val memberId: Int,
    val email: String,
    val nickname: String?,
    val password: String,
    @ColumnInfo(name = "profile_pic")
    val profilePic: String?,
    @ColumnInfo(name = "reg_time", defaultValue = "CURRENT_TIMESTAMP")
    val regTime: String?,
    @ColumnInfo(name = "update_time", defaultValue = "CURRENT_TIMESTAMP")
    val updateTime: String?,
    val social: Boolean = false,
    val admin: Boolean = false
)

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
    val restroomId: Int?,
    @ColumnInfo(name = "member_id")
    val memberId: Int?,
    val content: String?,
    @ColumnInfo(name = "reg_time", defaultValue = "CURRENT_TIMESTAMP")
    val regTime: String?,
    @ColumnInfo(name = "update_time", defaultValue = "CURRENT_TIMESTAMP")
    val updateTime: String?
)

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
    val reviewId: Int?,
    @ColumnInfo(name = "file_name")
    val fileName: String?
)

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
    val restroomId: Int?,
    @ColumnInfo(name = "member_id")
    val memberId: Int?
)