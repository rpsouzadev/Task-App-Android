package com.rpsouza.taskapp.ui.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.databinding.FragmentTodoBinding
import com.rpsouza.taskapp.ui.adapter.TaskAdapter

class TodoFragment : Fragment() {
  private var _binding: FragmentTodoBinding? = null
  private val binding get() = _binding!!

  private lateinit var taskAdapter: TaskAdapter

  private lateinit var reference: DatabaseReference
  private lateinit var auth: FirebaseAuth

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentTodoBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    reference = Firebase.database.reference
    auth = Firebase.auth

    initListener()
    initRecyclerView()
    getTasks()
  }

  private fun initListener() {
    binding.floatingActionButton.setOnClickListener {
      findNavController().navigate(R.id.action_homeFragment_to_formTaskFragment)
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
        Toast.makeText(
          requireContext(),
          "Editar task: ${task.id}",
          Toast.LENGTH_SHORT
        ).show()
      }

      TaskAdapter.SELECT_REMOVE -> {
        Toast.makeText(
          requireContext(),
          "Remover task: ${task.id}",
          Toast.LENGTH_SHORT
        ).show()
      }

      TaskAdapter.SELECT_NEXT -> {
        Toast.makeText(
          requireContext(),
          "Avançar task: ${task.id}",
          Toast.LENGTH_SHORT
        ).show()
      }
    }
  }

  private fun getTasks() {
    reference
      .child("tasks")
      .child(auth.currentUser?.uid ?: "")
      .addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
          val taskList = mutableListOf<Task>()
          for (ds in snapshot.children) {
            val task = ds.getValue(Task::class.java) as Task

            if (task.status == Status.TODO) {
              taskList.add(task)
            }
          }
          binding.progressBar.isVisible = false
          listEmpty(taskList)

          taskAdapter.submitList(taskList)
        }

        override fun onCancelled(error: DatabaseError) {
          Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
        }
      })
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