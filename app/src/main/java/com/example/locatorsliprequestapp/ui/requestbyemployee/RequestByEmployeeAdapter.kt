package com.example.locatorsliprequestapp.ui.requestbyemployee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.locatorsliprequestapp.R
import com.example.locatorsliprequestapp.api.RequestByEmployeeData
import com.example.locatorsliprequestapp.api.RequestStatus
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class RequestByEmployeeAdapter(
    private val context: Context,
    private var list: List<RequestByEmployeeData>,
    private val onScanQrClicked: (RequestByEmployeeData) -> Unit
) : RecyclerView.Adapter<RequestByEmployeeAdapter.ViewHolder>() {

    companion object {
        internal const val NULL_DATE = "0000-00-00 00:00:00"
    }

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
        val scanQrButton: Button = view.findViewById(R.id.scanQrButton)
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

        val statusColor = when (item.status) {
            "Pending" -> R.color.status_pending
            "Approved" -> R.color.status_approved
            "Denied" -> R.color.status_denied
            "Completed" -> R.color.status_completed
            "Cancelled" -> R.color.status_cancelled
            else -> android.R.color.black
        }
        holder.approvalStatus.setTextColor(ContextCompat.getColor(context, statusColor))

        // Set initial visibility
        holder.detailsLayout.visibility = View.GONE
        holder.completeDetailsLayout.visibility = View.GONE
        holder.returnedAfterLayout.visibility = View.GONE

        if (item.status == RequestStatus.APPROVED) {
            if (item.timeOut == null || item.timeOut == NULL_DATE) {
                holder.scanQrButton.visibility = View.VISIBLE
                holder.scanQrButton.text = "Scan to Time Out"
                holder.scanQrButton.setBackgroundColor(ContextCompat.getColor(context, R.color.wmsu))
            } else if (item.timeIn == null || item.timeIn == NULL_DATE) {
                holder.scanQrButton.visibility = View.VISIBLE
                holder.scanQrButton.text = "Scan to Time In"
                holder.scanQrButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            } else {
                holder.scanQrButton.visibility = View.GONE
            }
        } else {
            holder.scanQrButton.visibility = View.GONE
        }

        if (item.status == RequestStatus.COMPLETED) {
            holder.returnedAfterLayout.visibility = View.VISIBLE

            val timeInStr = item.timeIn
            val timeOutStr = item.timeOut
            var timeInDate: java.util.Date? = null
            var timeOutDate: java.util.Date? = null


            if (timeOutStr != null && timeOutStr != NULL_DATE) {
                try {
                    timeOutDate = inputDateFormat.parse(timeOutStr)
                } catch (e: Exception) {
                    holder.timeOut.text = timeOutStr
                }
            } else {
                holder.timeOut.text = "N/A"
            }

            if (timeInStr != null && timeInStr != NULL_DATE) {
                try {
                    timeInDate = inputDateFormat.parse(timeInStr)
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

                val displayFormat = if (diff >= TimeUnit.DAYS.toMillis(1)) {
                    outputDateFormat
                } else {
                    outputTimeFormat
                }
                holder.timeOut.text = displayFormat.format(timeOutDate)
                holder.timeIn.text = displayFormat.format(timeInDate)

                val days = TimeUnit.MILLISECONDS.toDays(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                if (days > 0) {
                    holder.returnedAfter.text =
                        String.format("%d days, %d hours and %d minutes", days, hours, minutes)
                } else {
                    holder.returnedAfter.text =
                        String.format("%d hours and %d minutes", hours, minutes)
                }
            } else {
                if (timeOutDate != null) {
                    holder.timeOut.text = outputTimeFormat.format(timeOutDate)
                }
                if (timeInDate != null) {
                    holder.timeIn.text = outputTimeFormat.format(timeInDate)
                }
                holder.returnedAfter.text = "N/A"
            }
        }

        holder.toggleDetails.setOnClickListener {
            val isDetailsVisible = holder.detailsLayout.visibility == View.VISIBLE
            val newVisibility = if (isDetailsVisible) View.GONE else View.VISIBLE

            holder.detailsLayout.visibility = newVisibility

            if (item.status.trim().equals("Completed", ignoreCase = true)) {
                holder.completeDetailsLayout.visibility = newVisibility
            } else {
                holder.completeDetailsLayout.visibility = View.GONE
            }
        }

        holder.scanQrButton.setOnClickListener {
            onScanQrClicked(item)
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<RequestByEmployeeData>) {
        list = newList
        notifyDataSetChanged()
    }
}
