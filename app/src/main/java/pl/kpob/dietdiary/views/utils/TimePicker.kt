package pl.kpob.dietdiary.views.utils

import android.app.Activity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.ZonedDateTime
import pl.kpob.dietdiary.find
import pl.kpob.dietdiary.R

/**
 * Created by kpob on 10.12.2017.
 */
class TimePicker {

    val dt = ZonedDateTime.now()

    private var minutes = dt.minute
    private var hours = dt.hour

    private val minutesScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                (recyclerView?.layoutManager as? LinearLayoutManager)?.let { lm ->
                    val fvi = lm.findFirstCompletelyVisibleItemPosition()
                    recyclerView?.scrollToPosition(fvi + 4)

                    minutes = fvi
                }

            }
        }
    }

    private val hoursScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                (recyclerView?.layoutManager as? LinearLayoutManager)?.let { lm ->
                    val fvi = lm.findFirstCompletelyVisibleItemPosition()
                    recyclerView?.scrollToPosition(fvi + 4)
                    hours = fvi
                }

            }
        }
    }

    fun dialog(activity: Activity, accept: (Int, Int) -> Unit) = AlertDialog.Builder(activity)
                .setView(activity.layoutInflater.inflate(R.layout.view_time_picker, null).apply {

                    find<RecyclerView>(R.id.hours).let {
                        initList(activity, it, HOURS)
                    }
                    find<RecyclerView>(R.id.minutes).let {
                        initList(activity, it, MINUTES)
                    }
                })
                .setPositiveButton("Zmień") { dialog, which -> accept(minutes, hours) }
                .setNegativeButton("Anuluj") { dialog, which -> }
                .create()

    private fun initList(activity: Activity, list: RecyclerView, mode: Int) {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = numberAdapter(activity, if(mode == MINUTES) 60 else 24)
        list.layoutManager?.scrollToPosition(if(mode == MINUTES) dt.minute else dt.hour)
        list.addOnScrollListener(if(mode == MINUTES) minutesScrollListener else hoursScrollListener)
    }

    private fun numberAdapter(activity: Activity, items: Int) = object: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(activity.layoutInflater.inflate(R.layout.item_clock, parent, false)) {}


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            holder?.itemView?.find<TextView>(R.id.time)?.let {
                it.text = when(position) {
                    in 0..1 -> ""
                    in itemCount-2 until itemCount -> ""
                    else -> {
                        val value = position - 2
                        if(value < 10) "0$value" else value.toString()
                    }
                }
            }

        }

        override fun getItemCount(): Int = items + 4

    }


    companion object {
        private val MINUTES = 1
        private val HOURS = 2
    }

}