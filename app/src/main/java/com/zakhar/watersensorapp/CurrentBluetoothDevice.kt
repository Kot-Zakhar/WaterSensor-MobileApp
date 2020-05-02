package com.zakhar.watersensorapp

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

class CurrentBluetoothDevice {
    companion object {
        var device: BluetoothDevice? = null
        private var socket : BluetoothSocket? = null

        fun setSocket(socket: BluetoothSocket) {
            if (this.socket != null) {
                this.socket?.close()
            }
            this.socket = socket
        }

        fun getSocket(): BluetoothSocket? {
            return this.socket
        }
    }
}