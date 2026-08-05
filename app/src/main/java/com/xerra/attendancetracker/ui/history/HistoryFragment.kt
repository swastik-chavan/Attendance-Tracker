package com.xerra.attendancetracker.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xerra.attendancetracker.R
import com.xerra.attendancetracker.domain.model.AttendanceHistory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnClear: Button = view.findViewById(R.id.btn_clear_history)
        val rvHistory: RecyclerView = view.findViewById(R.id.rv_history)
        val layoutEmpty: LinearLayout = view.findViewById(R.id.layout_history_empty)

        // Set up adapter
        adapter = HistoryAdapter { log ->
            showDeleteSingleLogConfirmation(log)
        }
        
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        rvHistory.adapter = adapter

        // Back action
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Clear all action
        btnClear.setOnClickListener {
            showClearAllLogsConfirmation()
        }

        // Collect logs flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyLogs.collectLatest { list ->
                    adapter.submitList(list)
                    layoutEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    btnClear.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun showDeleteSingleLogConfirmation(log: AttendanceHistory) {
        AlertDialog.Builder(requireContext())
            .setTitle("DELETE LOG ENTRY")
            .setMessage(R.string.history_delete_confirm)
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.deleteLog(log.id, log.subjectId, log.status)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun showClearAllLogsConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("CLEAR ALL LOGS")
            .setMessage(R.string.history_clear_confirm)
            .setPositiveButton("CLEAR ALL") { _, _ ->
                viewModel.clearAllLogs()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }
}
