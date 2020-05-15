package com.zakhar.watersensorapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_realtime.*
import java.util.*

class RealtimeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime)

        activity_realtime__chart.description.isEnabled = true;
        activity_realtime__chart.description.text = "Real time sensor value";

        activity_realtime__chart.setTouchEnabled(false)
        activity_realtime__chart.isDragEnabled = false
        activity_realtime__chart.isScaleXEnabled = false
        activity_realtime__chart.isScaleYEnabled = false
//        activity_realtime__chart.setDrawGridBackground(false)
//        activity_realtime__chart.setPinchZoom(false);

        val data = LineData()
        activity_realtime__chart.data = data;
    }

    fun createSet(): LineDataSet {
        var set = LineDataSet(null, "Sensor value")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.BLACK
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    fun addValue(value: Int) {
        val data = activity_realtime__chart.data
        var set = data.getDataSetByIndex(0)
        if (set != null) {
            set = createSet();
            data.addDataSet(set)
        }

        data.addEntry(Entry(set.entryCount.toFloat(), value.toFloat()), 0)
        data.notifyDataChanged()

        activity_realtime__chart.setMaxVisibleValueCount(4096)
        activity_realtime__chart.moveViewToX(data.entryCount.toFloat())
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
