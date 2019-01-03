package pl.kpob.dietdiary.sharedcode.presenter

import com.soywiz.klock.DateTime
import com.soywiz.klock.days
import com.soywiz.klock.months
import pl.kpob.dietdiary.sharedcode.utils.Day
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import pl.kpob.dietdiary.sharedcode.view.CalendarView

class CalendarPresenter(private val appNavigator: AppNavigator) {

    private var view: CalendarView? = null

    fun onShow(view: CalendarView) {
        view.viewTitle = "Wybierz zakres"
    }

    fun onMonthSelected(date: Day) {
        val dt = DateTime(date.year, date.month, date.day)

        val leapYear = dt.year.isLeap
        val length = if(leapYear) dt.month.daysLeap else dt.month.daysCommon

        val lastDay = Day.from(date.year, date.month, length)
        goToSummary(date, lastDay)
    }

    fun onRangeSelected(first: Day?, last: Day?) {
        if (first == null || last == null) return
        goToSummary(first, last)
    }


    fun onThisWeekClick() {
        val now = DateTime.now()

        val firstDayOfWeek = DateTime.now().minus(7.days)

        val start = Day.from(firstDayOfWeek.yearInt, firstDayOfWeek.month0, firstDayOfWeek.dayOfMonth)
        val end = Day.from(now.yearInt, now.month0, now.dayOfMonth)
        goToSummary(start, end)
    }


    fun onThisMothClick() {
        val now = DateTime.now()

        val firstDayOfWeek = DateTime.invoke(now.year, now.month, 1)

        val start = Day.from(firstDayOfWeek.yearInt, firstDayOfWeek.month0, firstDayOfWeek.dayOfMonth)
        val end = Day.from(now.yearInt, now.month0, now.dayOfMonth)
        goToSummary(start, end)

    }

    fun onLastMothClick() {
        val now = DateTime.now()

        val firstDay = DateTime.invoke(
                if(now.month0 == 0) now.yearInt - 1 else now.yearInt,
                if(now.month0 == 0) 11 else now.month0-1,
                1
        )


        val lastDay = DateTime(firstDay.unixMillisLong).plus(1.months)

        val start = Day.from(firstDay.yearInt, firstDay.month0, firstDay.dayOfMonth)
        val end = Day.from(lastDay.yearInt, lastDay.month0, lastDay.dayOfMonth)
        goToSummary(start, end)
    }


    private fun goToSummary(first: Day, last: Day) {
     //   view?.loader?.hide()
        appNavigator.goToRangeSummary(first, last)
    }

}