package com.busanit.searchrestroom.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
import kotlin.concurrent.Volatile

@Database(entities = [Restroom::class, Member::class, Bookmark::class, Review::class, ReviewImage::class ], version = 2, exportSchema = false)
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

    // getDatabase 메서드 추가
    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "app_database"
        )
          .addMigrations(MIGRATION_1_2) // 마이그레이션 적용
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}