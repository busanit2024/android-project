package com.busanit.searchrestroom.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R

class DeleteAdapter(
    private val deleteList: List<RequestDelete>
) : RecyclerView.Adapter<DeleteAdapter.DeleteViewHolder>() {

    inner class DeleteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val buildingAddress: TextView = view.findViewById(R.id.building_address)
        val deleteRequestDate: TextView = view.findViewById(R.id.request_date)
        val deleteRequestReason: TextView = view.findViewById(R.id.request_reason)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_delete, parent, false)
        return DeleteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeleteViewHolder, position: Int) {
        val deleteItem = deleteList[position]
        holder.buildingName.text = deleteItem.buildingName
        holder.buildingAddress.text = deleteItem.buildingAddress
        holder.deleteRequestDate.text = deleteItem.delete_request_time.toString()
        holder.deleteRequestReason.text = deleteItem.delete_request_reason
    }

    override fun getItemCount(): Int = deleteList.size
}