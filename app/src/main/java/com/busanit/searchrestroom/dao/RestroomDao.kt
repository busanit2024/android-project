package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.busanit.searchrestroom.entity.Restroom

@Dao
interface RestroomDao {
  @Query("select * from restroom")
  fun getAll(): List<Restroom>

  @Query("select * from restroom where restroom_id = :id")
  fun getRestroomById(id: Int): Restroom

  @Insert
  fun insert(vararg restroom: Restroom)

  @Delete
  fun delete(restroom: Restroom)

}