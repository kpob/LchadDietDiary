package pl.kpob.dietdiary.views

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.screens.RangeSummaryScreen
import pl.kpob.dietdiary.sharedcode.model.MealIngredient
import pl.kpob.dietdiary.sharedcode.model.Metrics
import pl.kpob.dietdiary.sharedcode.view.RangeSummaryView
import pl.kpob.dietdiary.views.adapter.SummaryAdapter

class RangeSummaryView(ctx: Context) : BaseScreenView<RangeSummaryScreen>(ctx), RangeSummaryView, ToolbarManager, AnkoLogger {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val pager by lazy { find<ViewPager>(R.id.pager) }

    init {
        View.inflate(ctx, R.layout.screen_range_summary, this)
    }

    override var viewTitle: String
        get() = toolbarTitle
        set(value) { toolbarTitle = value }

    override fun setupView(ingredients: List<MealIngredient>, caloriesMetrics: Metrics) {
        pager.adapter = object: PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

            override fun getCount(): Int = LAYOUTS.size

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun getPageTitle(position: Int): CharSequence? = TITLES[position]

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view = View.inflate(context, LAYOUTS[position], null)

                when (position) {
                    0 -> setupGeneralPage(view as ViewGroup, caloriesMetrics)
                    1 -> setupList(view as RecyclerView, ingredients)
                    else -> throw RuntimeException()
                }

                container.addView(view)
                return view
            }

        }
    }

    private fun setupList(recyclerView: RecyclerView, ingredients: List<MealIngredient>) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = SummaryAdapter(context, ingredients)
    }

    private fun setupGeneralPage(vg: ViewGroup, caloriesMetrics: Metrics) {
        vg.find<TextView>(R.id.calories_total).text = String.format("%.2f kcal", caloriesMetrics.total)
        vg.find<TextView>(R.id.calories_avg).text = String.format("%.2f kcal", caloriesMetrics.avg)
        vg.find<TextView>(R.id.calories_min_max).text = String.format("(%.2f/%.2f) kcal", caloriesMetrics.max, caloriesMetrics.min)
    }

    companion object {
        val LAYOUTS = listOf(R.layout.page_summary_general, R.layout.page_summary_list)
        val TITLES = listOf("Ogólne", "Ilość")
    }
}