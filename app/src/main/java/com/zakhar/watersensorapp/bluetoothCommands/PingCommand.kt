package com.zakhar.watersensorapp.bluetoothCommands

import android.bluetooth.BluetoothSocket
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PingCommand (private val coroutineScope: CoroutineScope,
                   bluetoothSocket: BluetoothSocket,
                   private val button: Button,
                   private val textView: TextView
) : BluetoothCommand(bluetoothSocket) {

    private val TAG = "PingCommand"
    init {
        button.setOnClickListener{ Execute() }
    }

    fun Execute() {
        coroutineScope.launch (Dispatchers.Main) {
            button.isEnabled = false
            val startTime = System.nanoTime()
            val answer = withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
                sendCommandWithFeedback("ping\n")
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