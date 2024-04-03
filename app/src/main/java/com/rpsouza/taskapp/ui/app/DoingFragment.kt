package com.rpsouza.taskapp.ui.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.databinding.FragmentDoingBinding
import com.rpsouza.taskapp.databinding.FragmentTodoBinding
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
        taskAdapter = TaskAdapter(taskList)

        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.setHasFixedSize(true)
        binding.rvTasks.adapter = taskAdapter
    }

    private fun getTasks(): List<Task> {
        return listOf(
            Task("1", "Criar nova tela do app", Status.TODO),
            Task("2", "Criar nova tela do app", Status.TODO),
            Task("3", "Criar nova tela do app", Status.TODO),
            Task("4", "Criar nova tela do app", Status.TODO),
            Task("5", "Criar nova tela do app", Status.TODO)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}