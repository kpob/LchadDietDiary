package pl.kpob.dietdiary.utils

import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.sharedcode.utils.AppSyncState

object AndroidAppSyncState: AppSyncState {
    override val isSyncing: Boolean
        get() = App.isSyncing
}