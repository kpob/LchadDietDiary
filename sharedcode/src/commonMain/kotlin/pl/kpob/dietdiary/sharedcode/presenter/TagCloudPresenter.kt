package pl.kpob.dietdiary.sharedcode.presenter

import pl.kpob.dietdiary.sharedcode.view.PopupData
import pl.kpob.dietdiary.sharedcode.view.TagCloudView
import pl.kpob.dietdiary.sharedcode.view.popup.NewTagPayload
import pl.kpob.dietdiary.sharedcode.view.popup.NewTagPopup
import pl.kpob.dietdiary.sharedcode.view.popup.PopupCallbacks
import pl.kpob.dietdiary.sharedcode.view.popup.PopupDisplayer
import pl.kpob.dietdiary.sharedcode.viewmodel.TagsViewModel

class TagCloudPresenter(private val popupDisplayer: PopupDisplayer) {


    companion object {
        private val Int.opaque: Int
            get() = this or 0xff000000.toInt()

        private val colors = listOf(
                0xf44336 to 0xc62828,
                0xe91e63 to 0xad1457,
                0x9c27b0 to 0x6a1b9a,
                0x673ab7 to 0x4527a0,
                0x3f51b5 to 0x283593,
                0x2196f3 to 0x1565c0,
                0x03a9f4 to 0x0277bd,
                0x00bcd4 to 0x00838f,
                0x009688 to 0x00695ca,
                0x4caf50 to 0x2e7d32,
                0x8bc34a to 0x558b2f,
                0xcddc39 to 0x9e9d24,
                0xffeb3b to 0xf9a825,
                0xffc107 to 0xff8f00
        ).map { it.first.opaque to it.second.opaque }
    }

    private var view: TagCloudView?  = null


    fun onShow(view: TagCloudView) {
        this.view = view
        view.viewTitle = "Tagi"
        view.initTags(TagsViewModel())
    }

    fun getTagColor(txt: String) = colors[txt.map { it.toInt() }.sum().rem(colors.size)]

    fun onNewTagClick() = popupDisplayer.display(NewTagPopup(
            PopupData(),
            PopupCallbacks.init {
                ok("Ok") { payload ->
                    if (payload is NewTagPayload) {
                        view?.addTag(payload.name)
                    }
                }

                cancel("Anuluj")
            },
            "Tag"
    ))

}