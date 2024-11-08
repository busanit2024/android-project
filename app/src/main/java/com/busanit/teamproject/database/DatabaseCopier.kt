package com.busanit.teamproject.database

import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import androidx.room.Room
import java.io.File
import java.io.FileOutputStream

object DatabaseCopier {
    private val TAG = DatabaseCopier::class.java.simpleName
    private const val DATABASE_NAME = "search-restroom"
    private var INSTANCE : AppDatabase? = null

    fun getAppDataBase(context: Context): AppDatabase? {
        if(INSTANCE == null) {
            Log.d(TAG, "instance null")
            synchronized(AppDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(AppDatabase.MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build()
            }
        }
        else {
            Log.d(TAG, "instance not null")
        }
        return INSTANCE
    }

    fun copyAttachedDatabase(context: Context) {
        Log.d(TAG, "copyAttachedDatabase")
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        Log.d(TAG, "dbPath: $dbPath")

        if (dbPath.exists()) {
            Log.d(TAG, "dbPath exists")
            val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = PackageInfoCompat.getLongVersionCode(info)

            Log.d(TAG, "version: $version")

            if (version != 1L) {
                Log.d(TAG, "version not match")
                copyDB(context, dbPath)
            }
            return

        } else {
            Log.d(TAG, "dbPath not exists")
            dbPath.parentFile?.mkdirs()
            copyDB(context, dbPath)
        }
    }

    private fun copyDB(context: Context, _dbPath: File) {
        try {
            val inputStream = context.assets.open("databases/$DATABASE_NAME.db")
            val output = FileOutputStream(_dbPath)
            var length: Int

            val buffer = ByteArray(8192)
            while(true) {
                length = inputStream.read(buffer, 0, 8192)
                if(length <= 0) {
                    break
                }
                output.write(buffer, 0, length)
            }

            output.flush()
            output.close()
            inputStream.close()

            Log.d(TAG, "copyDB success")
        }
        catch(e: Exception) {
            Log.d(TAG, "copyDB failed, Exception: $e")
            e.printStackTrace()
        }
    }
}