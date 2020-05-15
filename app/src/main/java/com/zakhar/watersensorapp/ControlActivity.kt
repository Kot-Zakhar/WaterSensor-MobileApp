package com.zakhar.watersensorapp

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.bluetooth.commands.PingCommand
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class ControlActivity: AppCompatActivity(), CoroutineScope {
    private val TAG = "ControlActivity"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job = Job()
    private var socket: BluetoothSocket? = null

    fun bindControls() {
        if (socket == null){
            Log.e(TAG, "Socket is null. Cannot bind controls.")
            return
        }

        PingCommand(this, activity_control__ping__button, activity_control__ping__text_view)

//        activity_control__wifi__button.setOnClickListener{
//            val intent = Intent(this, WifiActivity::class.java)
//            ContextCompat.startActivity(this, intent, null)
//        }
//
//        activity_control__email__button.setOnClickListener{
//            var intent = Intent(this, EmailActivity::class.java)
//            ContextCompat.startActivity(this, intent, null)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        socket = CurrentBluetoothDevice.getSocket()

        if (socket == null) {
            Log.i(TAG, "Socket is null.")
            finish()
        }

        bindControls()
    }

    override fun onDestroy() {
        socket?.close()
        CurrentBluetoothDevice.setSocket(null)
        CurrentBluetoothDevice.device = null;
        job.cancel()
        super.onDestroy()
    }

}