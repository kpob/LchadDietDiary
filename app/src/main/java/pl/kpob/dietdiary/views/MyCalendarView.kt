package pl.kpob.dietdiary.views

import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.View
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_RANGE
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.listeners.onClick
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.screens.CalendarScreen
import pl.kpob.dietdiary.sharedcode.view.CalendarView
import pl.kpob.dietdiary.show

class MyCalendarView(ctx: Context) : BaseScreenView<CalendarScreen>(ctx), ToolbarManager, CalendarView,  AnkoLogger {

    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val calendarView by lazy { find<MaterialCalendarView>(R.id.calendarView) }
    val loader by lazy { find<View>(R.id.loader) }

    override var viewTitle: String
        get() = toolbarTitle
        set(value) { toolbarTitle = value }

    init {
        View.inflate(ctx, R.layout.screen_calendar, this)

        calendarView.setOnTitleClickListener {
            loader.show()
            screen.onMonthSelected(calendarView.currentDate)

        }
        calendarView.setOnDateChangedListener { calendarView, calendarDay, b -> }

        calendarView.selectionMode = SELECTION_MODE_RANGE
        calendarView.setOnRangeSelectedListener { _, days ->
            loader.show()
            screen.onRangeSelected(days.first(), days.last())
        }


        find<View>(R.id.this_week).onClick { screen.onThisWeekClick()  }
        find<View>(R.id.this_month).onClick { screen.onThisMothClick() }
        find<View>(R.id.last_month).onClick { screen.onLastMothClick() }
    }
}