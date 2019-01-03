package pl.kpob.dietdiary.sharedcode.eventbus

import pl.kpob.dietdiary.sharedcode.view.SyncView

expect class MealsUpdateEventReceiver {

    var syncView: SyncView?

    fun receive(ev: MealsUpdateEvent)
}