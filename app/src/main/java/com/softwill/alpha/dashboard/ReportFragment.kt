package com.softwill.alpha.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentReportBinding
import kotlin.random.Random


class ReportFragment : Fragment() {


    private lateinit var binding: FragmentReportBinding
    var arrayColors = intArrayOf(
        ColorTemplate.rgb("#267DB3"),
        ColorTemplate.rgb("#FFFFFF"),
    )

    private val MAX_X_VALUE = 12
    private val MAX_Y_VALUE = 100
    private val MIN_Y_VALUE = 5
    private val SET_LABEL = "Monthly Report"
    private val DAYS = arrayOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false);
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPieChart("87", "13");

        val data: BarData = createChartData()
        setupBarChart();
        prepareChartData(data);

    }

    private fun createChartData(): BarData {
        val values: ArrayList<BarEntry> = ArrayList()
        for (i in 0 until MAX_X_VALUE) {
            val x = i.toFloat()
            val y: Float = getRandom(MIN_Y_VALUE, MAX_Y_VALUE)
            values.add(BarEntry(x, y))
        }
        val set1 = BarDataSet(values, SET_LABEL)
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        return BarData(dataSets)
    }

    private fun getRandom(min: Int, max: Int): Float {
        require(min < max) { "Invalid range [$min, $max]" }
        return min + Random.nextFloat() * (max - min)
    }


    private fun prepareChartData(data: BarData) {
        data.setValueTextSize(12f)
        binding.barChart.setData(data)
        binding.barChart.invalidate()
    }

    private fun setupBarChart() {
        binding.barChart.description.isEnabled = false;
        binding.barChart.setDrawValueAboveBar(false);
        binding.barChart.setPinchZoom(false)
        binding.barChart.setDrawBarShadow(false)
        binding.barChart.setDrawGridBackground(false)

        binding.barChart.getAxisLeft().setDrawGridLines(false)
        binding.barChart.animateY(1500)
        binding.barChart.getLegend().setEnabled(false)

        val xAxis: XAxis = binding.barChart.getXAxis()
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DAYS[value.toInt()]
            }
        }

        val axisLeft: YAxis = binding.barChart.axisLeft
        axisLeft.granularity = 10f
        axisLeft.axisMinimum = 0f

        val axisRight: YAxis = binding.barChart.axisRight
        axisRight.granularity = 10f
        axisRight.axisMinimum = 0f


        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)


    }

    private fun setupPieChart(Protein: String, Fat: String) {
        binding.piechart.invalidate()
        binding.piechart.animateXY(2100, 2100)
        binding.piechart.setUsePercentValues(true)
        binding.piechart.isDrawHoleEnabled = true
        binding.piechart.holeRadius = 65f
        binding.piechart.transparentCircleRadius = 0f
        binding.piechart.setHoleColor(Color.TRANSPARENT)
        binding.piechart.rotationAngle = 0F
        binding.piechart.isHighlightPerTapEnabled = true
        binding.piechart.setTouchEnabled(false)
        binding.piechart.isRotationEnabled = false
        binding.piechart.setEntryLabelTextSize(12F)
        val pieEntires = ArrayList<PieEntry>()
        pieEntires.add(PieEntry(Protein.toFloat(), "", 0)) //Protein
        pieEntires.add(PieEntry(Fat.toFloat(), "", 1)) //Fat


        val dataSet = PieDataSet(pieEntires, null)
        dataSet.sliceSpace = 0f
        dataSet.iconsOffset = MPPointF(0F, 20F)
        dataSet.selectionShift = 5f


        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        binding.piechart.setData(data)
        binding.piechart.getDescription().setEnabled(false)
        dataSet.colors = ColorTemplate.createColors(arrayColors)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.TRANSPARENT)
        val l: Legend = binding.piechart.getLegend()
        l.isEnabled = false
    }

}