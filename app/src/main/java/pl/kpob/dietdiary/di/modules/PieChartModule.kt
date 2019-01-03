package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.model.MealDetails
import pl.kpob.dietdiary.sharedcode.presenter.ChartPresenter
import pl.kpob.dietdiary.sharedcode.repository.Repository
import javax.inject.Named

@Module
class PieChartModule(private val ids: List<String>) {

    @Provides
    fun providePresenter(@Named(DataModule.NAME_DEFAULT_MEAL_DETAILS_REPO) repo: Repository<MealDTO, MealDetails>): ChartPresenter {
        return ChartPresenter(ids, repo)
    }
}