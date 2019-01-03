package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.IngredientDTO
import pl.kpob.dietdiary.sharedcode.presenter.AddIngredientPresenter
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import pl.kpob.dietdiary.utils.data.AndroidIngredientHandler

@Module
class AddIngredientModule(private val ingredient: FbIngredient? = null) {


    @Provides
    fun providePresenter(remoteDatabase: RemoteDatabase, appNavigator: AppNavigator,
                         repo: Repository<IngredientDTO, Ingredient>): AddIngredientPresenter {
        return AddIngredientPresenter(ingredient, remoteDatabase, repo, appNavigator, AndroidIngredientHandler)
    }
}