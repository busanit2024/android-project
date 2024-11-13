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

@Database(entities = [Restroom::class, Member::class, Bookmark::class, Review::class, ReviewImage::class, ReviewFilterOption::class ], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
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
    val MIGRATION_1_2 : Migration = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        Log.d("test", "migrate")
      }
    }

    @JvmField
    val MIGRATION_2_3 : Migration = object : Migration(2, 3) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE review_filter_option (
            option_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            review_id INTEGER NOT NULL,
            filter_type TEXT CHECK(filter_type IN ('0', '1', '2')) NOT NULL,
            option_name TEXT NOT NULL,
            FOREIGN KEY (review_id) REFERENCES review (review_id)
                ON DELETE CASCADE
                ON UPDATE NO ACTION )
    """)
      }
    }


    // getDatabase 메서드 추가
    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "search-restroom"
        )
          .addMigrations(MIGRATION_1_2) // 마이그레이션 적용
          .addMigrations(MIGRATION_2_3)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}