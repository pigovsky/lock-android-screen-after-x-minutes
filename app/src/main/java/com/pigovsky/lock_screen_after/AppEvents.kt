package com.pigovsky.lock_screen_after


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object AppEvents {
    // LiveData to post messages from the receiver to the activity
    private val _alarmMessage = MutableLiveData<String>()
    val alarmMessage: LiveData<String> = _alarmMessage

    // Optional: For one-shot events to prevent re-delivery on configuration change
    private val _eventConsumed = MutableLiveData<Boolean>()
    val eventConsumed: LiveData<Boolean> = _eventConsumed

    fun postAlarmMessage(message: String) {
        // Use postValue as this might be called from a background thread (BroadcastReceiver)
        _alarmMessage.postValue(message)
    }

    fun consumeEvent() {
        _eventConsumed.postValue(true)
    }

    fun resetEventConsumed() {
        _eventConsumed.postValue(false) // Or set to null if you prefer
    }
}