package pl.kpob.dietdiary.views

import android.app.Activity
import android.content.Context
import android.support.v7.widget.Toolbar
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.santalu.respinner.ReSpinner
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.*
import org.jetbrains.anko.internals.AnkoInternals
import org.jetbrains.anko.sdk25.listeners.onClick
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.MealIngredient
import pl.kpob.dietdiary.screens.AddMealScreen

/**
 * Created by kpob on 20.10.2017.
 */
class AddMealView(ctx: Context) : BaseScreenView<AddMealScreen>(ctx), AnkoLogger, ToolbarManager {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val nextBtn by lazy { find<View>(R.id.add_next_btn) }
    private val container by lazy { find<ViewGroup>(R.id.container) }
    private val timeContainer by lazy { find<View>(R.id.time_container) }
    private val timeView by lazy { find<TextView>(R.id.time) }

    var time: String
        get() = AnkoInternals.noGetter()
        set(value) {
            timeView.text = value
        }

    init {
        View.inflate(ctx, R.layout.screen_add_meal, this)

        nextBtn.onClick {
            addRow()
        }

        initMenu(R.menu.add_meal) {
            if (it == R.id.action_done) {
                val data = obtainData()
                hideKeyboard()
                screen.onAddClick(data)
            }
        }

        timeContainer.onClick {
            screen.onTimeEditClick()
        }
    }

    fun addInitialRow() {
        addRow()
    }

    private fun addRow() {
        View.inflate(context, R.layout.item_next_ingredient, container)
        setupRow()
    }

    private fun setupRow() = container
            .lastChild<ViewGroup>()
            .forEachChild { v ->
                when (v) {
                    is AutoCompleteTextView -> {
//                        it.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, screen.possibleIngredients).apply {
//                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        }
                        v.setOnItemClickListener { parent, view, position, id ->
                            attempt {
                                v.post {
                                    (v.parent as ViewGroup).firstChild { it !is AutoCompleteTextView && it is EditText }.requestFocus()
                                }

                                (parent?.adapter as IngredientAdapter).selected = position

                            }
                        }
                        v.setAdapter(IngredientAdapter(context, screen.possibleIngredients))
                        v.threshold = 1
                        v.requestFocus()
                    }
                    is ImageView -> {
                        v.setOnClickListener { container.removeView(it.parent as View) }
                    }
                    is EditText -> {

                    }
                }
            }

    private fun obtainData(): List<Pair<Ingredient, Float>> =
            container.mapTypedChild<ViewGroup, Pair<Ingredient, Float>> {
                rowToData(it)
            }

    private fun rowToData(v: ViewGroup): Pair<Ingredient, Float> {
        var i: Ingredient? = null
        var w = .0f
        v.forEachChild {
            when (it) {
                is AutoCompleteTextView -> i = (it.adapter as IngredientAdapter).selectedItem
                is EditText -> w = try {
                    it.text.toString().toFloat()
                } catch (e: Exception) {
                    .0f
                }
            }
        }
        return i!! to w
    }

    fun setExistingData(ingredients: List<MealIngredient>, data: List<Ingredient>) {
        (0 until ingredients.size).forEach { addRow() }

        container.forEachTypedIndexedChild<ViewGroup> { idx, vg ->

            vg.forEachChild {
                when (it) {
                    is AutoCompleteTextView -> it.setSelection(data.indexOfFirst { it.id == ingredients[idx].id })
                    is EditText -> it.setText(ingredients[idx].weight.toString())
                }
            }
        }
    }

    private fun hideKeyboard() {
        val view: View = (context as Activity).currentFocus ?: return
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    class IngredientAdapter(ctx: Context, val data: List<Ingredient>) : ArrayAdapter<String>(ctx, R.layout.ingredient_drop_down), AnkoLogger {

        var selected: Int = 0

        val selectedItem: Ingredient get() = data[selected]

        private var tmpItems: List<Ingredient> = arrayListOf<Ingredient>().apply { addAll(data) }
        private var suggestions: MutableList<Ingredient> = mutableListOf()


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
                viewFromResource(position, convertView, parent, R.layout.ingredient_drop_down)

        private fun viewFromResource(position: Int, convertView: View?, parent: ViewGroup?, res: Int): View {
            val item = getItem(position)
            return (convertView ?: context.layoutInflater.inflate(res, parent, false)).apply {
                (this as TextView).text = item
            }
        }

        override fun getItem(position: Int): String =
                try { suggestions[position].name } catch (e: Exception) { "" }

        override fun getFilter(): Filter = nameFilter

        private val nameFilter = object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults =
                    if (constraint != null) {
                        info { "constraint $constraint" }
                        suggestions.clear()
                        val filtered = tmpItems.filter {
                            it.name.toLowerCase().contains(constraint.toString().toLowerCase())
                        }
                        suggestions.addAll(filtered)

                        info { suggestions }
                        FilterResults().apply {
                            values = suggestions
                            count = suggestions.size
                        }
                    } else {
                        FilterResults()
                    }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val data = results?.values as List<Ingredient>? ?: return
                if (data.isNotEmpty()) {
                    val names = data.map { it.name }
                    info { names }
                    clear()
                    addAll(names)
                    notifyDataSetChanged()
                }
            }
        }
    }
}