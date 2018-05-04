package pl.kpob.dietdiary.views

import android.content.Context
import android.graphics.Color
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.jetbrains.anko.internals.AnkoInternals
import pl.kpob.dietdiary.domain.MealIngredient
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.hide
import pl.kpob.dietdiary.screens.PieChartScreen
import pl.kpob.dietdiary.show


/**
 * Created by kpob on 21.10.2017.
 */
class PieChartView(
        ctx: Context,
        private val nutrients: Map<String, Float>,
        private val ingredients: List<MealIngredient>
) : BaseScreenView<PieChartScreen>(ctx), ToolbarManager, OnChartValueSelectedListener, AnkoLogger {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val pieChart by lazy { find<PieChart>(R.id.pie_chart) }
    private val barChart by lazy { find<BarChart>(R.id.bar_chart) }
    private val list by lazy { find<RecyclerView>(R.id.ingredients) }
    private val container by lazy { find<NestedScrollView>(R.id.container) }

    var mode: PieChartScreen.ChartMode
        get() = AnkoInternals.noGetter()
        set(value) {
            when(value) {
                PieChartScreen.ChartMode.PIE -> { pieChart.show(); barChart.hide() }
                PieChartScreen.ChartMode.BAR  -> { barChart.show(); pieChart.hide() }
            }
        }

    init {
        inflate(ctx, R.layout.screen_pie_chart, this)
        initPieChart()
        initBarChart()
        initList()
    }

    private fun initPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.setOnChartValueSelectedListener(this)

        setData()

        with(pieChart.legend) {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(false)
            xEntrySpace = 7f
            yEntrySpace = 0f
            yOffset = 0f
        }

        // entry label styling
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
    }

    private fun initList() {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(View.inflate(context, R.layout.item_ingredient_2, null)) {}
            }

            override fun getItemCount(): Int = ingredients.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                holder?.itemView?.let {
                    it.find<TextView>(R.id.name).text = ingredients[position].name
                    it.find<TextView>(R.id.weight).text = String.format("%.2f g", ingredients[position].weight)
                }
            }

        }
        postDelayed( { container.scrollTo(0, 0) }, 100L)
    }

    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {}

    private fun setData() {
        val entries = nutrients.map {
            PieEntry(it.value, it.key)
        }

        val dataSet = PieDataSet(entries, "Składniki odżywcze").apply {
            setDrawIcons(false)
            sliceSpace = 3f
            iconsOffset = MPPointF(0f, 40f)
            selectionShift = 12f
            colors = CHART_COLORS
        }


        pieChart.data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setValueTextColor(Color.BLACK)
        }
        pieChart.highlightValues(null)
        pieChart.invalidate()
    }


    private fun initBarChart() {
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)

        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        barChart.xAxis.run {
            position = XAxis.XAxisPosition.BOTTOM

//            setValueFormatter { value: Float, axis: AxisBase? ->
//                val percent = (value / (axis?.mAxisRange ?: 1f)).toInt()
//                info { percent }
//                info { axis?.mAxisRange }
//
//                nutrients.keys.toList()[nutrients.size * percent]
//            }
        }

        barChart.axisLeft.run {
            setLabelCount(8, false)
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            spaceTop = 15f
            axisMinimum = 0f // this replaces setStartAtZero(true)
        }


        barChart.legend.run {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            form = Legend.LegendForm.SQUARE
            formSize = 9f
            textSize = 11f
            xEntrySpace = 4f
        }

        val entries = nutrients.values.mapIndexed { index, value -> BarEntry(index.toFloat(), value, "EEE") }
        val set = BarDataSet(entries, "Składniki odżywcze").apply {
            setDrawIcons(false)
            colors = CHART_COLORS
        }

        barChart.data = BarData(listOf(set)).apply {
            dataSetLabels
            setValueTextSize(10f)
            barWidth = 0.9f
        }
    }

    companion object {
        val CHART_COLORS = ArrayList<Int>().apply {
            addAll(ColorTemplate.VORDIPLOM_COLORS.toList())
            addAll(ColorTemplate.JOYFUL_COLORS.toList())
            addAll(ColorTemplate.COLORFUL_COLORS.toList())
            addAll(ColorTemplate.LIBERTY_COLORS.toList())
            addAll(ColorTemplate.PASTEL_COLORS.toList())
            add(ColorTemplate.getHoloBlue())
        }
    }
}