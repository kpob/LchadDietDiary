package pl.kpob.dietdiary.sharedcode.utils

import platform.Foundation.*

actual fun Float.asFormattedString(decimal: Int): String {
    return NSNumber.numberWithFloat(this).toString()
}

actual fun nextId(): String = NSUUID().UUIDString