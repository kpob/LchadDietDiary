package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import io.realm.Realm
import pl.kpob.dietdiary.utils.data.AndroidMealSaver
import pl.kpob.dietdiary.db.RealmDatabase
import pl.kpob.dietdiary.db.RealmMealTemplate
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.presenter.AddMealPresenter
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.repository.mapper.MealTemplateMapper
import pl.kpob.dietdiary.sharedcode.utils.UserTokenProvider
import pl.kpob.dietdiary.sharedcode.utils.templates.DefaultTemplateManager
import pl.kpob.dietdiary.sharedcode.utils.templates.TemplateManager
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import pl.kpob.dietdiary.sharedcode.view.popup.PopupDisplayer
import pl.kpob.dietdiary.template.AndroidMealTemplateCreator
import pl.kpob.dietdiary.template.AndroidMealTemplateSaver
import javax.inject.Named

@Module
class AddMealModule(
        private val type: MealType,
        private val meal: Meal?
) {

    @Provides
    fun providePresenter(
            @Named(DataModule.NAME_TYPED_MEALS_REPO) mealsRepo: Repository<MealDTO, Meal>,
            @Named(DataModule.NAME_TYPED_MEAL_DETAILS_REPO) detailsRepo: Repository<MealDTO, MealDetails>,
            ingredientRepo: Repository<IngredientDTO, Ingredient>, remoteDatabase: RemoteDatabase,
            appNavigator: AppNavigator, templateManager: TemplateManager, tokenProvider: UserTokenProvider,
            popupDisplayer: PopupDisplayer): AddMealPresenter {
        return AddMealPresenter(
                mealType = type,
                meal = meal,
                remoteDb = remoteDatabase,
                appNavigator = appNavigator,
                mealSaver = AndroidMealSaver,
                templateManager = templateManager,
                mealsRepository = mealsRepo,
                ingredientRepository = ingredientRepo,
                mealDetailsRepository = detailsRepo,
                popupDisplayer = popupDisplayer,
                tokenProvider = tokenProvider
        )
    }

    @Provides
    fun provideMealType(): MealType = type

    @Provides
    fun provideTemplateManager(repo: Repository<MealTemplateDTO, MealTemplate>): TemplateManager {
        return DefaultTemplateManager(
                creator = AndroidMealTemplateCreator,
                saver = AndroidMealTemplateSaver,
                type = type,
                repo = repo
        )
    }

    @Provides
    fun provideMealTemplateRepository(realm: Realm, @Named("typedIngredients") ingredients: List<Ingredient>): Repository<MealTemplateDTO, MealTemplate> {
        return Repository(MealTemplateMapper(ingredients), RealmDatabase(realm, RealmMealTemplate::class.java)) as Repository<MealTemplateDTO, MealTemplate>
    }
}