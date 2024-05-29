package com.ccs.fish.scan.data.capture

import kotlinx.coroutines.flow.Flow

class CaptureRepository(private val captureDao: CaptureDao) {
    val allCaptures: Flow<List<Capture>> = captureDao.getAllCaptures()

    // Convert yyyy-MM-dd to dd MMMM, yyyy, ex: 2021-08-01 to 01 August, 2021
    val allCapturesByDate: Flow<List<String>> = captureDao.getUniqueCaptureDates()
//        .map { dates ->
//            dates.map { date ->
//                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val outputFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
//                val parsedDate = inputFormat.parse(date)
//                outputFormat.format(parsedDate)
//            }
//        }

    fun getLatestCapture(): Flow<Capture> {
        return captureDao.getLatestCapture()
    }

    fun getCaptureById(id: Int): Flow<Capture> {
        return captureDao.getCaptureById(id)
    }

    suspend fun insertCapture(capture: Capture) {
        captureDao.insertCapture(capture)
    }

    suspend fun deleteCaptureById(id: Int) {
        captureDao.deleteCaptureById(id)
    }

    suspend fun deleteAllCaptures() {
        captureDao.deleteAllCaptures()
    }

    fun getCapturesByDate(date: String): Flow<List<Capture>> {
        return captureDao.getCapturesByDate(date)
    }

    fun getTotalLadyFishCountByDate(date: String): Flow<Int> {
        return captureDao.getTotalLadyFishCountByDate(date)
    }
}