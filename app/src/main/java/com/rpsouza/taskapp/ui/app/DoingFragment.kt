package com.rpsouza.taskapp.ui.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.databinding.FragmentDoingBinding
import com.rpsouza.taskapp.ui.adapter.TaskAdapter
import com.rpsouza.taskapp.utils.FirebaseHelper
import com.rpsouza.taskapp.utils.showBottomSheet

class DoingFragment : Fragment() {
  private var _binding: FragmentDoingBinding? = null
  private val binding get() = _binding!!

  private lateinit var taskAdapter: TaskAdapter

  private val viewModel: TaskViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentDoingBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    observeViewModel()
    initRecyclerView()
    getTasks()
  }

  private fun observeViewModel() {
    viewModel.taskUpdate.observe(viewLifecycleOwner) { updateTask ->
//      if (updateTask.status == Status.DOING) {
//        val oldList = taskAdapter.currentList
//
//        val newList = oldList.toMutableList().apply {
//          find { it.id == updateTask.id }?.description = updateTask.description
//        }
//
//        val position = newList.indexOfFirst { it.id == updateTask.id }
//
//        taskAdapter.submitList(newList)
//        taskAdapter.notifyItemChanged(position)
//      }
    }
  }

  private fun initRecyclerView() {
    taskAdapter = TaskAdapter(requireContext()) { task, option ->
      optionSelected(task, option)
    }

    with(binding.rvTasks) {
      layoutManager = LinearLayoutManager(requireContext())
      setHasFixedSize(true)
      adapter = taskAdapter
    }
  }

  private fun optionSelected(task: Task, option: Int) {
    when (option) {
      TaskAdapter.SELECT_DETAILS -> {
        Toast.makeText(
          requireContext(),
          "Detalhes da task: ${task.id}",
          Toast.LENGTH_SHORT
        ).show()
      }

      TaskAdapter.SELECT_EDIT -> {
        val action = HomeFragmentDirections
          .actionHomeFragmentToFormTaskFragment(task)
        findNavController().navigate(action)
      }

      TaskAdapter.SELECT_REMOVE -> {
        showBottomSheet(
          R.string.text_title_dialog_delete,
          R.string.text_button_dialog_confirm,
          getString(R.string.text_message_dialog_delete)
        ) { deleteTask(task) }
      }

      TaskAdapter.SELECT_NEXT -> {
        task.status = Status.DONE
        updateStatusTask(task)
      }

      TaskAdapter.SELECT_BACK -> {
        task.status = Status.TODO
        updateStatusTask(task)
      }
    }
  }

  private fun getTasks() {
    FirebaseHelper.getDatabase()
      .child("tasks")
      .child(FirebaseHelper.getIdUser())
      .addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
          val taskList = mutableListOf<Task>()
          for (ds in snapshot.children) {
            val task = ds.getValue(Task::class.java) as Task

            if (task.status == Status.DOING) {
              taskList.add(task)
            }
          }
          binding.progressBar.isVisible = false
          listEmpty(taskList)

          taskList.reverse()
          taskAdapter.submitList(taskList)
        }

        override fun onCancelled(error: DatabaseError) {
          Log.i("fireTask", "onCancelled")
        }
      })
  }

  private fun deleteTask(task: Task) {
    FirebaseHelper.getDatabase()
      .child("tasks")
      .child(FirebaseHelper.getIdUser())
      .child(task.id)
      .removeValue()
      .addOnCompleteListener { result ->
        if (result.isSuccessful) {
          Toast.makeText(
            requireContext(),
            R.string.text_remove_task_successful,
            Toast.LENGTH_SHORT
          ).show()
        } else {
          Toast.makeText(
            requireContext(),
            R.string.error_generic,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
  }

  private fun updateStatusTask(task: Task) {
    FirebaseHelper.getDatabase()
      .child("tasks")
      .child(FirebaseHelper.getIdUser())
      .child(task.id)
      .setValue(task).addOnCompleteListener { result ->
        if (result.isSuccessful) {
          Toast.makeText(
            requireContext(),
            R.string.text_update_sucess_form_task_fragment,
            Toast.LENGTH_SHORT
          ).show()
        } else {
          Toast.makeText(
            requireContext(),
            R.string.error_generic,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
  }

  private fun listEmpty(taskList: List<Task>) {
    binding.textInfo.text = if (taskList.isEmpty()) {
      getString(R.string.text_list_task_empty)
    } else {
      ""
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}