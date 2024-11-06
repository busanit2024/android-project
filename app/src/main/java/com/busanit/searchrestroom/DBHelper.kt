package com.busanit.searchrestroom

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "searchRestroom.db", null, 1) {
  override fun onCreate(db: SQLiteDatabase?) {
    db?.execSQL("create table restroom(" +
        "restroom_id integer primary key autoincrement," +
        "restroom_name text," +
        "location text," + // 주소
        "open_time text," + // 개방시간
        "full_time boolean," + // 24시간 여부
        "unisex boolean," + // 남여공용 여부
        "diaper boolean," + // 기저귀교환대 유무
        "accessible boolean," + // 장애인화장실 유무
        "memo text)")

    db?.execSQL("create table member(" +
        "member_id integer primary key autoincrement," +
        "nickname text not null," +
        "password text not null," +
        "profile_pic text," +
        "reg_time timestamp default current_timestamp," +
        "update_time timestamp default current_timestamp," +
        "social boolean default false," + // 소셜 로그인 여부
        "admin boolean default false)") // 관리자 여부

    db?.execSQL("create table review(" +
        "review_id integer primary key autoincrement," +
        "restroom_id integer," +
        "member_id integer," +
        "content text," +
        "reg_time timestamp default current_timestamp," +
        "update_time timestamp default current_timestamp," +
        "foreign key(restroom_id) references restroom(restroom_id))")

    db?.execSQL("create table review_image(" +
        "image_id integer primary key autoincrement," +
        "review_id integer," +
        "file_name text," +
        "foreign key(review_id) references review(review_id))")

    db?.execSQL("create table bookmark(" +
        "bookmark_id integer primary key autoincrement," +
        "restroom_id integer," +
        "member_id integer," +
        "foreign key(restroom_id) references restroom(restroom_id)," +
        "foreign key(member_id) references member(member_id))")

  }

  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    db?.execSQL("drop table if exists restroom")
    db?.execSQL("drop table if exists member")
    db?.execSQL("drop table if exists review")
    db?.execSQL("drop table if exists review_image")
    db?.execSQL("drop table if exists bookmark")
    onCreate(db)

  }
}