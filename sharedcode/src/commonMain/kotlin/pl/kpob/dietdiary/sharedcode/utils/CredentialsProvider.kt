package pl.kpob.dietdiary.sharedcode.utils

import pl.kpob.dietdiary.sharedcode.model.Credentials

interface CredentialsProvider {

    val credentials: Credentials
}