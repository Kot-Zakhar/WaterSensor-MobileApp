package com.zakhar.watersensorapp.bluetooth.commands

import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PingCommand (private val coroutineScope: CoroutineScope,
                   private val button: Button,
                   private val textView: TextView
) {

    private val TAG = "PingCommand"
    init {
        button.setOnClickListener{ execute() }
    }

    fun execute() {
        coroutineScope.launch (Dispatchers.Main) {
            button.isEnabled = false
            val startTime = System.nanoTime()
            val answer = withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
                CurrentBluetoothDevice.sendCommandWithFeedback("ping")
            }
            val endTime = System.nanoTime()
            Log.i(TAG, "Answer: '$answer'")
            if (answer == "pong") {
                textView.text = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS).toString() + " ms"
            } else {
                textView.text = "Wrong answer"
            }
            button.isEnabled = true
        }
    }
}