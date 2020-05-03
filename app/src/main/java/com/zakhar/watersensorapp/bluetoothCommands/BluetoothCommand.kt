package com.zakhar.watersensorapp.bluetoothCommands

import android.bluetooth.BluetoothSocket
import android.icu.text.UnicodeSet
import android.util.Log
import kotlinx.coroutines.yield
import java.io.IOException
import java.nio.charset.Charset

abstract class BluetoothCommand() {
    abstract fun execute()
}