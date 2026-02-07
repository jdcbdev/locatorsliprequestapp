package com.example.locatorsliprequestapp.ui.slipsforapproval

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locatorsliprequestapp.R
import com.example.locatorsliprequestapp.api.RequestBySupervisorData

class LocatorSlipForApprovalAdapter(
    private val context: Context,
    private var list: List<RequestBySupervisorData>,
    private val onApprove: (Int) -> Unit,
    private val onDeny: (Int) -> Unit,
    private val statusFilter: String
) : RecyclerView.Adapter<LocatorSlipForApprovalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val employeeName: TextView = view.findViewById(R.id.txtEmployeeName)
        val requestDate: TextView = view.findViewById(R.id.txtRequestDate)
        val approvalStatus: TextView = view.findViewById(R.id.txtApprovalStatus)
        val purpose: TextView = view.findViewById(R.id.txtPurpose)
        val location: TextView = view.findViewById(R.id.txtLocation)
        val details: TextView = view.findViewById(R.id.txtDetails)
        val detailsLayout: LinearLayout = view.findViewById(R.id.detailsLayout)
        val toggleDetails: ImageView = view.findViewById(R.id.ivToggleDetails)
        val approveButton: Button = view.findViewById(R.id.btnApprove)
        val denyButton: Button = view.findViewById(R.id.btnDeny)
        val buttonsLayout: LinearLayout = view.findViewById(R.id.buttonsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.row_request_for_approval, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.employeeName.text = "${item.firstname} ${item.lastname}"
        holder.requestDate.text = item.requestDate
        holder.approvalStatus.text = item.status
        holder.purpose.text = item.purpose
        holder.location.text = item.location
        holder.details.text = item.details

        if (statusFilter == "Pending") {
            holder.buttonsLayout.visibility = View.VISIBLE
        } else {
            holder.buttonsLayout.visibility = View.GONE
        }

        holder.toggleDetails.setOnClickListener {
            holder.detailsLayout.visibility = if (holder.detailsLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        holder.approveButton.setOnClickListener {
            onApprove(item.requestId)
        }

        holder.denyButton.setOnClickListener {
            onDeny(item.requestId)
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<RequestBySupervisorData>) {
        list = newList
        notifyDataSetChanged()
    }
}