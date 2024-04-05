package com.rpsouza.taskapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.databinding.FragmentRecoverAccountBinding
import com.rpsouza.taskapp.utils.FirebaseHelper
import com.rpsouza.taskapp.utils.initToolbar
import com.rpsouza.taskapp.utils.showBottomSheet

class RecoverAccountFragment : Fragment() {
    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)
        initListeners()
    }

    private fun initListeners() {
        binding.btnRecovery.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.editEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            binding.progressBar.isVisible = true
            handleRecoverAccountUser(email)
        } else {
            showBottomSheet(message = getString(R.string.email_empty))
        }
    }

    private fun handleRecoverAccountUser(email: String) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.isVisible = false

                if (task.isSuccessful) {
                    showBottomSheet(message = getString(R.string.text_message_recover_account))
                } else {
                    showBottomSheet(
                        message = getString(
                            FirebaseHelper.validError(
                                task.exception?.message.toString()
                            )
                        )
                    )
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}