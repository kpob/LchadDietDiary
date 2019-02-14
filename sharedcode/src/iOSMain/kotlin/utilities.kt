package pl.kpob.dietdiary.sharedcode.utils

import platform.Foundation.*
import platform.darwin.NSInteger

actual fun Float.asFormattedString(decimal: Int): String {
    val formatter = NSNumberFormatter()
    formatter.minimumFractionDigits = 2L.toULong()
    formatter.maximumFractionDigits = 2L.toULong()
    formatter.setMinimumIntegerDigits(1.toULong())
    return formatter.stringFromNumber(NSNumber(this)) ?: ""
}

actual fun nextId(): String = NSUUID().UUIDString



actual typealias Position = Long