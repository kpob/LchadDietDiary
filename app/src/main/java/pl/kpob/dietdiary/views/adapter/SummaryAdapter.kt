package pl.kpob.dietdiary.views.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.find
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.sharedcode.model.MealIngredient

class SummaryAdapter(
        private val ctx: Context,
        private val ingredients: List<MealIngredient>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rootView = LayoutInflater.from(ctx).inflate(R.layout.item_ingredient_2, parent, false);
        return object : RecyclerView.ViewHolder(rootView) {}
    }

    override fun getItemCount(): Int = ingredients.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView?.let {
            it.find<TextView>(R.id.name).text = ingredients[position].name
            it.find<TextView>(R.id.weight).text = String.format("%.1f g", ingredients[position].weight)
        }
    }
}