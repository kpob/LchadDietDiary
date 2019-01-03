package pl.kpob.dietdiary.sharedcode.view.popup

import pl.kpob.dietdiary.sharedcode.view.PopupData

interface PopupDisplayer {

    fun display(viewModel: PopupViewModel)
}


sealed class PopupViewModel(val data: PopupData, val callbacks: PopupCallbacks)
class DeleteMealPopup(data: PopupData, callbacks: PopupCallbacks): PopupViewModel(data, callbacks)
class EditTimePopup(data: PopupData, callbacks: PopupCallbacks): PopupViewModel(data, callbacks)
class AddTemplatePopup(data: PopupData, callbacks: PopupCallbacks): PopupViewModel(data, callbacks)
class LoadTemplatePopup(data: PopupData, callbacks: PopupCallbacks, val names: Array<String>): PopupViewModel(data, callbacks)
class NewTagPopup(data: PopupData, callbacks: PopupCallbacks, val hint: String): PopupViewModel(data, callbacks)