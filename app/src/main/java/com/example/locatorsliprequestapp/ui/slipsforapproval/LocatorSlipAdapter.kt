package com.example.locatorsliprequestapp.ui.slipsforapproval

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locatorsliprequestapp.R
import com.example.locatorsliprequestapp.api.RequestByEmployeeData

class LocatorSlipAdapter(
    private val context: Context,
    private var list: List<RequestByEmployeeData>
) : RecyclerView.Adapter<LocatorSlipAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtEmployeeName)
        val department: TextView = view.findViewById(R.id.txtDepartment)
        val purpose: TextView = view.findViewById(R.id.txtPurpose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.row_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = list[pos]
        //holder.name.text = "${item.firstName} ${item.lastName}"
        //holder.department.text = item.department
        holder.purpose.text = item.purpose
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<RequestByEmployeeData>) {
        list = newList
        notifyDataSetChanged()
    }
}