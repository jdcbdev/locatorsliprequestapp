package com.example.locatorsliprequestapp.ui.requestbyemployee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locatorsliprequestapp.R
import com.example.locatorsliprequestapp.api.RequestByEmployeeData

class RequestByEmployeeAdapter(
    private var list: List<RequestByEmployeeData>
) : RecyclerView.Adapter<RequestByEmployeeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val purpose: TextView = view.findViewById(R.id.txtPurpose)
        val location: TextView = view.findViewById(R.id.txtLocation)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val details: TextView = view.findViewById(R.id.txtDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_my_request_as_employee, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = list[pos]
        holder.purpose.text = item.purpose
        holder.location.text = item.location
        holder.status.text = item.status
        holder.details.text = item.details
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<RequestByEmployeeData>) {
        list = newList
        notifyDataSetChanged()
    }
}
