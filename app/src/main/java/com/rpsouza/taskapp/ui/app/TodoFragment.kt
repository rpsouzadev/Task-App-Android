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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.databinding.FragmentTodoBinding
import com.rpsouza.taskapp.ui.adapter.TaskAdapter
import com.rpsouza.taskapp.utils.showBottomSheet

class TodoFragment : Fragment() {
  private var _binding: FragmentTodoBinding? = null
  private val binding get() = _binding!!

  private lateinit var taskAdapter: TaskAdapter

  private val viewModel: TaskViewModel by activityViewModels()

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

    initListener()
    initRecyclerView()
    observeViewModel()
    viewModel.getTasks(Status.TODO)
  }

  private fun initListener() {
    binding.floatingActionButton.setOnClickListener {
      val action = HomeFragmentDirections.actionHomeFragmentToFormTaskFragment(null)
      findNavController().navigate(action)
    }
  }

  private fun observeViewModel() {
    viewModel.taskList.observe(viewLifecycleOwner) { taskList ->
      binding.progressBar.isVisible = false
      listEmpty(taskList)

      taskAdapter.submitList(taskList)
    }

    viewModel.taskInsert.observe(viewLifecycleOwner) { task ->
      if (task.status == Status.TODO) {
        val oldList = taskAdapter.currentList

        val newList = oldList.toMutableList().apply {
          add(0, task)
        }

        taskAdapter.submitList(newList)
        setPositionRecyclerView()
      }
    }

    viewModel.taskUpdate.observe(viewLifecycleOwner) { updateTask ->
      val oldList = taskAdapter.currentList

      val newList = oldList.toMutableList().apply {
        if (updateTask.status == Status.TODO) {
          find { it.id == updateTask.id }?.description = updateTask.description
        } else {
          remove(updateTask)
        }
      }

      val position = newList.indexOfFirst { it.id == updateTask.id }

      taskAdapter.submitList(newList)
      taskAdapter.notifyItemChanged(position)
    }

    viewModel.taskDelete.observe(viewLifecycleOwner) { task ->
      Toast.makeText(
        requireContext(),
        R.string.text_remove_task_successful,
        Toast.LENGTH_SHORT
      ).show()

      val oldList = taskAdapter.currentList
      val newList = oldList.toMutableList().apply {
        remove(task)
      }

      taskAdapter.submitList(newList)
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
        ) { viewModel.deleteTask(task) }
      }

      TaskAdapter.SELECT_NEXT -> {
        task.status = Status.DOING
        viewModel.updateTask(task)
      }
    }
  }

  private fun setPositionRecyclerView() {
    taskAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        binding.rvTasks.scrollToPosition(0)
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