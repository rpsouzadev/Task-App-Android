package com.rpsouza.taskapp.ui.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.databinding.FragmentFormTaskBinding
import com.rpsouza.taskapp.utils.initToolbar
import com.rpsouza.taskapp.utils.showBottomSheet

class FormTaskFragment : Fragment() {
    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)
        initListeners()
    }

    private fun initListeners() {
        binding.btnSalveTask.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val description = binding.editDescription.text.toString().trim()

        if (description.isNotEmpty()) {
            Toast.makeText(
                requireContext(), "Tudo certo.", Toast.LENGTH_SHORT
            ).show()

        } else {
            showBottomSheet(message = getString(R.string.description_empty_form_task_fragment))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}