package com.busanit.searchrestroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.busanit.searchrestroom.database.DeleteRequest
import com.busanit.searchrestroom.database.DeleteRequestWithRestroom

@Dao
interface DeleteRequestDao {
  @Query("select * from delete_request")
  fun getAll(): List<DeleteRequest>

  @Transaction
  @Query("select * from delete_request")
  suspend fun getDeleteRequestWithRestroom(): List<DeleteRequestWithRestroom>

  @Query("select * from delete_request where request_id = :id")
  fun getDeleteRequestById(id: Int): DeleteRequest

  @Query("select * from delete_request where member_id = :memberId")
  fun getDeleteRequestByMemberId(memberId: Int): List<DeleteRequest>

  @Query("select * from delete_request where restroom_id = :restroomId")
  fun getDeleteRequestByRestroomId(restroomId: Int): List<DeleteRequest>

  @Insert
  fun insert(vararg deleteRequest: DeleteRequest)

  @Delete
  fun delete(deleteRequest: DeleteRequest)


}