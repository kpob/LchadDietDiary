package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.utils.data.AndroidIngredientHandler
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.IngredientDTO
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.presenter.IngredientListPresenter
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import javax.inject.Named

@Module
class IngredientsModule {

    @Provides
    fun providePresenter(@Named(DataModule.NAME_DEFAULT_MEALS_REPO) mealsRepo: Repository<MealDTO, Meal>, ingredientsRepo: Repository<IngredientDTO, Ingredient>, navigator: AppNavigator, remoteDatabase: RemoteDatabase): IngredientListPresenter {
        return IngredientListPresenter(navigator, ingredientsRepo, mealsRepo, remoteDatabase, AndroidIngredientHandler)
    }
}