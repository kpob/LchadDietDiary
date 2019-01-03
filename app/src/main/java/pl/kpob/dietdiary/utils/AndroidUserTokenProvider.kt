package pl.kpob.dietdiary.utils

import com.google.firebase.iid.FirebaseInstanceId
import pl.kpob.dietdiary.AppPrefs
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.utils.UserTokenProvider

class AndroidUserTokenProvider(private val remoteDatabase: RemoteDatabase): UserTokenProvider {

    override var token: String
        get() = AppPrefs.token
        set(value) { AppPrefs.token = value }

    override fun initToken() {
        if(AppPrefs.token.isEmpty()) {
            val token = FirebaseInstanceId.getInstance().token ?: return
            remoteDatabase.addToken(token)
            AppPrefs.token = token
        }

    }
}