package com.busanit.searchrestroom.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.busanit.searchrestroom.dao.BookmarkDao
import com.busanit.searchrestroom.dao.DeleteRequestDao
import com.busanit.searchrestroom.dao.MemberDao
import com.busanit.searchrestroom.dao.RestroomDao
import com.busanit.searchrestroom.dao.ReviewDao
import com.busanit.searchrestroom.dao.ReviewImageDao
import com.busanit.searchrestroom.database.Bookmark
import com.busanit.searchrestroom.database.Member
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.database.ReviewImage

@Database(entities = [Restroom::class, Member::class, Bookmark::class, Review::class, ReviewImage::class, DeleteRequest::class ], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun restroomDao(): RestroomDao
  abstract fun memberDao(): MemberDao
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun reviewDao(): ReviewDao
  abstract fun reviewImageDao(): ReviewImageDao
  abstract fun DeleteRequestDao() : DeleteRequestDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    @JvmField
    val MIGRATION_2_3 : Migration = object : Migration(2, 3) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("create table if not exists delete_request (request_id integer primary key autoincrement not null, member_id integer null, request_message text null, reg_time text null DEFAULT CURRENT_TIMESTAMP, restroom_id integer null, foreign key(member_id) references member(member_id) on delete set null, foreign key(restroom_id) references restroom(restroom_id) on delete cascade)")
        Log.d("AppDatabase", "migrate")
      }
    }


    // getDatabase 메서드 추가
    fun getDatabase(context: Context): AppDatabase? {
      if(INSTANCE == null) {
        Log.d("AppDatabase", "instance null")
        synchronized(AppDatabase::class) {
          INSTANCE = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "search-restroom"
          ).addMigrations(MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()
        }
      }
      else {
        Log.d("AppDatabase", "instance not null")
      }
      return INSTANCE
    }
  }
}