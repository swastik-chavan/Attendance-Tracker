package com.xerra.attendancetracker.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xerra.attendancetracker.R
import com.xerra.attendancetracker.domain.model.AttendanceHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onDeleteClick: (AttendanceHistory) -> Unit
) : ListAdapter<AttendanceHistory, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_log, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val log = getItem(position)
        holder.bind(log, onDeleteClick)
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSubjectName: TextView = itemView.findViewById(R.id.tv_history_subject_name)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_history_time)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_history_status)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_log)
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())

        fun bind(log: AttendanceHistory, onDeleteClick: (AttendanceHistory) -> Unit) {
            val context = itemView.context
            
            tvSubjectName.text = log.subjectName.uppercase()
            tvTime.text = dateFormat.format(Date(log.timestamp)).uppercase()
            tvStatus.text = log.status.uppercase()

            val isPresent = log.status.equals("PRESENT", ignoreCase = true)
            if (isPresent) {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_present)
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_absent)
            }
            
            btnDelete.setOnClickListener { onDeleteClick(log) }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<AttendanceHistory>() {
        override fun areItemsTheSame(oldItem: AttendanceHistory, newItem: AttendanceHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AttendanceHistory, newItem: AttendanceHistory): Boolean {
            return oldItem == newItem
        }
    }
}
