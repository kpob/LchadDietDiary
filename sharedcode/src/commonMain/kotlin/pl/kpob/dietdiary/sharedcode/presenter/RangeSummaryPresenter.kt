package pl.kpob.dietdiary.sharedcode.presenter

import com.soywiz.klock.DateTime
import com.soywiz.klock.days
import com.soywiz.klock.milliseconds
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.utils.Day
import pl.kpob.dietdiary.sharedcode.utils.MealsInRangeProvider
import pl.kpob.dietdiary.sharedcode.view.RangeSummaryView

class RangeSummaryPresenter(
        private val firstDay: Day,
        private val lastDay: Day,
        private val mealDetailsRepository: Repository<MealDTO, MealDetails>) {

    private var view: RangeSummaryView? = null

    private val dataProvider: MealsInRangeProvider by lazy {
        val start = DateTime(firstDay.year, firstDay.month, firstDay.day).unixMillisLong
        val end = DateTime(lastDay.year, lastDay.month, lastDay.day)
                .plus(1.days)
                .minus(1.milliseconds)
                .unixMillisLong

        MealsInRangeProvider(start, end, mealDetailsRepository)
    }

    private val formattedDate: String get() = "Od ${firstDay.asString()} do ${lastDay.asString()}"


    fun onShow(view: RangeSummaryView) {
        this.view = view
        view.viewTitle = formattedDate

        val data = dataProvider.ingredients
        val calories = dataProvider.data
                .groupBy { it.dayOfYear }
                .map { it.value }
                .map { it.map { it.caloriesTotal }.sum() }
        val m = Metrics(calories)
        view.setupView(data, m)
    }

    private fun Day.asString() = let { "${it.day.asString}.${it.month.asString}.${it.year}"}

    private val Int.asString get() = if(this < 10) "0$this" else "$this"
}