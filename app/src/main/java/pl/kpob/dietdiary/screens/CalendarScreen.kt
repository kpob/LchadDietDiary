package pl.kpob.dietdiary.screens

import android.content.Context
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.di.components.DaggerCalendarComponent
import pl.kpob.dietdiary.di.modules.CalendarModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.sharedcode.presenter.CalendarPresenter
import pl.kpob.dietdiary.sharedcode.utils.Day
import pl.kpob.dietdiary.views.MyCalendarView
import javax.inject.Inject

class CalendarScreen: ScopedScreen<MyCalendarView>(), AnkoLogger {

    @Inject lateinit var presenter: CalendarPresenter

    override fun createView(context: Context?): MyCalendarView {
        return MyCalendarView(context!!).apply {
            enableHomeAsUp { navigator.goBack() }
        }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)

        DaggerCalendarComponent.builder()
                .appComponent(appComponent)
                .navigatorModule(NavigatorModule(navigator))
                .calendarModule(CalendarModule())
                .build().inject(this)
        presenter.onShow(view)
    }

    fun onMonthSelected(date: CalendarDay) = presenter.onMonthSelected(date.asDay())

    fun onRangeSelected(first: CalendarDay?, last: CalendarDay?) =
            presenter.onRangeSelected(first?.asDay(), last?.asDay())

    fun onThisWeekClick() = presenter.onThisWeekClick()

    fun onThisMothClick() = presenter.onThisMothClick()

    fun onLastMothClick() = presenter.onLastMothClick()


    private fun CalendarDay.asDay(): Day {
        return Day.from(year, month, day)
    }
}