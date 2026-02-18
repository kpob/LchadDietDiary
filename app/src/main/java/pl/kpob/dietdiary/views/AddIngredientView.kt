package pl.kpob.dietdiary.views

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import com.santalu.respinner.ReSpinner
import com.wealthfront.magellan.BaseScreenView
import pl.kpob.dietdiary.find
import pl.kpob.dietdiary.db.IngredientCategory
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.screens.AddIngredientScreen

/**
 * Created by kpob on 22.10.2017.
 */
class AddIngredientView(ctx: Context): BaseScreenView<AddIngredientScreen>(ctx), ToolbarManager {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val name by lazy { find<EditText>(R.id.name) }
    private val kcal by lazy { find<EditText>(R.id.kcal) }
    private val lct by lazy { find<EditText>(R.id.lct) }
    private val mct by lazy { find<EditText>(R.id.mct) }
    private val carbohydrates by lazy { find<EditText>(R.id.carbohydrates) }
    private val salt by lazy { find<EditText>(R.id.salt) }
    private val roughage by lazy { find<EditText>(R.id.roughage) }
    private val protein by lazy { find<EditText>(R.id.protein) }
    private val saveBtn by lazy { find<View>(R.id.save_btn) }
    private val categorySpinner by lazy { find<ReSpinner>(R.id.category) }


    init {
        View.inflate(ctx, R.layout.screen_add_ingredient, this)

        categorySpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, IngredientCategory.stringValues()).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        saveBtn.setOnClickListener {
            screen.onSaveClick(
                    name.stringValue(), kcal.floatValue(), lct.floatValue(), mct.floatValue(),
                    carbohydrates.floatValue(), protein.floatValue(), roughage.floatValue(),
                    salt.floatValue(), category.ingredientType()
            )
        }
    }

    fun preFill(ingredient: FbIngredient) {
        name.setText(ingredient.name)
        kcal.setText(ingredient.calories.toString())
        lct.setText(ingredient.lct.toString())
        mct.setText(ingredient.mtc.toString())
        carbohydrates.setText(ingredient.carbohydrates.toString())
        salt.setText(ingredient.salt.toString())
        roughage.setText(ingredient.roughage.toString())
        protein.setText(ingredient.protein.toString())
        val category = IngredientCategory.stringValues().indexOfFirst { it == IngredientCategory.fromInt(ingredient.category).label  }
        categorySpinner.setSelection(category)
    }


    private fun EditText.floatValue() = try { text.toString().toFloat() } catch (e: Exception) { .0f }
    private fun EditText.stringValue() = text.toString()
    private fun Spinner.ingredientType() = IngredientCategory.fromString(categorySpinner.selectedItem as String)


}