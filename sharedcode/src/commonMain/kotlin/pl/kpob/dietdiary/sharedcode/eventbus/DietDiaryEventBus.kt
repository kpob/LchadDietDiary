package pl.kpob.dietdiary.sharedcode.eventbus

expect class DietDiaryEventBus {

    fun register(any: Any)
    fun unregister(any: Any)
}