package pl.kpob.dietdiary.views

import android.app.Activity
import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import com.santalu.respinner.ReSpinner
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.info
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
    private val addMealBtn by lazy { find<View>(R.id.add_meal_btn) }
    private val container by lazy { find<ViewGroup>(R.id.container) }


    init {
        View.inflate(ctx, R.layout.screen_add_meal, this)

        addMealBtn.setOnClickListener {
            val data = obtainData()
            hideKeyboard()
            screen.onAddClick(data)
            info { "data $data" }
        }

        nextBtn.setOnClickListener {
            addRow()
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
            .forEachChild {
                when(it) {
                    is ReSpinner -> {
                        it.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, screen.possibleIngredients).apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                    }
                    is ImageView -> {
                        it.setOnClickListener { container.removeView(it.parent as View) }
                    }
                    is EditText -> {
                        it.requestFocus()
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
            when(it) {
                is ReSpinner -> i = it.selectedItem as Ingredient
                is EditText -> w = try { it.text.toString().toFloat() } catch (e: Exception) { .0f }
            }
        }
        return i!! to w
    }

    fun setExistingData(ingredients: List<MealIngredient>, data: List<Ingredient>) {
        (0 until ingredients.size).forEach { addRow() }

        info { "cc ${container.childCount}" }
        container.forEachTypedIndexedChild<ViewGroup> { idx, vg ->

            vg.forEachChild {
                when(it) {
                    is ReSpinner -> it.setSelection(data.indexOfFirst { it.id == ingredients[idx].id })
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
}