package pl.kpob.dietdiary.sharedcode.utils

interface PlatformInitializer {

    fun refreshData()
    fun stopRefreshingData()

    fun initNotifications()
    fun handleError()
}