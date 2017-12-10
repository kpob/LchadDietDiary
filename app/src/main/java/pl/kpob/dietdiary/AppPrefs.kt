package pl.kpob.dietdiary

/**
 * Created by kpob on 20.10.2017.
 */
object AppPrefs: Preferences() {

    var isInitialized: Boolean
        set(value) = putPreference("init", value)
        get() = findPreference("init", false)

    var token: String
        set(value) { putPreference("token", value) }
        get() = findPreference("token", "")
}