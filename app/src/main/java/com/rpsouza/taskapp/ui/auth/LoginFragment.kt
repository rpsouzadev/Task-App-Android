package com.rpsouza.taskapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.databinding.FragmentLoginBinding
import com.rpsouza.taskapp.ui.BaseFragment
import com.rpsouza.taskapp.utils.FirebaseHelper
import com.rpsouza.taskapp.utils.showBottomSheet

class LoginFragment : BaseFragment() {
  private var _binding: FragmentLoginBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentLoginBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initListeners()
  }

  private fun initListeners() {
    binding.btnRegister.setOnClickListener {
      findNavController()
        .navigate(R.id.action_loginFragment_to_registerFragment)
    }

    binding.btnRecover.setOnClickListener {
      findNavController()
        .navigate(R.id.action_loginFragment_to_recoverAccountFragment)
    }

    binding.btnLogin.setOnClickListener {
      validateData()
    }
  }

  private fun validateData() {
    val email = binding.editEmail.text.toString().trim()
    val password = binding.editPassword.text.toString().trim()

    if (email.isNotEmpty()) {
      if (password.isNotEmpty()) {
        hideKeyboard()
        binding.progressBar.isVisible = true
        handleLogin(email, password)
      } else {
        showBottomSheet(message = getString(R.string.password_empty))
      }
    } else {
      showBottomSheet(message = getString(R.string.email_empty))
    }
  }

  private fun handleLogin(email: String, password: String) {
    FirebaseHelper.getAuth().signInWithEmailAndPassword(email, password)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          findNavController().navigate(R.id.action_global_homeFragment)
        } else {
          binding.progressBar.isVisible = false

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