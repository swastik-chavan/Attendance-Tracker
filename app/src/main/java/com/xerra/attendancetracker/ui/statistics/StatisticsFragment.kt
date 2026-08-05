package com.xerra.attendancetracker.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.xerra.attendancetracker.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvOverallPct: TextView = view.findViewById(R.id.tv_stats_overall_percentage)
        val tvOverallClasses: TextView = view.findViewById(R.id.tv_stats_overall_classes)
        
        val tvBestName: TextView = view.findViewById(R.id.tv_best_subject_name)
        val tvBestPct: TextView = view.findViewById(R.id.tv_best_subject_percentage)
        
        val tvWorstName: TextView = view.findViewById(R.id.tv_worst_subject_name)
        val tvWorstPct: TextView = view.findViewById(R.id.tv_worst_subject_percentage)
        
        val tvTotalSubjects: TextView = view.findViewById(R.id.tv_stats_total_subjects)
        val tvTotalClasses: TextView = view.findViewById(R.id.tv_stats_total_classes)
        val tvPresent: TextView = view.findViewById(R.id.tv_stats_present)
        val tvAbsent: TextView = view.findViewById(R.id.tv_stats_absent)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statsState.collectLatest { state ->
                    tvOverallPct.text = String.format("%.1f%%", state.overallPercentage)
                    tvOverallClasses.text = "${state.presentClasses} / ${state.totalClasses} CLASSES ATTENDED"

                    tvBestName.text = state.bestSubjectName.uppercase()
                    tvBestPct.text = String.format("%.1f%%", state.bestSubjectPercentage)
                    
                    tvWorstName.text = state.worstSubjectName.uppercase()
                    tvWorstPct.text = String.format("%.1f%%", state.worstSubjectPercentage)

                    tvTotalSubjects.text = state.totalSubjects.toString()
                    tvTotalClasses.text = state.totalClasses.toString()
                    tvPresent.text = state.presentClasses.toString()
                    tvAbsent.text = state.absentClasses.toString()
                }
            }
        }
    }
}
