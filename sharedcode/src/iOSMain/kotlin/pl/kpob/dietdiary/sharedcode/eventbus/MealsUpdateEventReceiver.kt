package pl.kpob.dietdiary.sharedcode.eventbus

import pl.kpob.dietdiary.sharedcode.view.SyncView

actual class MealsUpdateEventReceiver {

    actual fun receive(ev: MealsUpdateEvent) {}

    actual var syncView: SyncView? = null

}