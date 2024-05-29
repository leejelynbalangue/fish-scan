package com.ccs.fish.scan.data.capture

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CaptureViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val database = AppDatabase.getInstance(context)
    private val repository: CaptureRepository = CaptureRepository(database.captureDao())

    val allCaptures: Flow<List<Capture>> = repository.allCaptures
    val allCapturesByDate: Flow<List<String>> = repository.allCapturesByDate

    fun getCaptureById(id: Int): Flow<Capture> {
        return repository.getCaptureById(id)
            .flowOn(Dispatchers.IO) // Run on IO thread
    }

    fun getLatestCapture(): Flow<Capture> {
        return repository.getLatestCapture()
            .flowOn(Dispatchers.IO) // Run on IO thread
    }

    fun insertCapture(capture: Capture) {
        viewModelScope.launch {
            repository.insertCapture(capture)
        }
    }

    fun deleteCaptureById(id: Int) {
        viewModelScope.launch {
            repository.deleteCaptureById(id)
        }
    }

    fun deleteAllCaptures() {
        viewModelScope.launch {
            repository.deleteAllCaptures()
        }
    }

    fun getCapturesByDate(date: String): Flow<List<Capture>> {
        return repository.getCapturesByDate(date)
            .flowOn(Dispatchers.IO) // Run on IO thread
    }

    fun getTotalLadyFishCountByDate(date: String): Flow<Int> {
        return repository.getTotalLadyFishCountByDate(date)
            .flowOn(Dispatchers.IO) // Run on IO thread
    }
    // LiveData to track when an capture is deleted
//    private val _onCaptureDeleted = MutableStateFlow<Boolean?>(null)
//    val onCaptureDeleted: StateFlow<Boolean?> = _onCaptureDeleted
//
//    // Function to notify that an capture is deleted
//    fun notifyCaptureDeleted() {
//        _onCaptureDeleted.value = true
//    }
//
//    fun resetCaptureDeleted() {
//        _onCaptureDeleted.value = null
//    }

}