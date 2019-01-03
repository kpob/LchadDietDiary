package pl.kpob.dietdiary.screens

import android.content.Context
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.di.components.DaggerChartComponent
import pl.kpob.dietdiary.di.modules.ChartModule
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.sharedcode.presenter.ChartPresenter
import pl.kpob.dietdiary.views.PieChartView
import javax.inject.Inject

/**
 * Created by kpob on 21.10.2017.
 */
class PieChartScreen() : ScopedScreen<PieChartView>(), AnkoLogger {

    @Inject lateinit var presenter: ChartPresenter
    private lateinit var ids: List<String>

    constructor(ids: List<String>) : this() {
        this.ids = ids
    }

    constructor(id: String) : this() {
        this.ids = listOf(id)
    }

    override fun createView(context: Context?) = PieChartView(context!!).also {
        it.enableHomeAsUp { navigator.goBack() }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        DaggerChartComponent.builder()
                .appComponent(appComponent)
                .dataModule(DataModule(realm))
                .chartModule(ChartModule(ids))
                .build().inject(this)
        presenter.onShow(view)
    }
}