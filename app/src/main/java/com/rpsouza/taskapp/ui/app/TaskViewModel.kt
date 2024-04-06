package com.rpsouza.taskapp.ui.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.utils.FirebaseHelper

class TaskViewModel : ViewModel() {
  private val _taskList = MutableLiveData<List<Task>>()
  val taskList: LiveData<List<Task>> = _taskList

  private val _taskInsert = MutableLiveData<Task>()
  val taskInsert: LiveData<Task> = _taskInsert

  private val _taskUpdate = MutableLiveData<Task>()
  val taskUpdate: LiveData<Task> = _taskUpdate

  private val _taskDelete = MutableLiveData<Task>()
  val taskDelete: LiveData<Task> = _taskDelete

  fun getTasks(status: Status) {
    FirebaseHelper.getDatabase()
      .child("tasks")
      .child(FirebaseHelper.getIdUser())
      .addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
          val taskList = mutableListOf<Task>()

          for (ds in snapshot.children) {
            val task = ds.getValue(Task::class.java) as Task

            if (task.status == status) {
              taskList.add(task)
            }
          }

          taskList.reverse()
          _taskList.postValue(taskList)
        }

        override fun onCancelled(error: DatabaseError) {
          Log.i("fireTask", "onCancelled")
        }
      })
  }

  fun insertTask(task: Task) {
    FirebaseHelper.getDatabase()
      .child("tasks")
      .child(FirebaseHelper.getIdUser())
      .child(task.id)
      .setValue(task).addOnCompleteListener { result ->
        if (result.isSuccessful) {
          _taskInsert.postValue(task)
        }

      }
  }

  fun updateTask(task: Task) {
    val map = mapOf(
      "description" to task.description,
      "status" to task.status
    )

    FirebaseHelper.getDatabase()
      .child("tasks")
      .child(FirebaseHelper.getIdUser())
      .child(task.id)
      .updateChildren(map).addOnCompleteListener { result ->
        if (result.isSuccessful) {
          _taskUpdate.postValue(task)
        }
      }
  }

  fun deleteTask(task: Task) {
    FirebaseHelper.getDatabase()
      .child("tasks")
      .child(FirebaseHelper.getIdUser())
      .child(task.id)
      .removeValue()
      .addOnCompleteListener { result ->
        if (result.isSuccessful) {
          _taskDelete.postValue(task)
        }
      }
  }
}