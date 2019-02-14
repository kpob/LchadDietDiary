package pl.kpob.dietdiary.views

import android.content.Context
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.DividerItemDecoration
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
import pl.kpob.dietdiary.hide
import pl.kpob.dietdiary.screens.MainScreen
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.view.MainView
import pl.kpob.dietdiary.sharedcode.viewmodel.DayLabelViewModel
import pl.kpob.dietdiary.sharedcode.viewmodel.MealItemViewModel
import pl.kpob.dietdiary.sharedcode.viewmodel.MealsViewModel
import pl.kpob.dietdiary.show

/**
 * Created by kpob on 20.10.2017.
 */
class MainView(ctx: Context) : BaseScreenView<MainScreen>(ctx), ToolbarManager, AnkoLogger, MainView {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val meals by lazy { find<RecyclerView>(R.id.meals) }
    private val fab by lazy { find<OneMoreFabMenu>(R.id.fab) }
    private val syncBar by lazy { find<View>(R.id.sync_bar) }
    private val loader by lazy { find<View>(R.id.loader) }

    private val drawerLayout: DrawerLayout by lazy { find<DrawerLayout>(R.id.drawer_layout)}
    val navigationView: NavigationView by lazy { find<NavigationView>(R.id.nav_view)}

    init {
        inflate(ctx, R.layout.screen_home, this)

        toolbar.setNavigationIcon(R.mipmap.ic_launcher)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START, true)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

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

    override fun showMeals(viewModel: MealsViewModel) {
        if (meals.adapter != null) {
            if (meals.adapter.itemCount == viewModel.mealsData.size) return
            (meals.adapter as Adapter).let {
                it.data = viewModel
                it.notifyDataSetChanged()
            }
            return
        }
        meals.layoutManager = LinearLayoutManager(context)
        meals.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        meals.adapter = Adapter(viewModel)
        loader.hide()
    }

    override fun hideSyncBar() = syncBar.hide()

    override fun showSyncBar() = syncBar.show()

    override fun closeDrawers() = drawerLayout.closeDrawers()

    override fun hideMeals() = meals.hide()

    inner class Adapter(var data: MealsViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewBinderHelper = ViewBinderHelper()

        init {
            viewBinderHelper.setOpenOnlyOne(true)
        }

        private val ranges = data.ranges

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val type = getItemViewType(position)
            if(type == MealsViewModel.MEAL_ITEM_VIEW_TYPE) {
                val viewModel = data.mealViewModelByPosition(position)
                bindMealItem(holder, viewModel)
            } else {
                val viewModel = data.labelViewModelByPosition(position)
                bindLabelItem(holder, position, viewModel)
            }
        }

        override fun getItemCount(): Int = data.viewsCount

        override fun getItemViewType(position: Int): Int = data.viewTypeByPosition(position)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val viewRes = if(viewType == MealsViewModel.LABEL_VIEW_TYPE) R.layout.item_meal_header else R.layout.item_meal
            return object : RecyclerView.ViewHolder(View.inflate(context, viewRes, null)) {}
        }

        private fun bindMealItem(holder: RecyclerView.ViewHolder, viewModel: MealItemViewModel) {
            holder.itemView?.let {
                it.find<ImageView>(R.id.meal_type).setImageResource(icon(viewModel.meal.type))
                it.find<TextView>(R.id.meal_time).text = viewModel.time
                it.findOptional<TextView>(R.id.meal_calories)?.text = viewModel.calories
                it.find<TextView>(R.id.meal_lct).text = viewModel.lct
                it.find<View>(R.id.delete).onClick { screen.onDeleteClick(viewModel.meal) }
                it.find<View>(R.id.edit).onClick { screen.onEditClick(viewModel.meal) }
                it.find<View>(R.id.meal_row).onClick { screen.onItemClick(viewModel.meal) }
                it.find<View>(R.id.meal_time).onClick { screen.onTimeClick(viewModel.meal) }
                viewBinderHelper.bind(it.find(R.id.swipe_layout), viewModel.meal.id)
            }
        }

        private fun bindLabelItem(holder: RecyclerView.ViewHolder, position: Int, viewModel: DayLabelViewModel) {
            val idx = ranges.indexOfFirst { it.first > position }
            if (idx == -1) return
            val meals = data.mealsData[idx].meals

            holder.itemView?.let {
                it.find<TextView>(R.id.time).text = viewModel.date
                it.find<TextView>(R.id.meal_lct).text = viewModel.lct
                it.find<TextView>(R.id.meal_calories).text = viewModel.calories
                it.onClick { screen.onLabelClick(meals) }
            }
        }

        private fun icon(mealType: MealType) = when(mealType) {
            MealType.DESSERT -> R.drawable.ic_porridge
            MealType.MILK -> R.drawable.ic_milk_bottle
            MealType.DINNER -> R.drawable.ic_dinner
            else -> R.drawable.ic_ingerdient
        }

    }
}