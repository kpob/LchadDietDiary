package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.model.MealDetails
import pl.kpob.dietdiary.sharedcode.presenter.RangeSummaryPresenter
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.utils.Day
import javax.inject.Named

@Module
class RangeSummaryModule(private val start: Day, private val end: Day) {


    @Provides
    fun providePresenter(@Named(DataModule.NAME_DEFAULT_MEAL_DETAILS_REPO) repo: Repository<MealDTO, MealDetails>): RangeSummaryPresenter {
        return RangeSummaryPresenter(start, end, repo)
    }
}