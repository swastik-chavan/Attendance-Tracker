package com.xerra.attendancetracker.ui.dashboard

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.xerra.attendancetracker.R
import com.xerra.attendancetracker.domain.model.Subject

class SubjectAdapter(
    private var targetGoal: Float,
    private val onPresentClick: (Subject) -> Unit,
    private val onAbsentClick: (Subject) -> Unit,
    private val onEditClick: (Subject) -> Unit,
    private val onDeleteClick: (Subject) -> Unit
) : ListAdapter<Subject, SubjectAdapter.SubjectViewHolder>(SubjectDiffCallback()) {

    fun updateTargetGoal(newGoal: Float) {
        this.targetGoal = newGoal
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject_card, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = getItem(position)
        holder.bind(subject, targetGoal, onPresentClick, onAbsentClick, onEditClick, onDeleteClick)
    }

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView as MaterialCardView
        private val tvName: TextView = itemView.findViewById(R.id.tv_subject_name)
        private val tvTeacher: TextView = itemView.findViewById(R.id.tv_teacher_name)
        private val tvGoal: TextView = itemView.findViewById(R.id.tv_goal_status)
        private val tvCounts: TextView = itemView.findViewById(R.id.tv_counts)
        private val tvPercent: TextView = itemView.findViewById(R.id.tv_percentage)
        private val btnMenu: ImageButton = itemView.findViewById(R.id.btn_menu)
        private val btnPresent: Button = itemView.findViewById(R.id.btn_present)
        private val btnAbsent: Button = itemView.findViewById(R.id.btn_absent)

        fun bind(
            subject: Subject,
            targetGoal: Float,
            onPresentClick: (Subject) -> Unit,
            onAbsentClick: (Subject) -> Unit,
            onEditClick: (Subject) -> Unit,
            onDeleteClick: (Subject) -> Unit
        ) {
            val context = itemView.context
            
            // Set text values
            tvName.text = subject.name.uppercase()
            tvTeacher.text = subject.teacherName?.uppercase() ?: ""
            tvTeacher.visibility = if (subject.teacherName.isNullOrBlank()) View.GONE else View.VISIBLE
            
            tvPercent.text = subject.formattedPercentage

            tvCounts.text = "ATTENDED: ${subject.presentCount} / TOTAL: ${subject.totalCount}"

            // Goal status logic
            val goalMsg = subject.getGoalStatus(targetGoal.toDouble())
            tvGoal.text = goalMsg.uppercase()
            
            // Adjust goal text color based on track status
            if (subject.percentage >= targetGoal) {
                tvGoal.setTextColor(ContextCompat.getColor(context, R.color.brutalist_accent))
            } else {
                tvGoal.setTextColor(ContextCompat.getColor(context, R.color.brutalist_absent))
            }

            // In Brutalist design, we keep it simple - white background for all cards
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bg_white))

            // Setup listeners
            btnPresent.setOnClickListener { onPresentClick(subject) }
            btnAbsent.setOnClickListener { onAbsentClick(subject) }

            btnMenu.setOnClickListener { view ->
                val popup = PopupMenu(context, view)
                popup.menu.add(0, 1, 0, "EDIT")
                popup.menu.add(0, 2, 1, "DELETE")
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> onEditClick(subject)
                        2 -> onDeleteClick(subject)
                    }
                    true
                }
                popup.show()
            }
        }
    }

    class SubjectDiffCallback : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem == newItem
        }
    }
}
