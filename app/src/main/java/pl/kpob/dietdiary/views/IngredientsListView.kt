package pl.kpob.dietdiary.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.screens.IngredientsListScreen
import pl.kpob.dietdiary.sharedcode.view.IngredientListView
import pl.kpob.dietdiary.sharedcode.viewmodel.IngredientsViewModel

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientsListView(ctx: Context): BaseScreenView<IngredientsListScreen>(ctx), ToolbarManager, IngredientListView {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val list by lazy { find<RecyclerView>(R.id.ingredients) }

    init {
        View.inflate(ctx, R.layout.screen_ingredients_list, this)
    }

    override var viewTitle: String
        get() = toolbarTitle
        set(value) { toolbarTitle = value }

    override fun initList(data: IngredientsViewModel) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = Adapter(data)
    }

    override fun updateList(data: IngredientsViewModel) {
        (list.adapter as Adapter).let {
            it.viewModel = data
            it.notifyDataSetChanged()
        }
    }

    override fun displayMessage(message: String) {
        context.toast(message)
    }

    inner class Adapter(var viewModel: IngredientsViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var groups = viewModel.ingredients
        private val ranges = viewModel.ranges

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val range = ranges.firstOrNull { it.contains(position) }
            if (range != null) {
                val rangeIdx = ranges.indexOfFirst { it.contains(position) }

                holder.itemView.let {
                    val item = groups.map { it.ingredients }.flatten()[position - rangeIdx - 1]
                    it.find<TextView>(R.id.name).text = item.name
                    it.find<View>(R.id.edit).onClick { screen.onEditClick(item) }
                    it.find<View>(R.id.delete).onClick { screen.onDeleteClick(item) }
                }
            } else {
                holder.itemView?.let {
                    val category = groups[ranges.indexOfFirst { it.first > position }].category
                    it.find<TextView>(R.id.category).text = category.label
                }
            }
        }

        override fun getItemCount(): Int = viewModel.viewsCount

        override fun getItemViewType(position: Int): Int = if (ranges.any { it.contains(position) }) 1 else 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(if (viewType == 1) R.layout.item_ingredient else R.layout.item_ingredient_category, parent, false)) {}
        }
    }
}