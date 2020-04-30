package com.zakhar.watersensorapp

import android.bluetooth.BluetoothSocket

class SocketHandler {
    companion object {
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