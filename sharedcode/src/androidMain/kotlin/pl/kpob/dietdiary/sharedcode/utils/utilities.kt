package pl.kpob.dietdiary.sharedcode.utils

import java.util.*

actual fun Float.asFormattedString(decimal: Int): String {
    return java.lang.String.format(Locale.getDefault(), "%.2f", this)
}

actual fun nextId(): String = UUID.randomUUID().toString()

actual typealias Position = Int