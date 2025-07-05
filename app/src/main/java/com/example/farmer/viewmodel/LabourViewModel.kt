package com.example.farmer.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.farmer.database.AppDatabase
import com.example.farmer.database.Labour
import com.example.farmer.database.WeightEntry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LabourViewModel(context: Context) : ViewModel() {
    private val labourDao = AppDatabase.getDatabase(context).labourDao()
    private val weightDao = AppDatabase.getDatabase(context).weightDao()

    private val _labours = MutableLiveData<List<Labour>>(emptyList())
    val labours: LiveData<List<Labour>> = _labours

    private val _weights = MutableLiveData<List<WeightEntry>>(emptyList())
    val weights: LiveData<List<WeightEntry>> = _weights

    private val _shouldPromptWorkingType = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val shouldPromptWorkingType: LiveData<Map<Int, Boolean>> = _shouldPromptWorkingType

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val currentDate: String
        get() = dateFormat.format(Date())

    init {
        loadLabours()
    }

    private fun loadLabours() {
        viewModelScope.launch {
            _labours.postValue(labourDao.getAllLabours())
        }
    }

    fun insertLabour(labour: Labour) {
        viewModelScope.launch {
            labourDao.insertLabour(labour)
            loadLabours()
        }
    }

    fun deleteLabour(labour: Labour) {
        viewModelScope.launch {
            labourDao.deleteLabour(labour)
            loadLabours()
        }
    }

    fun checkWorkingTypeForDate(labourId: Int, date: String) {
        viewModelScope.launch {
            val weights = weightDao.getWeightsForLabour(labourId)
            val hasEntryForDate = weights.any { it.date == date }
            _shouldPromptWorkingType.postValue(_shouldPromptWorkingType.value.orEmpty().toMutableMap().apply {
                this[labourId] = !hasEntryForDate
            })
        }
    }

    fun updateWorkingType(labourId: Int, newWorkingType: Boolean) {
        viewModelScope.launch {
            val labour = labourDao.getAllLabours().find { it.labourId == labourId }
            labour?.let {
                val updatedLabour = it.copy(workingType = newWorkingType)
                labourDao.insertLabour(updatedLabour)
                _shouldPromptWorkingType.postValue(_shouldPromptWorkingType.value.orEmpty().toMutableMap().apply {
                    this[labourId] = false
                })
                loadLabours()
            }
        }
    }

    fun addWeightEntry(labourId: Int, weights: MutableList<Int>, date: String) {
        viewModelScope.launch {
            val labour = labourDao.getAllLabours().find { it.labourId == labourId }
            if (labour?.workingType == true) {
                val weightEntry = WeightEntry(
                    labourOwnerId = labourId,
                    date = date,
                    weight = weights
                )
                weightDao.insertWeight(weightEntry)
                loadWeightsForLabour(labourId)
            }
        }
    }

    fun deleteLastWeight(labourId: Int) {
        viewModelScope.launch {
            val weights = weightDao.getWeightsForLabour(labourId)
            if (weights.isNotEmpty()) {
                weightDao.deleteWeight(weights.last())
                loadWeightsForLabour(labourId)
            }
        }
    }

    fun loadWeightsForLabour(labourId: Int) {
        viewModelScope.launch {
            _weights.postValue(weightDao.getWeightsForLabour(labourId))
        }
    }
}

class LabourViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LabourViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LabourViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}