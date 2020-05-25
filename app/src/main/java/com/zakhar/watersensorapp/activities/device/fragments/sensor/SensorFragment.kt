package com.zakhar.watersensorapp.activities.device.fragments.sensor

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.databinding.FragmentSettingsSensorBinding
import com.zakhar.watersensorapp.models.SensorDevice
import kotlinx.android.synthetic.main.fragment_settings_sensor.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.coroutines.CoroutineContext

class SensorFragment : Fragment(), CoroutineScope {
    private val TAG = "SensorFragment"
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var job = Job()

    private lateinit var binding: FragmentSettingsSensorBinding
    private lateinit var chart: LineChart

    var data = LineData()
    lateinit var timer: Timer
    var startTime: Long = 0
    val callPeriod = 1500L

    val sensorMaxValue = 4096
    var currentRightBorder = 3000
    val sensorMinValue = 0
    var currentLeftBorder = 1000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings_sensor,
            container,
            false
        )

        chart = binding.realtimeChart
        initChart()
        startTime = System.currentTimeMillis()

        timer = Timer("FetchValueTimer", false)
        timer.scheduleAtFixedRate(timerTask { fetchValue() }, 0, callPeriod)

        // todo: add checks for min to be < than max

        binding.borderMin.apply {
            minValue = sensorMinValue
            maxValue = sensorMaxValue
            value = currentLeftBorder
            setOnValueChangedListener { picker, oldVal, newVal ->
                currentLeftBorder = newVal
            }
        }

        binding.borderMax.apply {
            minValue = sensorMinValue
            maxValue = sensorMaxValue
            value = currentRightBorder
            setOnValueChangedListener { picker, oldVal, newVal ->
                currentRightBorder = newVal
            }
        }

        return binding.root
    }

    fun initChart() {
        chart.description.isEnabled = true
        chart.description.text = "Real time sensor value"
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.isScaleXEnabled = true
        chart.setPinchZoom(true)

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.isEnabled = true
        chart.xAxis.setDrawAxisLine(true)
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawLabels(false)

        chart.data = data

        data.addDataSet(createSet(Color.GREEN, "Sensor value"))
        data.addDataSet(createBorderSet(Color.BLACK, "left notification border"))
        data.addDataSet(createBorderSet(Color.RED, "right notification border"))
//        chart.setVisibleXRangeMinimum(1f)
//        chart.setVisibleXRangeMaximum(1000f)
    }

    fun createSet(color: Int, name: String): LineDataSet {
        var set = LineDataSet(null, name)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = color
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        set.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
        return set
    }

    fun createBorderSet(color: Int, name: String): LineDataSet {
        var set = LineDataSet(null, name)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 2f
        set.color = color
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        set.setDrawCircles(false)
        set.setDrawCircleHole(false)
        set.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
        return set
    }

    fun fetchValue() {
        launch {
            val value = SensorDevice.getInstance()?.getSensorValue() ?: 0L
//            var value = 1000 + System.currentTimeMillis() % 1000 * 2
            addValue(value)

            Log.d(TAG, "fetching value " + value.toString())
        }
    }

    fun addValue(value: Long) {
        val x = (System.currentTimeMillis() - startTime) / 1000f
//        val x = data.dataSets[0].entryCount.toFloat()
        data.addEntry(Entry(x, value.toFloat()), 0)
        data.addEntry(Entry(x, currentLeftBorder.toFloat()), 1)
        data.addEntry(Entry(x, currentRightBorder.toFloat()), 2)
        data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRange(10f, 10f);
        chart.moveViewToAnimated(x, value.toFloat(), YAxis.AxisDependency.LEFT, callPeriod / 2)
    }

    override fun onDestroy() {
        timer.cancel()
        job.cancel()
        super.onDestroy()
    }
}
