package com.busanit.searchrestroom.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "restroom")
data class Restroom (
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
) : Parcelable

@Entity(tableName = "member")
data class Member (
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "member_id")
  val memberId: Int = 0,
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
  @ColumnInfo(name = "review_id")
  @PrimaryKey(autoGenerate = true)
  var reviewId: Int,
    @ColumnInfo(name = "restroom_id")
  var restroomId: Int?,
    @ColumnInfo(name = "member_id")
  var memberId: Int?,
  var content: String?,
    @ColumnInfo(name = "reg_time", defaultValue = "CURRENT_TIMESTAMP")
  var regTime: String?,
    @ColumnInfo(name = "update_time", defaultValue = "CURRENT_TIMESTAMP")
  var updateTime: String?,
    @ColumnInfo(name = "toilet_paper_option")
  var toiletPaperOption: Int? = null,  // 1: 휴지 있음(디폴트), 2: 휴지 없음
    @ColumnInfo(name="how_many_option")
  var howManyOption: Int? = null,      // 1: 1칸(디폴트), 2: 2칸, 3: 3칸, 4: 4칸 이상
    @ColumnInfo(name="cleanliness_option")
  var cleanlinessOption: Int? = null   // 1: 깨끗함(디폴트), 2: 무난함, 3: 더러움
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

@Entity(
  tableName = "delete_request",
  foreignKeys = [
    ForeignKey(
      entity = Restroom::class,
      parentColumns = ["restroom_id"],
      childColumns = ["restroom_id"],
      onDelete = ForeignKey.CASCADE
    ),
  ForeignKey(
    entity = Member::class,
    parentColumns = ["member_id"],
    childColumns = ["member_id"],
    onDelete = ForeignKey.SET_NULL
  )
  ]
)
data class DeleteRequest(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "request_id")
  val requestId: Int,
  @ColumnInfo(name = "restroom_id")
  val restroomId: Int?,
  @ColumnInfo(name = "member_id")
  val memberId: Int?,
  @ColumnInfo(name = "request_message")
  val requestMessage: String?,
  @ColumnInfo(name = "reg_time", defaultValue = "CURRENT_TIMESTAMP")
  val regTime: String?
)

data class DeleteRequestWithRestroom(
  @Embedded val deleteRequest: DeleteRequest,
  @Relation(
    parentColumn = "restroom_id",
    entityColumn = "restroom_id"
  )
  val restroom: Restroom?
)



