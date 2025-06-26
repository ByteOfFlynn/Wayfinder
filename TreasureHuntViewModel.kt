package com.example.wayfinder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.atan2

class TreasureHuntViewModel(application: Application) : AndroidViewModel(application) {

    // list of clues
    private val _currentClueIndex = MutableStateFlow(0)
    val currentClueIndex: StateFlow<Int> = _currentClueIndex

    // timer
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    // timer state
    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning

    // load json clues file
    val clues: List<Clue> = loadCluesFromResource()

    init {
        viewModelScope.launch {
            while (true) {
                if (_timerRunning.value) {
                    delay(1000L)
                    _elapsedTime.value += 1
                } else {
                    delay(1000L)
                }
            }
        }
    }

    fun startTimer() {
        _timerRunning.value = true
        _elapsedTime.value = 0L
        _currentClueIndex.value = 0
    }

    fun pauseTimer() {
        _timerRunning.value = false
    }

    fun resumeTimer() {
        _timerRunning.value = true
    }

    fun nextClue() {
        if (_currentClueIndex.value < clues.size - 1) {
            _currentClueIndex.value += 1
        } else {
            _timerRunning.value = false
        }
    }

    fun getCurrentClue(): Clue? = clues.getOrNull(_currentClueIndex.value)

    fun hasMoreClues(): Boolean = _currentClueIndex.value < clues.size - 1

    // load clues
    private fun loadCluesFromResource(): List<Clue> {
        val context = getApplication<Application>().applicationContext
        val inputStream = context.resources.openRawResource(R.raw.clues)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val listType = object : TypeToken<List<Clue>>() {}.type
        return gson.fromJson(jsonString, listType)
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}
