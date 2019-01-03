package pl.kpob.dietdiary.sharedcode.utils

interface UserTokenProvider {

    var token: String

    fun initToken()
}