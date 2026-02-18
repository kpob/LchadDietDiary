package pl.kpob.dietdiary.screens.utils

import android.app.Activity
import android.app.Dialog
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import pl.kpob.dietdiary.currentTime
import pl.kpob.dietdiary.views.utils.TimePicker

/**
 * Created by kpob on 16.03.2018.
 */
interface TimePickerCreator {

    fun createTimePicker(activity: Activity, cb: (Long) -> Unit): Dialog
}

internal class ITimePickerCreator : TimePickerCreator {
    override fun createTimePicker(activity: Activity, cb: (Long) -> Unit): Dialog =
            TimePicker().dialog(activity) { m, h ->
                val newTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime()), ZoneId.systemDefault())
                        .withMinute(m)
                        .withHour(h)
                        .toInstant().toEpochMilli()

                cb(newTimestamp)
            }
}