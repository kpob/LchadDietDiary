package pl.kpob.dietdiary.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.dekoservidoni.omfm.OneMoreFabMenu
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.sdk25.listeners.onClick
import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.domain.MealsViewModel
import pl.kpob.dietdiary.hide
import pl.kpob.dietdiary.screens.MainScreen
import pl.kpob.dietdiary.show

/**
 * Created by kpob on 20.10.2017.
 */
class MainView(ctx: Context) : BaseScreenView<MainScreen>(ctx), ToolbarManager, AnkoLogger {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    val meals by lazy { find<RecyclerView>(R.id.meals) }
    val fab by lazy { find<OneMoreFabMenu>(R.id.fab) }
    private val syncBar by lazy { find<View>(R.id.sync_bar) }
    private val loader by lazy { find<View>(R.id.loader) }

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

        if(App.isSyncing) {
            syncBar.show()
        }
    }

    fun showMeals(viewModel: MealsViewModel) {
        if (meals.adapter != null && meals.adapter.itemCount == viewModel.size) {
            (meals.adapter as Adapter).let {
                it.data = viewModel
                it.notifyDataSetChanged()
            }
            return
        }

        meals.layoutManager = LinearLayoutManager(context)
        meals.adapter = Adapter(viewModel)
        loader.hide()
    }

    fun hideSyncBar() {
        syncBar.hide()
    }

    fun showSyncBar() {
        syncBar.show()
    }

    inner class Adapter(var data: MealsViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewBinderHelper = ViewBinderHelper()

        init {
            viewBinderHelper.setOpenOnlyOne(true)
        }

        private val ranges = data.ranges

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val range = ranges.firstOrNull { it.contains(position) }
            if (range != null) {
                val rangeIdx = ranges.indexOfFirst { it.contains(position) }

                holder.itemView?.let {
                    val item = data.mealsData.map { it.meals }.flatten()[position - rangeIdx - 1]
                    it.find<ImageView>(R.id.meal_type).setImageResource(item.type.icon)
                    it.find<TextView>(R.id.meal_time).text = item.time
                    it.findOptional<TextView>(R.id.meal_calories)?.text = String.format("%.2f kcal", item.calories)
                    it.find<TextView>(R.id.meal_lct).text = String.format("%.2f g", item.lct)
                    it.find<View>(R.id.delete).onClick { screen.onDeleteClick(item) }
                    it.find<View>(R.id.edit).onClick { screen.onEditClick(item) }
                    it.find<View>(R.id.meal_row).onClick { screen.onItemClick(item) }
                    it.find<View>(R.id.meal_time).onClick { screen.onTimeClick(item) }
                    viewBinderHelper.bind(it.find(R.id.swipe_layout), item.id)
                }
            } else {
                holder.itemView?.let {
                    val meals = data[ranges.indexOfFirst { it.first > position }].meals
                    it.find<TextView>(R.id.time).text = if(meals[0].isToday) "Dzisiaj" else meals[0].date
                    val totalLtc = meals.map { it.lct }.sum()
                    it.find<TextView>(R.id.meal_lct).text =  String.format("%.2f g", totalLtc)
                    val totalCalories = meals.map { it.calories }.sum()
                    it.find<TextView>(R.id.meal_calories).text =  String.format("%.2f kcal", totalCalories)
                    it.onClick { screen.onLabelClick(meals) }
                }
            }
        }

        override fun getItemCount(): Int = data.viewsCount

        override fun getItemViewType(position: Int): Int = if (ranges.any { it.contains(position) }) 1 else 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(View.inflate(context, if (viewType == 1) R.layout.item_meal else R.layout.item_meal_header, null)) {}
        }

    }
}