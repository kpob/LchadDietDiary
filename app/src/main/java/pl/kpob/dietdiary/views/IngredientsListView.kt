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
import pl.kpob.dietdiary.Ingredient
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.db.IngredientCategory
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.screens.IngredientsListScreen

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientsListView(ctx: Context): BaseScreenView<IngredientsListScreen>(ctx), ToolbarManager {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val list by lazy { find<RecyclerView>(R.id.ingredients) }

    init {
        View.inflate(ctx, R.layout.screen_ingredients_list, this)
    }

    fun initList(data: List<Ingredient>) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = Adapter(data)
    }

    fun updateList(data: List<Ingredient>) {
        (list.adapter as Adapter).let {
            it.data = data
            it.notifyDataSetChanged()
        }
    }

    inner class Adapter(var data: List<Ingredient>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        var groups = data.groupBy { it.category }.toList()

        private val groupsCount = groups.size
        private val ranges = (0 until groupsCount).map {
            (1 + it + groups.take(it).map { it.second }.flatten().count())..(groups.take(it + 1).map { it.second }.flatten().count() + it)
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val range = ranges.firstOrNull { it.contains(position) }
            if (range != null) {
                val rangeIdx = ranges.indexOfFirst { it.contains(position) }

                holder?.let {
                    val item = groups.map { it.second }.flatten()[position - rangeIdx - 1]
                    it.itemView.find<TextView>(R.id.name).text = item.name
                    it.itemView.find<View>(R.id.edit).setOnClickListener {
                        screen.onEditClick(item)
                    }
                    it.itemView.find<View>(R.id.delete).setOnClickListener {
                        screen.onDeleteClick(item)
                    }
                }
            } else {
                holder?.itemView?.let {
                    val category = IngredientCategory.fromInt(groups[ranges.indexOfFirst { it.first > position }].first)
                    it.find<TextView>(R.id.category).text = category.label
                }
            }
        }


        override fun getItemCount(): Int = groupsCount + data.size

        override fun getItemViewType(position: Int): Int {
            return (if (ranges.any { it.contains(position) }) 1 else 2).apply {}
        }


        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(if (viewType == 1) R.layout.item_ingredient else R.layout.item_ingredient_category, parent, false)) {}
        }
    }
}