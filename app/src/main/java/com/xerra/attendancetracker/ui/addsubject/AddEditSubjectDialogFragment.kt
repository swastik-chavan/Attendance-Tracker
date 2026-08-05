package com.xerra.attendancetracker.ui.addsubject

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.xerra.attendancetracker.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddEditSubjectDialogFragment : DialogFragment() {

    private val viewModel: AddEditSubjectViewModel by viewModels()
    private var subjectId: Long = -1L

    companion object {
        private const val ARG_SUBJECT_ID = "arg_subject_id"

        fun newInstance(subjectId: Long): AddEditSubjectDialogFragment {
            val fragment = AddEditSubjectDialogFragment()
            val args = Bundle().apply {
                putLong(ARG_SUBJECT_ID, subjectId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make the dialog fit screen width nicely
        setStyle(STYLE_NORMAL, R.style.Theme_AttendanceTracker)
        subjectId = arguments?.getLong(ARG_SUBJECT_ID, -1L) ?: -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle: TextView = view.findViewById(R.id.tv_dialog_title)
        val tilName: TextInputLayout = view.findViewById(R.id.til_subject_name)
        val etName: TextInputEditText = view.findViewById(R.id.et_subject_name)
        val etTeacher: TextInputEditText = view.findViewById(R.id.et_teacher_name)
        val btnCancel: android.widget.Button = view.findViewById(R.id.btn_cancel_dialog)
        val btnSave: android.widget.Button = view.findViewById(R.id.btn_save_dialog)

        // Prepopulate colors in circular options
        val isDark = (requireContext().resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val colorOptions: Array<FrameLayout?> = Array(8) { i ->
            view.findViewById<FrameLayout>(
                resources.getIdentifier("color_option_$i", "id", requireContext().packageName)
            )
        }
        val colorViews: Array<View?> = Array(8) { i ->
            view.findViewById<View>(
                resources.getIdentifier("color_view_$i", "id", requireContext().packageName)
            )
        }
        val colorChecks: Array<ImageView?> = Array(8) { i ->
            view.findViewById<ImageView>(
                resources.getIdentifier("color_check_$i", "id", requireContext().packageName)
            )
        }

        for (i in 0..7) {
            val colorVal = getPastelColor(requireContext(), i, isDark)
            colorViews[i]?.setBackgroundColor(colorVal)
            
            colorChecks[i]?.imageTintList = ColorStateList.valueOf(android.graphics.Color.BLACK)

            colorOptions[i]?.setOnClickListener {
                viewModel.selectColor(i)
            }
        }

        // Cancel action
        btnCancel.setOnClickListener {
            dismiss()
        }

        // Save action
        btnSave.setOnClickListener {
            val name = etName.text?.toString()?.trim() ?: ""
            val teacherName = etTeacher.text?.toString()?.trim()

            if (name.isEmpty()) {
                tilName.error = getString(R.string.error_name_empty)
                return@setOnClickListener
            }
            if (name.length > 50) {
                tilName.error = getString(R.string.error_name_too_long)
                return@setOnClickListener
            }
            tilName.error = null
            viewModel.saveSubject(name, teacherName)
        }

        // Load subject if in edit mode
        viewModel.loadSubject(subjectId)

        // Observe flows
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe subject details loading
                launch {
                    viewModel.subject.collectLatest { subject ->
                        if (subject != null) {
                            tvTitle.text = getString(R.string.edit_subject_title)
                            etName.setText(subject.name)
                            etTeacher.setText(subject.teacherName ?: "")
                        } else {
                            tvTitle.text = getString(R.string.add_subject_title)
                        }
                    }
                }

                // Observe color selection
                launch {
                    viewModel.selectedColor.collectLatest { index ->
                        for (i in 0..7) {
                            colorChecks[i]?.visibility = if (i == index) View.VISIBLE else View.GONE
                        }
                    }
                }

                // Observe save result
                launch {
                    viewModel.saveResult.collectLatest { result ->
                        if (result.isSuccess) {
                            dismiss()
                        } else {
                            val errorMsg = result.exceptionOrNull()?.message ?: getString(R.string.toast_error_generic)
                            tilName.error = errorMsg
                            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun getPastelColor(context: Context, index: Int, isDark: Boolean): Int {
        val resId: Int = if (isDark) {
            when (index) {
                0 -> R.color.pastel_blue_dark
                1 -> R.color.pastel_green_dark
                2 -> R.color.pastel_yellow_dark
                3 -> R.color.pastel_red_dark
                4 -> R.color.pastel_purple_dark
                5 -> R.color.pastel_orange_dark
                6 -> R.color.pastel_teal_dark
                7 -> R.color.pastel_pink_dark
                else -> R.color.pastel_blue_dark
            }
        } else {
            when (index) {
                0 -> R.color.pastel_blue
                1 -> R.color.pastel_green
                2 -> R.color.pastel_yellow
                3 -> R.color.pastel_red
                4 -> R.color.pastel_purple
                5 -> R.color.pastel_orange
                6 -> R.color.pastel_teal
                7 -> R.color.pastel_pink
                else -> R.color.pastel_blue
            }
        }
        return ContextCompat.getColor(context, resId)
    }
}
