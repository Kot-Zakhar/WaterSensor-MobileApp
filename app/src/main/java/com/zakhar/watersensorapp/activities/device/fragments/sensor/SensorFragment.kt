package com.zakhar.watersensorapp.activities.device.fragments.sensor

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.zakhar.watersensorapp.R
import kotlinx.android.synthetic.main.fragment_settings_sensor.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.time.Instant
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.coroutines.CoroutineContext

class SensorFragment : Fragment(), CoroutineScope {
    private val TAG = "SensorFragment"
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var job = Job()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_sensor, container, false)
    }

    var data = LineData()
    lateinit var timer: Timer

//    var valueSet: LineDataSet = createSet(Color.BLACK, "Sensor value")
//    var leftBorderSet: LineDataSet = createSet(Color.RED, "left notification border")
//    var rightBorderSet: LineDataSet = createSet(Color.RED, "right notification border")

    val maxValue = 4096
    var currentRightBorder = maxValue
    val minValue = 0
    var currentLeftBorder = minValue

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initChart()

        timer = Timer("FetchValueTimer", false)
        timer.scheduleAtFixedRate(timerTask { fetchValue() }, 0, 500)
    }

    fun initChart() {
        val chart = fragment_settings_sensor__realtime__line_chart
        chart.description.isEnabled = true
        chart.description.text = "Real time sensor value"
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.isScaleYEnabled = false
        chart.isScaleXEnabled = false
        chart.setMaxVisibleValueCount(maxValue)
        chart.setPinchZoom(false)
        chart.isAutoScaleMinMaxEnabled = false

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.isEnabled = true
        chart.xAxis.setDrawAxisLine(true)
        chart.xAxis.setDrawGridLines(true)
        chart.xAxis.setDrawLabels(true)

//        fragment_settings_sensor__realtime__line_chart.setVisibleXRangeMinimum(100f)
//        fragment_settings_sensor__realtime__line_chart.setVisibleXRangeMaximum(1000f)

        chart.data = data

        data.addDataSet(createSet(Color.BLACK, "Sensor value"))
        data.addDataSet(createSet(Color.RED, "left notification border"))
        data.addDataSet(createSet(Color.RED, "right notification border"))
    }

    fun createSet(color: Int, name: String): LineDataSet {
        var set = LineDataSet(null, name)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = color
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    fun fetchValue() {
//        launch {
            var value = 1000 + Instant.now().epochSecond % 2000
            addValue(value.toInt())

            Log.d(TAG, "fetching value " + value.toString())
//        }
    }

    fun addValue(value: Int) {
//        val x = Instant.now().epochSecond.toFloat()
        val x = data.dataSets[0].entryCount.toFloat()
        data.addEntry(Entry(x, value.toFloat()), 0)
//        valueSet.addEntry(Entry(x, value.toFloat()))
//        valueSet.notifyDataSetChanged()
        data.addEntry(Entry(x, currentLeftBorder.toFloat()), 1)
//        leftBorderSet.addEntry()
//        leftBorderSet.notifyDataSetChanged()
        data.addEntry(Entry(x, currentRightBorder.toFloat()), 2)
//        rightBorderSet.addEntry(Entry(x, currentRightBorder.toFloat()))
//        rightBorderSet.notifyDataSetChanged()
        data.notifyDataChanged()
        fragment_settings_sensor__realtime__line_chart.xAxis.mAxisMaximum = x
        fragment_settings_sensor__realtime__line_chart.moveViewToX(x)
    }

    override fun onDestroy() {
        timer.cancel()
        job.cancel()
        super.onDestroy()
    }
}
