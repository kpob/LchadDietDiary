package pl.kpob.dietdiary.sharedcode.eventbus

import org.greenrobot.eventbus.EventBus

actual class DietDiaryEventBus {

    private val bus: EventBus get() = EventBus.getDefault()

    actual fun register(any: Any) {
        bus.register(any)
    }

    actual fun unregister(any: Any) {
        bus.unregister(any)
    }
}