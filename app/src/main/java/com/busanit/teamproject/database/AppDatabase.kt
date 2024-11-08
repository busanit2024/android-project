package com.busanit.teamproject.database

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.busanit.teamproject.dao.BookmarkDao
import com.busanit.teamproject.dao.MemberDao
import com.busanit.teamproject.dao.RestroomDao
import com.busanit.teamproject.dao.ReviewDao
import com.busanit.teamproject.dao.ReviewImageDao
import com.busanit.teamproject.database.Bookmark
import com.busanit.teamproject.database.Member
import com.busanit.teamproject.database.Restroom
import com.busanit.teamproject.database.Review
import com.busanit.teamproject.database.ReviewImage

@Database(entities = [Restroom::class, Member::class, Bookmark::class, Review::class, ReviewImage::class ], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restroomDao(): RestroomDao
    abstract fun memberDao(): MemberDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun reviewDao(): ReviewDao
    abstract fun reviewImageDao(): ReviewImageDao

    companion object {
        @JvmField
        val MIGRATION_1_2 : Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("test", "migrate")
            }
        }
    }

}