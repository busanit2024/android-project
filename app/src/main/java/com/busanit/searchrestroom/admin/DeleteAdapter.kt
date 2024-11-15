package com.busanit.searchrestroom.admin

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.DeleteRequest
import com.busanit.searchrestroom.database.DeleteRequestWithRestroom
import com.busanit.searchrestroom.restroomDetail.RestroomDetailActivity

class DeleteAdapter(
    private val deleteList: MutableList<DeleteRequestWithRestroom>
) : RecyclerView.Adapter<DeleteAdapter.DeleteViewHolder>() {

    private val sortedList = deleteList.sortedBy { it.restroom?.restroomId }

    inner class DeleteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val buildingAddress: TextView = view.findViewById(R.id.building_address)
        val deleteRequestDate: TextView = view.findViewById(R.id.request_date)
        val deleteRequestReason: TextView = view.findViewById(R.id.request_reason)
        val detailButton: TextView = view.findViewById(R.id.detailButton)
        val cancelButton: TextView = view.findViewById(R.id.cancelButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_delete, parent, false)
        return DeleteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeleteViewHolder, position: Int) {
        val deleteItem = sortedList[position]
        holder.buildingName.text = deleteItem.restroom?.restroomName
        holder.buildingAddress.text = deleteItem.restroom?.location
        holder.deleteRequestDate.text = deleteItem.deleteRequest.regTime.toString()
        holder.deleteRequestReason.text = deleteItem.deleteRequest.requestMessage
        holder.detailButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, RestroomDetailActivity::class.java)
            intent.putExtra("restroom", deleteItem.restroom)
            holder.itemView.context.startActivity(intent)
        }
        holder.cancelButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).run {
                setTitle("삭제 요청 취소")
                setMessage("삭제 요청을 취소하시겠습니까?")
                setPositiveButton("확인") { _, _ ->
                    val db = AppDatabase.getDatabase(holder.itemView.context)
                    db!!.DeleteRequestDao().delete(deleteItem.deleteRequest)
                    deleteList.removeAt(position)
                    notifyItemRemoved(position)
                }
                setNegativeButton("취소", null)
                show()
            }
        }
    }


    override fun getItemCount(): Int = deleteList.size
}