package pl.kpob.dietdiary.views

import android.app.Activity
import android.content.Context
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.*
import org.jetbrains.anko.internals.AnkoInternals
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.sdk25.listeners.onSeekBarChangeListener
import org.jetbrains.anko.sdk25.listeners.textChangedListener
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.delegates.TextViewDelegate
import pl.kpob.dietdiary.delegates.TextViewFloatValueDelegate
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.MealIngredient
import pl.kpob.dietdiary.screens.AddMealScreen
import kotlin.properties.Delegates

/**
 * Created by kpob on 20.10.2017.
 */
class AddMealView(ctx: Context) : BaseScreenView<AddMealScreen>(ctx), AnkoLogger, ToolbarManager {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val nextBtn by lazy { find<View>(R.id.add_next_btn) }
    private val container by lazy { find<ViewGroup>(R.id.container) }
    private val timeContainer by lazy { find<View>(R.id.time_container) }
    private val leftView by lazy { find<EditText>(R.id.left) }

    var time: CharSequence by TextViewDelegate(R.id.time)
    var totalWeight: Float by TextViewFloatValueDelegate(R.id.total_weight, "Razem:")

    init {
        inflate(ctx, R.layout.screen_add_meal, this)

        nextBtn.onClick { addRow() }

        initMenu(R.menu.add_meal) {
            when (it) {
                R.id.action_done -> {
                    val data = obtainData()
                    hideKeyboard()
                    screen.onAddClick(data, leftView.floatValue)
                }
                R.id.action_add_template -> screen.addTemplate(obtainIngredients())
                R.id.action_load_template -> screen.loadTemplate()
            }
        }

        timeContainer.onClick {
            screen.onTimeEditClick()
        }
    }

    fun addInitialRow() = addRow()

    fun setExistingData(ingredients: List<MealIngredient>, data: List<Ingredient>) {
        (0 until ingredients.size).forEach { addRow() }

        container.forEachTypedIndexedChild<ViewGroup> { idx, vg ->
            setRowData(vg, data, ingredients, idx)
        }
    }

    private fun setRowData(vg: ViewGroup, data: List<Ingredient>, ingredients: List<MealIngredient>, idx: Int) {
        vg.forEachChild {
            when (it) {
                is AutoCompleteTextView -> {
                    val i = data.first { it.id == ingredients[idx].id }
                    val iidx = data.indexOfFirst { it.id == ingredients[idx].id }
                    it.setText(i.name)
                    (it.adapter as IngredientAdapter).selected = iidx
                }
                is EditText -> it.setText(ingredients[idx].weight.toString())
            }
        }
    }

    private fun addRow() {
        inflate(context, R.layout.item_next_ingredient, container)
        setupRow()
    }

    private fun setupRow() = container
            .lastChild<ViewGroup>()
            .forEachChild { v ->
                when (v) {
                    is AutoCompleteTextView -> {
                        setupAutocompleteView(v)
                    }
                    is ImageView -> {
                        v.setOnClickListener { container.removeView(it.parent as View) }
                    }
                    is EditText -> {
                        v.textChangedListener {
                            afterTextChanged { screen.updateTotalWeight(rowsAsWeight()) }
                        }
                    }
                }
            }

    private fun setupAutocompleteView(v: AutoCompleteTextView) = with(v) {
        setOnItemClickListener { parent, view, position, id ->
            (parent?.adapter as IngredientAdapter).selected = position

            attempt {
                v.post {
                    (v.parent as ViewGroup).firstChild { it !is AutoCompleteTextView && it is EditText }.requestFocus()
                }

            }
        }
        setAdapter(IngredientAdapter(context, screen.possibleIngredients))
        threshold = 1
        requestFocus()
    }

    private fun obtainData(): List<Pair<Ingredient, Float>> = container
            .mapTypedChild<ViewGroup, Pair<Ingredient?, Float>> { rowToData(it) }
            .filter { it.first != null }
            .map { it.first!! to it.second }


    private fun obtainIngredients(): List<Ingredient> = container
            .mapTypedChild<ViewGroup, Pair<Ingredient?, Float>> { rowToData(it) }
            .filter { it.first != null }
            .map { it.first!! }

    private fun rowToData(v: ViewGroup): Pair<Ingredient?, Float> {
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
        return i to w
    }

    private fun hideKeyboard() {
        val view: View = (context as Activity).currentFocus ?: return
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    class IngredientAdapter(ctx: Context, val data: List<Ingredient>) : ArrayAdapter<String>(ctx, R.layout.ingredient_drop_down), AnkoLogger {

        private var tmpItems: List<Ingredient> = arrayListOf<Ingredient>().apply { addAll(data) }
        private var suggestions: MutableList<Ingredient> = mutableListOf()
        private var _selectedItem: Ingredient? = null

        var selected: Int by Delegates.observable(0) { prop, old, new ->
            _selectedItem = if (suggestions.size > new) suggestions[new] else data[new]
        }

        val selectedItem: Ingredient? get() = _selectedItem

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
                viewFromResource(position, convertView, parent, R.layout.ingredient_drop_down)

        private fun viewFromResource(position: Int, convertView: View?, parent: ViewGroup?, res: Int): View {
            val item = getItem(position)
            return (convertView ?: context.layoutInflater.inflate(res, parent, false)).apply {
                (this as TextView).text = item
            }
        }

        override fun getItem(position: Int): String = try {
            suggestions[position].name
        } catch (e: Exception) {
            ""
        }

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

    fun addRows(ingredients: List<Ingredient>, possibleIngredients: List<Ingredient>) {
        ingredients.forEach {
            container
                    .lastChild<ViewGroup>()
                    .forEachChild { v ->
                        when (v) {
                            is AutoCompleteTextView -> {
                                setupAutocompleteView(v)
                                val iidx = possibleIngredients.indexOf(it)
                                v.setText(it.name)
                                (v.adapter as IngredientAdapter).selected = iidx
                            }
                            is ImageView -> {
                                v.setOnClickListener { container.removeView(it.parent as View) }
                            }
                        }
                    }
            addRow()
        }
    }

    fun rowsAsWeight(): Float =
        container.mapTypedChild<ViewGroup, Float> {
            it.childrenSequence()
                    .filter { it is EditText && it !is AutoCompleteTextView }
                    .map { it as EditText }
                    .map { it.floatValue }
                    .sum()
        }.sum()




}