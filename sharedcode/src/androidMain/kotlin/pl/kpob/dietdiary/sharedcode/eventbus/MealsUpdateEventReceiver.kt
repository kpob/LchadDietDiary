package pl.kpob.dietdiary.sharedcode.eventbus

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pl.kpob.dietdiary.sharedcode.view.SyncView

actual class MealsUpdateEventReceiver {

    actual var syncView: SyncView? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    actual fun receive(ev: MealsUpdateEvent) {
        syncView?.hideSyncBar()
    }

}