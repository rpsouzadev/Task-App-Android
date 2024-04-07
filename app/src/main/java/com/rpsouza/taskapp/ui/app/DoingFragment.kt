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
import com.rpsouza.taskapp.databinding.FragmentDoingBinding
import com.rpsouza.taskapp.ui.adapter.TaskAdapter
import com.rpsouza.taskapp.utils.StateView
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

    initRecyclerView()
    observeViewModel()
    viewModel.getTasks()
  }

  private fun observeViewModel() {
    viewModel.taskList.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          val taskList = stateView.data?.filter { it.status == Status.DOING }

          listEmpty(taskList ?: emptyList())
          taskAdapter.submitList(taskList)
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    viewModel.taskInsert.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          if (stateView.data?.status == Status.DONE) {
            val oldList = taskAdapter.currentList

            val newList = oldList.toMutableList().apply {
              add(0, stateView.data)
            }

            taskAdapter.submitList(newList)
            setPositionRecyclerView()
          }
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    viewModel.taskUpdate.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          val oldList = taskAdapter.currentList
          val newList = oldList.toMutableList().apply {
            if (!oldList.contains(stateView.data) && stateView.data?.status == Status.DOING) {
              add(0, stateView.data)
              setPositionRecyclerView()
            }


            if (stateView.data?.status == Status.DOING) {
              find { it.id == stateView.data.id }?.description = stateView.data.description
            } else {
              remove(stateView.data)
            }
          }

          val position = newList.indexOfFirst { it.id == stateView.data?.id }

          taskAdapter.submitList(newList)
          taskAdapter.notifyItemChanged(position)
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    viewModel.taskDelete.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          Toast.makeText(
            requireContext(),
            R.string.text_remove_task_successful,
            Toast.LENGTH_SHORT
          ).show()

          val oldList = taskAdapter.currentList
          val newList = oldList.toMutableList().apply {
            remove(stateView.data)
          }

          taskAdapter.submitList(newList)
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
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
        task.status = Status.DONE
        viewModel.updateTask(task)
      }

      TaskAdapter.SELECT_BACK -> {
        task.status = Status.TODO
        viewModel.updateTask(task)
      }
    }
  }

  private fun setPositionRecyclerView() {
    taskAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
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