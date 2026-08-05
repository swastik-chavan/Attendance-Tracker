package com.xerra.attendancetracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.xerra.attendancetracker.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let {
            try {
                val outputStream = requireContext().contentResolver.openOutputStream(it)
                if (outputStream != null) {
                    viewModel.exportDatabase(outputStream)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.toast_error_generic, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                if (inputStream != null) {
                    viewModel.importDatabase(inputStream)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.toast_error_generic, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sliderGoal: Slider = view.findViewById(R.id.slider_target_goal)
        val tvGoalValue: TextView = view.findViewById(R.id.tv_goal_value)
        val chipGroupTheme: ChipGroup = view.findViewById(R.id.chip_group_theme)
        val chipGroupScale: ChipGroup = view.findViewById(R.id.chip_group_scale)
        
        val btnExport: View = view.findViewById(R.id.btn_export)
        val btnImport: View = view.findViewById(R.id.btn_import)
        val btnReset: View = view.findViewById(R.id.btn_reset_app)

        // Target goal slider setup
        sliderGoal.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.setTargetGoal(value)
            }
        }

        // Theme selection setup
        chipGroupTheme.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val theme = when (checkedIds.first()) {
                    R.id.chip_theme_light -> "LIGHT"
                    R.id.chip_theme_dark -> "DARK"
                    else -> "SYSTEM"
                }
                viewModel.setTheme(theme)
            }
        }

        // UI Scale selection setup
        chipGroupScale.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val scale = when (checkedIds.first()) {
                    R.id.chip_scale_compact -> "COMPACT"
                    R.id.chip_scale_comfortable -> "COMFORTABLE"
                    R.id.chip_scale_large -> "LARGE"
                    else -> "DEFAULT"
                }
                if (scale != viewModel.uiScale.value) {
                    viewModel.setUiScale(scale)
                }
            }
        }

        // Export data setup
        btnExport.setOnClickListener {
            exportLauncher.launch("attendance_backup.db")
        }

        // Import data setup
        btnImport.setOnClickListener {
            importLauncher.launch(arrayOf("*/*"))
        }

        // Reset app setup
        btnReset.setOnClickListener {
            showResetConfirmation()
        }

        // Collect states
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                launch {
                    viewModel.targetGoal.collectLatest { goal ->
                        sliderGoal.value = goal
                        tvGoalValue.text = String.format("%.0f%%", goal)
                    }
                }

                launch {
                    viewModel.themeMode.collectLatest { theme ->
                        val chipId = when (theme) {
                            "LIGHT" -> R.id.chip_theme_light
                            "DARK" -> R.id.chip_theme_dark
                            else -> R.id.chip_theme_system
                        }
                        chipGroupTheme.findViewById<Chip>(chipId)?.isChecked = true
                    }
                }

                launch {
                    viewModel.uiScale.collectLatest { scale ->
                        val chipId = when (scale) {
                            "COMPACT" -> R.id.chip_scale_compact
                            "COMFORTABLE" -> R.id.chip_scale_comfortable
                            "LARGE" -> R.id.chip_scale_large
                            else -> R.id.chip_scale_default
                        }
                        chipGroupScale.findViewById<Chip>(chipId)?.isChecked = true
                    }
                }

                launch {
                    viewModel.settingsEvent.collectLatest { result ->
                        if (result.isSuccess) {
                            val msg = result.getOrNull()
                            if (msg == "UI_SCALE_CHANGED") {
                                activity?.recreate()
                            } else {
                                val msgRes = when (msg) {
                                    "EXPORT_SUCCESS" -> R.string.toast_export_success
                                    "IMPORT_SUCCESS" -> R.string.toast_import_success
                                    "RESET_SUCCESS" -> R.string.toast_reset_success
                                    else -> R.string.toast_export_success
                                }
                                Toast.makeText(requireContext(), msgRes, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = result.exceptionOrNull()?.message ?: getString(R.string.toast_error_generic)
                            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showResetConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("RESET APPLICATION")
            .setMessage(R.string.dialog_reset_message)
            .setPositiveButton("RESET") { _, _ ->
                viewModel.resetApp()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }
}
