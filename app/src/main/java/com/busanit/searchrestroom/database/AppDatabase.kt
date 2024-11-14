package com.busanit.searchrestroom.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
import com.busanit.searchrestroom.reviewReg.Converters

@Database(entities = [Restroom::class, Member::class, Bookmark::class, Review::class, ReviewImage::class ], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun restroomDao(): RestroomDao
  abstract fun memberDao(): MemberDao
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun reviewDao(): ReviewDao
  abstract fun reviewImageDao(): ReviewImageDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    @JvmField
    val MIGRATION_3_4 : Migration = object : Migration(3, 4) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE IF NOT EXISTS review_filter_option (
            option_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            review_id INTEGER NOT NULL,
            filter_type TEXT CHECK(filter_type IN ('0', '1', '2')) NOT NULL,
            option_name TEXT NOT NULL,
            FOREIGN KEY (review_id) REFERENCES review (review_id)
                ON DELETE CASCADE
                ON UPDATE NO ACTION )
    """)
        db.execSQL("""create table if not exists delete_request (
          request_id integer primary key autoincrement not null, 
          member_id integer null, request_message text null, 
          reg_time text null DEFAULT CURRENT_TIMESTAMP, 
          restroom_id integer null, 
          foreign key(member_id) references member(member_id) on delete set null, 
          foreign key(restroom_id) references restroom(restroom_id) on delete cascade)
          """)
      }
    }

    @JvmField
    val MIGRATION_4_5 : Migration = object : Migration(3, 4) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Review ADD COLUMN toilet_paper_option INTEGER")
        db.execSQL("ALTER TABLE Review ADD COLUMN how_many_option INTEGER")
        db.execSQL("ALTER TABLE Review ADD COLUMN cleanliness_option INTEGER")
        db.execSQL("DROP TABLE IF EXISTS review_filter_option")
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
          )
            .addMigrations(MIGRATION_4_5)
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