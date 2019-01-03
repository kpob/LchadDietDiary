package pl.kpob.dietdiary.screens

import android.content.Context
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.di.components.DaggerRangeSummaryComponent
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.di.modules.RangeSummaryModule
import pl.kpob.dietdiary.sharedcode.presenter.RangeSummaryPresenter
import pl.kpob.dietdiary.sharedcode.utils.Day
import pl.kpob.dietdiary.views.RangeSummaryView
import javax.inject.Inject

class RangeSummaryScreen(
        private val firstDay: Day,
        private val lastDay: Day
) : ScopedScreen<RangeSummaryView>(), AnkoLogger {

    @Inject lateinit var presenter: RangeSummaryPresenter

    override fun createView(context: Context?): RangeSummaryView {
        return RangeSummaryView(context!!).apply {
            enableHomeAsUp { navigator.goBack() }
        }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        DaggerRangeSummaryComponent.builder()
                .appComponent(appComponent)
                .dataModule(DataModule(realm))
                .rangeSummaryModule(RangeSummaryModule(firstDay, lastDay))
                .build().inject(this)
        presenter.onShow(view)
    }

}