package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.searchrestroom.database.Restroom

@Dao
interface RestroomDao {
  @Query("select * from restroom")
  fun getAll(): List<Restroom>

  @Query("select * from restroom where restroom_id = :id")
  fun getRestroomById(id: Int): Restroom

  @Query("SELECT * FROM restroom WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLong AND :maxLong")
  fun getRestroomsWithinArea(minLat: Double, maxLat: Double, minLong: Double, maxLong: Double): List<Restroom>


  @Insert
  fun insert(vararg restroom: Restroom)

  @Delete
  fun delete(restroom: Restroom)

}