package com.rpsouza.taskapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.databinding.ItemTaskBinding

class TaskAdapter(
    private val context: Context,
    private val taskList: List<Task>,
    private val taskSelected: (Task, option: Int) -> Unit
) :
    RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {
    companion object {
        const val SELECT_BACK: Int = 1
        const val SELECT_REMOVE: Int = 2
        const val SELECT_EDIT: Int = 3
        const val SELECT_DETAILS: Int = 4
        const val SELECT_NEXT: Int = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.textDescription.text = task.description
        setIndicators(task, holder)

        holder.binding.btnEdit.setOnClickListener { taskSelected(task, SELECT_EDIT) }
        holder.binding.btnDelete.setOnClickListener { taskSelected(task, SELECT_REMOVE) }
        holder.binding.btnDetails.setOnClickListener { taskSelected(task, SELECT_DETAILS) }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun setIndicators(task: Task, holder: MyViewHolder) {
        val binding = holder.binding

        when (task.status) {
            Status.TODO -> {
                binding.btnBack.isVisible = false

                binding.btnNext.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }

            Status.DOING -> {
                binding.btnBack.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.color_status_todo
                    )
                )

                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.color_status_done
                    )
                )

                binding.btnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
                binding.btnNext.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }

            Status.DONE -> {
                binding.btnNext.isVisible = false

                binding.btnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
            }
        }
    }

    inner class MyViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)
}
