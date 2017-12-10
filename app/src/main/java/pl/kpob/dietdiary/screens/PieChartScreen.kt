package pl.kpob.dietdiary.screens

import android.content.Context
import com.wealthfront.magellan.rx.RxScreen
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.MealDetails
import pl.kpob.dietdiary.mapper.MealMapper
import pl.kpob.dietdiary.usingRealm
import pl.kpob.dietdiary.views.PieChartView

/**
 * Created by kpob on 21.10.2017.
 */
class PieChartScreen(private val mealId: String) : RxScreen<PieChartView>(), AnkoLogger {

    val meal: MealDetails by lazy {
        usingRealm {
            val dto = it.where(MealDTO::class.java).equalTo("id", mealId).findFirst()!!
            MealMapper.mapToMealDetails(dto)
        }

    }

    val nutrients
        get() = mapOf(
                "Węglowodany" to meal.carbohydrates,
                "Mct" to meal.mtc, "Lct" to meal.lct, "Białko" to meal.protein,"Błonnik" to meal.roughage,
                 "Sól" to meal.salt
        )

    override fun createView(context: Context?): PieChartView = PieChartView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.enableHomeAsUp { navigator.goBack() }
            it.toolbarTitle = "${meal.time} ${meal.type.string}"
            it.initList(meal.ingredients)
            it.initChart()
        }


    }
}