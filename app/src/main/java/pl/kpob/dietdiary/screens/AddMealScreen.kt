package pl.kpob.dietdiary.screens

import android.content.Context
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.wealthfront.magellan.rx.RxScreen
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.firebase.FbMealIngredient
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.repo.*
import pl.kpob.dietdiary.template.DefaultTemplateManager
import pl.kpob.dietdiary.template.TemplateManager
import pl.kpob.dietdiary.utils.MealProcessor
import pl.kpob.dietdiary.views.AddMealView
import pl.kpob.dietdiary.views.utils.TimePicker
import pl.kpob.dietdiary.worker.RefreshIngredientsDataService


/**
 * Created by kpob on 20.10.2017.
 */
class AddMealScreen(private val type: MealType, private val meal: Meal? = null) : RxScreen<AddMealView>(), AnkoLogger {

    private val fbSaver by lazy { FirebaseSaver() }

    private val ingredientRepo by lazy { IngredientRepository() }
    private val mealRepo by lazy { MealDetailsRepository() }

    private var mealTime: Long = meal?.timestamp ?: currentTime()
    private var totalWeight: Float = .0f

    val possibleIngredients: List<Ingredient> by lazy {
        ingredientRepo.withRealmQuery { IngredientsByMealTypeSpecification(it, type) }
    }

    private val ingredients by lazy {
        mealRepo.withRealmSingleQuery { MealByIdSpecification(it, meal!!.id) }?.ingredients ?: listOf()
    }

    private val templateManager: TemplateManager by lazy {
        DefaultTemplateManager(MealTemplateRepository(possibleIngredients), type)
    }

    override fun createView(context: Context?) = AddMealView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.enableHomeAsUp { navigator.handleBack() }
            it.time = mealTime.asReadableString

            if(meal != null) {
                it.setExistingData(ingredients, possibleIngredients)
                it.toolbarTitle = "Edytuj posiłek - ${type.string}"
            } else {
                it.addInitialRow()
                it.toolbarTitle = "Nowy posiłek - ${type.string}"
            }
        }
    }

    fun onAddClick(data: List<Pair<Ingredient, Float>>, left: Float) {
        if (data.all { it.second == .0f }) {
            view?.context?.toast("Nie można zapisać pustego posiłku")
            return
        }

        val processedData = MealProcessor.process(data, left)
        val mealIngredients = processedData.map { FbMealIngredient(it.ingredient.id, it.weight) }
        val kcal = processedData.map { it.kcal }.sum()

        val meal = when {
            this.meal != null -> FbMeal(meal.id, mealTime, type.toString(), mealIngredients, kcal = kcal)
            else -> FbMeal(nextId(), mealTime, type.toString(), mealIngredients, kcal = kcal)
        }

        fbSaver.saveMeal(meal, this.meal != null)
        MealProcessor.calculateUsage(data).forEach {
            fbSaver.updateUsageCounter(it.itemId, it.counter)
        }

        realmAsyncTransaction(
            transaction = { mealRepo.insert(meal.toRealm(), RealmAddTransaction(it)) },
            callback = {
                activity.startService<RefreshIngredientsDataService>()
                navigator.handleBack()
            }
        )
    }

    fun onTimeEditClick() = showDialog {
        TimePicker().dialog(activity) { m, h ->
            val newTimestamp = DateTime(currentTime())
                    .withMinuteOfHour(m)
                    .withHourOfDay(h)
                    .millis

            mealTime = newTimestamp
            view.time = mealTime.asReadableString
        }

    }

    fun addTemplate(ingredients: List<Ingredient>) = showDialog {
        AlertDialog.Builder(it).apply {
            setTitle("Dodaj szablon posiłku")

            var nameField: EditText? = null
            setView(it.layoutInflater.inflate(R.layout.view_save_template, null).apply {
                nameField = find(R.id.template_name)
            })

            setNegativeButton("Anuluj") { _, _ ->  }
            setPositiveButton("Zapisz") { _, _ ->
                val name = nameField?.text?.toString() ?: ""
                saveTemplate(name, ingredients)
            }
        }.create()
    }

    fun loadTemplate() = showDialog {
        AlertDialog.Builder(it).apply {
            setTitle("Wczytaj szablon posiłku")
            val templates = templateManager.loadTemplates()
            val items = templates.map { it.name }.toTypedArray()
            setSingleChoiceItems(items, 0) { dialog, which ->
                view?.addRows(templates[which].ingredients, possibleIngredients)
                dialog.dismiss()
            }
        }.create()
    }

    private fun saveTemplate(name: String, ingredients: List<Ingredient>) {
        if(templateManager.exists(name)) {
            view?.context?.toast("Szablon o nazwie $name już istnieje")
            return
        }
        templateManager.addTemplate(name, ingredients)
    }

    fun updateTotalWeight(value: Float) {
        view?.totalWeight = value
    }

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    companion object {
        fun Ingredient.toString(): String = name
    }

}