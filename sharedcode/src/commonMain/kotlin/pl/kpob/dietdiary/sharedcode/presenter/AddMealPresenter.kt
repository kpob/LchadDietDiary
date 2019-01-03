package pl.kpob.dietdiary.sharedcode.presenter

import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.IngredientsByMealTypeSpecification
import pl.kpob.dietdiary.sharedcode.repository.MealByIdSpecification
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.utils.*
import pl.kpob.dietdiary.sharedcode.utils.templates.TemplateManager
import pl.kpob.dietdiary.sharedcode.view.AddMealView
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import pl.kpob.dietdiary.sharedcode.view.PopupData
import pl.kpob.dietdiary.sharedcode.view.popup.*

class AddMealPresenter(
        private val mealType: MealType,
        private val meal: Meal?,
        private val remoteDb: RemoteDatabase,
        private val mealDetailsRepository: Repository<MealDTO, MealDetails>,
        private val mealsRepository: Repository<MealDTO, Meal>,
        private val ingredientRepository: Repository<IngredientDTO, Ingredient>,
        private val templateManager: TemplateManager,
        private val appNavigator: AppNavigator,
        private val mealSaver: MealSaver,
        private val popupDisplayer: PopupDisplayer,
        private val tokenProvider: UserTokenProvider
) {

    private var view: AddMealView? = null

    private val possibleIngredients: List<Ingredient> by lazy {
        ingredientRepository.query(IngredientsByMealTypeSpecification(mealType))
    }

    private var mealTime: Long = meal?.timestamp ?: currentTime()

    private val ingredients: List<MealIngredient> by lazy {
        val spec = MealByIdSpecification(meal!!.id)
        mealDetailsRepository.querySingle(spec)?.ingredients ?: listOf()
    }


    fun onShow(view: AddMealView) {
        view.time = mealTime.asReadableString

        if(meal != null) {
            view.setExistingData(ingredients, possibleIngredients)
            view.viewTitle = "Edytuj posiłek - ${mealType.string}"
        } else {
            view.addInitialRow(possibleIngredients)
            view.viewTitle = "Nowy posiłek - ${mealType.string}"
        }
    }

    fun onAddClick(data: List<Pair<Ingredient, Float>>, left: Float) {
        if (data.all { it.second == .0f }) {
            view?.displayError("Nie można zapisać pustego posiłku")
            return
        }

        val processedData = MealProcessor.process(data, left)
        val mealIngredients = processedData.map { FbMealIngredient(it.ingredient.id, it.weight) }
        val kcal = processedData.map { it.kcal }.sum()

        val meal = when {
            this.meal != null -> FbMeal(meal.id, mealTime, mealType.toString(), mealIngredients, kcal, tokenProvider.token)
            else -> FbMeal(nextId(), mealTime, mealType.toString(), mealIngredients, kcal, tokenProvider.token)
        }

        remoteDb.saveMeal(meal, this.meal != null)
        MealProcessor.calculateUsage(data).forEach {
            remoteDb.updateUsageCounter(it.itemId, it.counter)
        }

        mealSaver.save(mealsRepository, meal)
        appNavigator.goBack()
    }

    fun onAddRowClick() {
        view?.addRow(possibleIngredients)
    }

    fun onTimeEditClick() {
        val vm = EditTimePopup(
            data = PopupData(),
            callbacks = PopupCallbacks.init {
                ok("Zmień") { payload ->
                    if (payload is EditMealTimePayload) {
                        updateMealTimestamp(payload)
                    }
                }

                cancel("Anuluj") {}
        })
        popupDisplayer.display(vm)
    }

    private fun updateMealTimestamp(payload: EditMealTimePayload) {
        val newTimestamp = MyDateTime(currentTime())
                        .withTime(payload.hour, payload.minute)
                        .timestamp
        mealTime = newTimestamp
        view?.time = mealTime.asReadableString
    }

    fun addTemplate(ingredients: List<Ingredient>) {
        val vm = AddTemplatePopup(
            data = PopupData("Dodaj szablon posiłku"),
            callbacks = PopupCallbacks.init {
                ok("Zapisz") { payload ->
                    if (payload is AddTemplatePayload) {
                        saveTemplate(payload.name, ingredients)
                    }
                }

                cancel("Anuluj")
        })
        popupDisplayer.display(vm)
    }

    fun loadTemplate() {
        val templates = templateManager.loadTemplates()

        val vm = LoadTemplatePopup(
            data = PopupData("Wczytaj szablon posiłku"),
            callbacks = PopupCallbacks.init {
                ok("") { payload ->
                    if (payload is LoadTemplatePayload) {
                        val template = templateManager.loadTemplates()[payload.which]
                        view?.addRows(template.ingredients, possibleIngredients)
                    }
                }

                cancel("Anuluj")
            },
            names = templates.map { it.name }.toTypedArray()
        )
        popupDisplayer.display(vm)
    }

    private fun saveTemplate(name: String, ingredients: List<Ingredient>) {
        if(templateManager.exists(name)) {
            view?.displayError("Szablon o nazwie $name już istnieje")
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