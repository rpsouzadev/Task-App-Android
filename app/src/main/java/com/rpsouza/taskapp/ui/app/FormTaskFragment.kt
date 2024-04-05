package com.rpsouza.taskapp.ui.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.databinding.FragmentFormTaskBinding
import com.rpsouza.taskapp.utils.initToolbar
import com.rpsouza.taskapp.utils.showBottomSheet

class FormTaskFragment : Fragment() {
  private var _binding: FragmentFormTaskBinding? = null
  private val binding get() = _binding!!

  private lateinit var task: Task
  private var status: Status = Status.TODO
  private var newTask: Boolean = true

  private val args: FormTaskFragmentArgs by navArgs()

  private lateinit var reference: DatabaseReference
  private lateinit var auth: FirebaseAuth

  private val viewModel: TaskViewModel by activityViewModels()

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

    reference = Firebase.database.reference
    auth = Firebase.auth

    getArgs()
    initListeners()
  }

  private fun getArgs() {
    args.task?.let {
      this.task = it
      configTask()
    }
  }

  private fun configTask() {
    newTask = false
    status = task.status
    binding.textToolbarTitle.setText(R.string.toolbar_title_edit_form_task_fragment)
    binding.editDescription.setText(task.description)
    setStatus()
  }

  private fun setStatus() {
    val id = when (task.status) {
      Status.TODO -> R.id.rbTodo
      Status.DOING -> R.id.rbDoing
      else -> R.id.rbDone
    }

    binding.rgStatus.check(id)
  }

  private fun initListeners() {
    binding.btnSalveTask.setOnClickListener { validateData() }

    binding.rgStatus.setOnCheckedChangeListener { _, checkedId ->
      status = when (checkedId) {
        R.id.rbTodo -> Status.TODO
        R.id.rbDoing -> Status.DOING
        else -> Status.DONE
      }
    }
  }

  private fun validateData() {
    val description = binding.editDescription.text.toString().trim()

    if (description.isNotEmpty()) {
      binding.progressBar.isVisible = true

      if (newTask) {
        task = Task()
        task.id = reference.database.reference.push().key ?: ""
      }

      task.description = description
      task.status = status

      saveTask()
    } else {
      showBottomSheet(message = getString(R.string.description_empty_form_task_fragment))
    }
  }

  private fun saveTask() {
    reference
      .child("tasks")
      .child(auth.currentUser?.uid ?: "")
      .child(task.id)
      .setValue(task).addOnCompleteListener { result ->
        if (result.isSuccessful) {
          if (newTask) {
            Toast.makeText(
              requireContext(),
              R.string.text_save_sucess_form_task_fragment,
              Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack()
          } else {
            viewModel.setUpdateTask(task)
            binding.progressBar.isVisible = false
            binding.editDescription.setText("")

            Toast.makeText(
              requireContext(),
              R.string.text_update_sucess_form_task_fragment,
              Toast.LENGTH_SHORT
            ).show()
          }
        } else {
          showBottomSheet(message = getString(R.string.error_generic))
          binding.progressBar.isVisible = false
        }

      }
  }


  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}