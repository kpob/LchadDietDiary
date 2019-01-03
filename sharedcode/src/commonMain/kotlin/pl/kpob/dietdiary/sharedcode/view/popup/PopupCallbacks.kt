package pl.kpob.dietdiary.sharedcode.view.popup


class PopupCallbacks {

    var ok: CallbackItem? = null
    var cancel: CallbackItem? = null

    fun ok(title: String, f: (CallbackPayload) -> Unit) {
        ok = CallbackItem(title, f)
    }

    fun cancel(title: String, f: (CallbackPayload) -> Unit = {}) {
        cancel = CallbackItem(title, f)
    }

    companion object {
        fun init(f: PopupCallbacks.() -> Unit): PopupCallbacks {
            return PopupCallbacks().also { it.f() }
        }
    }
}

data class CallbackItem(val title: String, val action: (CallbackPayload) -> Unit) {

    operator fun invoke(payload: CallbackPayload = EmptyPayload) {
        action.invoke(payload)
    }
}

sealed class CallbackPayload
object EmptyPayload: CallbackPayload()
class EditMealTimePayload(val hour: Int, val minute: Int): CallbackPayload()
class AddTemplatePayload(val name: String): CallbackPayload()
class LoadTemplatePayload(val which: Int): CallbackPayload()
class NewTagPayload(val name: String): CallbackPayload()
