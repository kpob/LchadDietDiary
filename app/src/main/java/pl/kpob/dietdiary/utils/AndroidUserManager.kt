package pl.kpob.dietdiary.utils

import com.google.firebase.auth.FirebaseAuth
import pl.kpob.dietdiary.sharedcode.utils.CredentialsProvider
import pl.kpob.dietdiary.sharedcode.utils.UserManager
import javax.inject.Inject

class AndroidUserManager
@Inject constructor(): UserManager {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun signIn(with: CredentialsProvider, result: (Boolean) -> Unit) {
        val credentials = with.credentials

        firebaseAuth
                .signInWithEmailAndPassword(credentials.login, credentials.password)
                .addOnCompleteListener { result(it.isSuccessful) }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }



}