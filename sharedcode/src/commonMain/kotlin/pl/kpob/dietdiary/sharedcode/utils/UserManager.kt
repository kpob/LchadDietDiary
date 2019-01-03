package pl.kpob.dietdiary.sharedcode.utils

interface UserManager {

    fun signIn(with: CredentialsProvider, result: (Boolean) -> Unit)
    fun signOut()

}