@file:JvmName("KotlinUtils")

package pl.kpob.dietdiary.sharedcode.utils

import com.soywiz.klock.DateTime
import kotlin.jvm.JvmName

expect fun Float.asFormattedString(decimal: Int = 2): String
expect fun nextId(): String

class MyDateTime {

    val dt: DateTime

    constructor() {
        dt = DateTime.now()
    }

    constructor(timestamp: Long) {
        dt = DateTime(timestamp)
    }

    val date: String get() = "${dt.dayOfMonth}-${dt.month1}-${dt.yearInt}"
    val time: String get() = "${dt.hours}:${dt.minutes}"

    val year: Int get() = dt.yearInt
    val dayOfYear: Int get() = dt.dayOfYear
    val hourOfDay: Int get() = dt.hours
    val minuteOfHour: Int get() = dt.minutes

    val timestamp: Long get() = dt.unixMillisLong

    fun withTime(h: Int, m: Int): MyDateTime {
        return MyDateTime(DateTime.invoke(dt.year, dt.month, dt.dayOfMonth, h, m).unixMillisLong)
    }

}

class Day(val year: Int, val month: Int, val day: Int) {

    companion object {
        fun from(y: Int, m: Int, d: Int): Day = Day(y, m, d)
    }
}

fun currentTime(): Long {
    return MyDateTime().timestamp
}

val Long.asReadableString: String
    get() = MyDateTime(this).let {
        val h = it.hourOfDay
        val m = it.minuteOfHour
        "$h:${if(m < 10) "0$m" else m.toString()}"
    }