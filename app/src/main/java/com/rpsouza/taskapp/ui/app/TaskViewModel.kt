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
import com.rpsouza.taskapp.utils.StateView

class TaskViewModel : ViewModel() {
  private val _taskList = MutableLiveData<StateView<List<Task>>>()
  val taskList: LiveData<StateView<List<Task>>> = _taskList

  private val _taskInsert = MutableLiveData<StateView<Task>>()
  val taskInsert: LiveData<StateView<Task>> = _taskInsert

  private val _taskUpdate = MutableLiveData<StateView<Task>>()
  val taskUpdate: LiveData<StateView<Task>> = _taskUpdate

  private val _taskDelete = MutableLiveData<StateView<Task>>()
  val taskDelete: LiveData<StateView<Task>> = _taskDelete

  fun getTasks(status: Status) {
    try {
      _taskList.postValue(StateView.OnLoading())

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
            _taskList.postValue(StateView.OnSuccess(taskList))
          }

          override fun onCancelled(error: DatabaseError) {
            Log.i("fireTask", "onCancelled")
          }
        })
    } catch (e: Exception) {
      _taskList.postValue(StateView.OnError(e.message))
    }
  }

  fun insertTask(task: Task) {
    try {
      _taskInsert.postValue(StateView.OnLoading())

      FirebaseHelper.getDatabase()
        .child("tasks")
        .child(FirebaseHelper.getIdUser())
        .child(task.id)
        .setValue(task).addOnCompleteListener { result ->
          if (result.isSuccessful) {
            _taskInsert.postValue(StateView.OnSuccess(task))
          }
        }
    } catch (e: Exception) {
      _taskInsert.postValue(StateView.OnError(e.message))
    }
  }

  fun updateTask(task: Task) {
    try {
      _taskUpdate.postValue(StateView.OnLoading())

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
            _taskUpdate.postValue(StateView.OnSuccess(task))
          }
        }
    } catch (e: Exception) {
      _taskUpdate.postValue(StateView.OnError(e.message))
    }
  }

  fun deleteTask(task: Task) {
    try {
      _taskDelete.postValue(StateView.OnLoading())

      FirebaseHelper.getDatabase()
        .child("tasks")
        .child(FirebaseHelper.getIdUser())
        .child(task.id)
        .removeValue()
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            _taskDelete.postValue(StateView.OnSuccess(task))
          }
        }
    } catch (e: Exception) {
      _taskDelete.postValue(StateView.OnError(e.message))
    }
  }
}