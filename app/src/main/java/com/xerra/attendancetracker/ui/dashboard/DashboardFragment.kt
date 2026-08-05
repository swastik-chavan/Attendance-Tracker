package com.xerra.attendancetracker.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.xerra.attendancetracker.R
import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.ui.addsubject.AddEditSubjectDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: SubjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        val tvOverallPct: TextView = view.findViewById(R.id.tv_overall_percentage)
        val tvOverallClasses: TextView = view.findViewById(R.id.tv_overall_classes)
        val progressFill: View = view.findViewById(R.id.view_progress_fill)
        val rvSubjects: RecyclerView = view.findViewById(R.id.rv_subjects)
        val layoutEmpty: LinearLayout = view.findViewById(R.id.layout_empty_state)
        val etSearch: EditText = view.findViewById(R.id.et_search)
        val btnHistory: ImageButton = view.findViewById(R.id.btn_history)
        val fabAdd: FloatingActionButton = view.findViewById(R.id.fab_add_subject)

        // Set up recycler view
        adapter = SubjectAdapter(
            targetGoal = viewModel.targetGoal.value,
            onPresentClick = { subject -> viewModel.recordPresent(subject.id) },
            onAbsentClick = { subject -> viewModel.recordAbsent(subject.id) },
            onEditClick = { subject -> showAddEditDialog(subject) },
            onDeleteClick = { subject -> showDeleteConfirmation(subject) }
        )
        rvSubjects.layoutManager = LinearLayoutManager(requireContext())
        rvSubjects.adapter = adapter

        // Search text listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // History logs button
        btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_history)
        }

        // FAB add button
        fabAdd.setOnClickListener {
            showAddEditDialog(null)
        }

        // Collect StateFlows
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                launch {
                    viewModel.subjects.collectLatest { list ->
                        adapter.submitList(list)
                        layoutEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.overallStats.collectLatest { stats ->
                        val percentage = stats.first
                        val attended = stats.second
                        val total = stats.third
                        
                        tvOverallPct.text = String.format("%.1f%%", percentage)
                        tvOverallClasses.text = "ATTENDED: $attended / TOTAL: $total"
                        
                        // Update custom progress fill
                        progressFill.post {
                            val params = progressFill.layoutParams as ViewGroup.LayoutParams
                            val parentWidth = (progressFill.parent as View).width
                            params.width = (parentWidth * (percentage / 100f)).toInt()
                            progressFill.layoutParams = params
                        }
                    }
                }

                launch {
                    viewModel.targetGoal.collectLatest { goal ->
                        adapter.updateTargetGoal(goal)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload target goal on resume, as it could have changed in Settings screen
        viewModel.loadTargetGoal()
    }

    private fun showAddEditDialog(subject: Subject?) {
        val dialog = AddEditSubjectDialogFragment.newInstance(subject?.id ?: -1L)
        dialog.show(childFragmentManager, "AddEditSubjectDialog")
    }

    private fun showDeleteConfirmation(subject: Subject) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, subject.name))
            .setPositiveButton(R.string.dialog_confirm_btn) { _, _ ->
                viewModel.deleteSubject(subject)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }
}
