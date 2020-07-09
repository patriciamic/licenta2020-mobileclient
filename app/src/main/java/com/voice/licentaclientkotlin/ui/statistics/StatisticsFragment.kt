package com.voice.licentaclientkotlin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.voice.licentaclientkotlin.R
import kotlinx.android.synthetic.main.fragment_raport.*


class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_raport, container, false)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChartToday()
        initChartWeek()
    }

    private fun initChartToday() {
        val lineEntries = ArrayList<Entry>()
        lineEntries.add(Entry(0f, 2f))
        lineEntries.add(Entry(1f, 4f))
        lineEntries.add(Entry(2f, 3f))
        lineEntries.add(Entry(3f, 3.2f))
        lineEntries.add(Entry(4f, 3.4f))
        lineEntries.add(Entry(5f, 4f))

        val lineDataSet1 = LineDataSet(lineEntries, "Today's positivity")
        lineDataSet1.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet1.lineWidth = 3f
        lineDataSet1.isHighlightEnabled = true
        lineDataSet1.circleRadius = 6f
        lineDataSet1.circleHoleRadius = 3f
        lineDataSet1.setDrawHighlightIndicators(true)
        lineDataSet1.highLightColor = R.color.colorSecondaryAccent

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet1)

        val data = LineData(dataSets)

        lineChartViewToday.data = data

        lineChartViewToday.axisLeft.mAxisMaximum = 1f
        lineChartViewToday.axisLeft.mAxisMinimum = -1f
        lineChartViewToday.axisLeft.mAxisRange = 2f
        lineChartViewToday.invalidate() // refresh

        lineChartViewToday.xAxis.valueFormatter = IndexAxisValueFormatter(getXAxisValuesForToday());
    }

    private fun initChartWeek() {
        val lineEntries = ArrayList<Entry>()
        lineEntries.add(Entry(0f, 3f))
        lineEntries.add(Entry(1f, 4f))
        lineEntries.add(Entry(2f, 5f))
        lineEntries.add(Entry(3f, 4f))
        lineEntries.add(Entry(4f, 6f))
        lineEntries.add(Entry(5f, 7f))

        val lineDataSet1 = LineDataSet(lineEntries, "Positivity")
        lineDataSet1.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet1.lineWidth = 3f
        lineDataSet1.setColors(getColor(context!!, R.color.colorSecondaryAccent))
        lineDataSet1.setCircleColor(getColor(context!!, R.color.colorSecondaryAccent));
        lineDataSet1.isHighlightEnabled = true
        lineDataSet1.circleRadius = 6f
        lineDataSet1.circleHoleRadius = 3f
        lineDataSet1.setDrawHighlightIndicators(true)
        lineDataSet1.highLightColor = R.color.colorSecondaryAccent
//        lineDataSet1.mode = LineDataSet.Mode.CUBIC_BEZIER

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet1)

        val data = LineData(dataSets)


        lineChartViewWeek.data = data

        lineChartViewWeek.axisLeft.mAxisMaximum = 1f
        lineChartViewWeek.axisLeft.mAxisMinimum = -1f
        lineChartViewWeek.axisLeft.mAxisRange = 2f
        lineChartViewWeek.invalidate() // refresh

        lineChartViewWeek.xAxis.valueFormatter = IndexAxisValueFormatter(getXAxisValues());
    }

    private fun getXAxisValues(): ArrayList<String>? {
        val labels = ArrayList<String>()
        labels.add("SUN")
        labels.add("MON")
        labels.add("WED")
        labels.add("THU")
        labels.add("FRI")
        labels.add("SAT")
        return labels
    }

    private fun getXAxisValuesForToday(): ArrayList<String>? {
        val labels = ArrayList<String>()
        for (x in 6..23){
            labels.add("$x:00")
        }
        return labels
    }


}
