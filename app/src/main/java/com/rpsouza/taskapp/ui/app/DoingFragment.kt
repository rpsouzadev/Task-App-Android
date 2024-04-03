package com.rpsouza.taskapp.ui.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.databinding.FragmentDoingBinding
import com.rpsouza.taskapp.ui.adapter.TaskAdapter

class DoingFragment : Fragment() {
    private var _binding: FragmentDoingBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

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
        initRecyclerView(getTasks())
    }

    private fun initRecyclerView(taskList: List<Task>) {
        taskAdapter = TaskAdapter(requireContext(), taskList) { task, option ->
            optionSelected(task, option)
        }

        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.setHasFixedSize(true)
        binding.rvTasks.adapter = taskAdapter
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

            TaskAdapter.SELECT_BACK -> {
                Toast.makeText(
                    requireContext(),
                    "Voltar task: ${task.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getTasks(): List<Task> {
        return listOf(
            Task("1", "Criar nova tela do app", Status.DOING),
            Task("2", "Criar nova tela do app", Status.DOING),
            Task("3", "Criar nova tela do app", Status.DOING),
            Task("4", "Criar nova tela do app", Status.DOING),
            Task("5", "Criar nova tela do app", Status.DOING)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}