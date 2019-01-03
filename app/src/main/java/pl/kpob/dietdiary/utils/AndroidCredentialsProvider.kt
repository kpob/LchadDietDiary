package pl.kpob.dietdiary.utils

import android.content.Context
import pl.kpob.dietdiary.sharedcode.model.Credentials
import pl.kpob.dietdiary.sharedcode.utils.CredentialsProvider
import java.io.BufferedReader
import java.io.InputStreamReader

class AndroidCredentialsProvider(private val ctx: Context): CredentialsProvider {

    override val credentials: Credentials
            get() {
                val file = ctx.assets.open("credentials")
                return BufferedReader(InputStreamReader(file, "UTF-8")).use {
                    it.readLine().split(",").let { Credentials(it[0], it[1]) }
                }
            }
}