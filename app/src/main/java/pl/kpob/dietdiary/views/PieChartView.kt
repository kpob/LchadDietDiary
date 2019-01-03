package pl.kpob.dietdiary.views

import android.content.Context
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.find
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.screens.PieChartScreen
import pl.kpob.dietdiary.sharedcode.model.MealIngredient
import pl.kpob.dietdiary.sharedcode.view.ChartView
import pl.kpob.dietdiary.sharedcode.viewmodel.SummaryTabsViewModel
import pl.kpob.dietdiary.views.adapter.SummaryAdapter


/**
 * Created by kpob on 21.10.2017.
 */
class PieChartView(ctx: Context) : BaseScreenView<PieChartScreen>(ctx), ToolbarManager, ChartView, OnChartValueSelectedListener {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private val pager by lazy { find<ViewPager>(R.id.pager) }

    init {
        View.inflate(ctx, R.layout.screen_pie_chart, this)
    }

    override var viewTitle: String
        get() = toolbarTitle
        set(value) { toolbarTitle = value }

    override fun setupView(viewModel: SummaryTabsViewModel) {
        pager.adapter = object: PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

            override fun getCount(): Int = viewModel.tabsCount

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun getPageTitle(position: Int): CharSequence? = viewModel.tabTitle(position)

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val viewResId = if(position == 0) R.layout.page_list else R.layout.page_chart
                val view = View.inflate(context, viewResId, null)
                if (view is PieChart) {
                    initChart(view, viewModel.nutrients)
                } else if (view is RecyclerView) {
                    initList(view, viewModel.ingredients)
                }

                container.addView(view)
                return view
            }

        }
    }

    private fun initChart(chart: PieChart, nutrients: Map<String, Float>) {
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        chart.dragDecelerationFrictionCoef = 0.95f
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        chart.holeRadius = 58f
        chart.transparentCircleRadius = 61f

        chart.setDrawCenterText(true)

        chart.rotationAngle = 0f
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        chart.setOnChartValueSelectedListener(this)

        setData(chart, nutrients)

        with(chart.legend) {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(false)
            xEntrySpace = 7f
            yEntrySpace = 0f
            yOffset = 0f
        }

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(12f)
    }

    private fun initList(list: RecyclerView, ingredients: List<MealIngredient>) {
        list.layoutManager = LinearLayoutManager(context)
        list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        list.adapter = SummaryAdapter(context, ingredients)
    }

    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {}

    private fun setData(chart: PieChart, nutrients: Map<String, Float>) {

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        val entries = nutrients.map {
            PieEntry(it.value, it.key)
        }

        val dataSet = PieDataSet(entries, "Składniki odżywcze").apply {
            setDrawIcons(false)
            sliceSpace = 3f
            iconsOffset = MPPointF(0f, 40f)
            selectionShift = 12f
        }

        val colors = ArrayList<Int>()

        for (c in ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS)
            colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS)
            colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS)
            colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS)
            colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setValueTextColor(Color.BLACK)
        }
        chart.data = data
        chart.highlightValues(null)
        chart.invalidate()
    }

}