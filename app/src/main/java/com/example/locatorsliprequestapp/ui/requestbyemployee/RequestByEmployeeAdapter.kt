package com.example.locatorsliprequestapp.ui.requestbyemployee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locatorsliprequestapp.R
import com.example.locatorsliprequestapp.api.RequestByEmployeeData
import com.example.locatorsliprequestapp.api.RequestStatus
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class RequestByEmployeeAdapter(
    private val context: Context,
    private var list: List<RequestByEmployeeData>
) : RecyclerView.Adapter<RequestByEmployeeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val requestDate: TextView = view.findViewById(R.id.txtRequestDate)
        val approvalStatus: TextView = view.findViewById(R.id.txtApprovalStatus)
        val purpose: TextView = view.findViewById(R.id.txtPurpose)
        val location: TextView = view.findViewById(R.id.txtLocation)
        val details: TextView = view.findViewById(R.id.txtDetails)
        val detailsLayout: LinearLayout = view.findViewById(R.id.detailsLayout)
        val toggleDetails: ImageView = view.findViewById(R.id.ivToggleDetails)
        val completeDetailsLayout: LinearLayout = view.findViewById(R.id.completeDetailsLayout)
        val timeOut: TextView = view.findViewById(R.id.txtTimeOut)
        val timeIn: TextView = view.findViewById(R.id.txtTimeIn)
        val exitPoint: TextView = view.findViewById(R.id.txtExitPoint)
        val entryPoint: TextView = view.findViewById(R.id.txtEntryPoint)
        val returnedAfter: TextView = view.findViewById(R.id.txtReturnedAfter)
        val returnedAfterLayout: LinearLayout = view.findViewById(R.id.returnedAfterLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.row_my_request_as_employee, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val outputDateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a")
        val outputTimeFormat = SimpleDateFormat("hh:mm a")

        try {
            val date = inputDateFormat.parse(item.requestDate)
            holder.requestDate.text = outputDateFormat.format(date)
        } catch (e: Exception) {
            holder.requestDate.text = item.requestDate // fallback
        }

        holder.approvalStatus.text = item.status
        holder.purpose.text = item.purpose
        holder.location.text = item.location
        holder.details.text = item.details

        // Set initial visibility
        holder.detailsLayout.visibility = View.GONE
        holder.completeDetailsLayout.visibility = View.GONE
        holder.returnedAfterLayout.visibility = View.GONE

        if (item.status == RequestStatus.COMPLETED) {
            holder.returnedAfterLayout.visibility = View.VISIBLE

            val timeInStr = item.timeIn
            val timeOutStr = item.timeOut
            var timeInDate: java.util.Date? = null
            var timeOutDate: java.util.Date? = null


            if (timeOutStr != null) {
                try {
                    timeOutDate = inputDateFormat.parse(timeOutStr)
                    holder.timeOut.text = outputTimeFormat.format(timeOutDate)
                } catch (e: Exception) {
                    holder.timeOut.text = timeOutStr
                }
            } else {
                 holder.timeOut.text = "N/A"
            }

            if (timeInStr != null) {
                 try {
                    timeInDate = inputDateFormat.parse(timeInStr)
                    holder.timeIn.text = outputTimeFormat.format(timeInDate)
                } catch (e: Exception) {
                    holder.timeIn.text = timeInStr
                }
            } else {
                holder.timeIn.text = "N/A"
            }

            holder.exitPoint.text = item.exitpoint
            holder.entryPoint.text = item.entrypoint

            // Calculate the difference between timeIn and timeOut
            if (timeInDate != null && timeOutDate != null) {
                val diff = timeInDate.time - timeOutDate.time
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                holder.returnedAfter.text = String.format("%d hours and %d minutes", hours, minutes)
            } else {
                holder.returnedAfter.text = "N/A"
            }
        }

        holder.toggleDetails.setOnClickListener {
            val isDetailsVisible = holder.detailsLayout.visibility == View.VISIBLE
            val newVisibility = if (isDetailsVisible) View.GONE else View.VISIBLE

            holder.detailsLayout.visibility = newVisibility

            if (item.status == RequestStatus.COMPLETED) {
                holder.completeDetailsLayout.visibility = newVisibility
            } else {
                holder.completeDetailsLayout.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<RequestByEmployeeData>) {
        list = newList
        notifyDataSetChanged()
    }
}
