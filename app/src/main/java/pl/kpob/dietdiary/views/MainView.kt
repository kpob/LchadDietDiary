package pl.kpob.dietdiary.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.dekoservidoni.omfm.OneMoreFabMenu
import com.wealthfront.magellan.BaseScreenView
import pl.kpob.dietdiary.AnkoLogger
import pl.kpob.dietdiary.find
import pl.kpob.dietdiary.findOptional
import pl.kpob.dietdiary.onClick
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.screens.MainScreen


/**
 * Created by kpob on 20.10.2017.
 */
class MainView(ctx: Context) : BaseScreenView<MainScreen>(ctx), ToolbarManager, AnkoLogger {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val meals by lazy { find<RecyclerView>(R.id.meals) }
    private val fab by lazy { find<OneMoreFabMenu>(R.id.fab) }

    init {
        inflate(ctx, R.layout.screen_home, this)

        fab.setOptionsClick(object : OneMoreFabMenu.OptionsClick {
            override fun onOptionClick(optionId: Int?) {
                when (optionId) {
                    R.id.option1 -> screen.onDessertClick()
                    R.id.option2 -> screen.onDinnerClick()
                    R.id.option3 -> screen.onMilkClick()
                    R.id.add_ingredient -> screen.onAddIngredientClick()
                }
            }
        })
    }

    fun showMeals(data: List<Meal>) {
        if (meals.adapter != null && meals.adapter.itemCount == data.size) return

        meals.layoutManager = LinearLayoutManager(context)
        meals.adapter = Adapter(data.groupBy { it.dayOfYear }.toList())
    }


    inner class Adapter(val data: List<Pair<Int, List<Meal>>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewBinderHelper = ViewBinderHelper()

        init {
            viewBinderHelper.setOpenOnlyOne(true)
        }

        private val groups = data.size
        private val ranges = (0 until groups).map {
            (1 + it + data.take(it).map { it.second }.flatten().count())..(data.take(it + 1).map { it.second }.flatten().count() + it)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val range = ranges.firstOrNull { it.contains(position) }
            if (range != null) {
                val rangeIdx = ranges.indexOfFirst { it.contains(position) }

                holder?.itemView?.let {
                    val item = data.map { it.second }.flatten()[position - rangeIdx - 1]
                    it.find<ImageView>(R.id.meal_type).setImageResource(item.type.icon)
                    it.find<TextView>(R.id.meal_time).text = item.time
                    it.findOptional<TextView>(R.id.meal_calories)?.text = String.format("%.2f kcal", item.calories)
                    it.find<TextView>(R.id.meal_lct).text = String.format("%.2f g", item.lct)
                    it.find<View>(R.id.delete).onClick { screen.onDeleteClick(item) }
                    it.find<View>(R.id.edit).onClick { screen.onEditClick(item) }
                    it.find<View>(R.id.meal_row).onClick { screen.onItemClick(item) }
                    it.find<View>(R.id.meal_time).onClick { screen.onTimeClick(item) }
                    viewBinderHelper.bind(it.find<SwipeRevealLayout>(R.id.swipe_layout), item.id)
                }
            } else {
                holder?.itemView?.let {
                    val meals = data[ranges.indexOfFirst { it.first > position }].second
                    it.find<TextView>(R.id.time).text = if(meals[0].isToday) "Dzisiaj" else meals[0].date
                    val totalLtc = meals.map { it.lct }.sum()
                    it.find<TextView>(R.id.meal_lct).text =  String.format("%.2f g", totalLtc)
                    val totalCalories = meals.map { it.calories }.sum()
                    it.find<TextView>(R.id.meal_calories).text =  String.format("%.2f kcal", totalCalories)
                    it.onClick { screen.onLabelClick(meals) }
                }
            }
        }

        override fun getItemCount(): Int = data.size + data.map { it.second }.flatten().size

        override fun getItemViewType(position: Int): Int {
            return (if (ranges.any { it.contains(position) }) 1 else 2).apply {}
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(View.inflate(context, if (viewType == 1) R.layout.item_meal else R.layout.item_meal_header, null)) {}
        }

    }
}