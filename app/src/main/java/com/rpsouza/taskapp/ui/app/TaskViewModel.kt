package com.rpsouza.taskapp.ui.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rpsouza.taskapp.data.model.Task

class TaskViewModel : ViewModel() {
  private val _taskUpdate = MutableLiveData<Task>()
  val taskUpdate: LiveData<Task> = _taskUpdate

  fun setUpdateTask(task: Task) {
    _taskUpdate.value = task
  }
}