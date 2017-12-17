package pl.kpob.dietdiary.screens

import android.content.Context
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.wealthfront.magellan.rx.RxScreen
import org.jetbrains.anko.*
import pl.kpob.dietdiary.repo.TagRepository
import pl.kpob.dietdiary.views.TagCloudView

/**
 * Created by kpob on 13.12.2017.
 */
class TagCloudScreen : RxScreen<TagCloudView>(), AnkoLogger {

    companion object {
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

    private val tagRepo by lazy { TagRepository() }

    override fun createView(context: Context?) = TagCloudView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.toolbarTitle = "Tagi"
            it.enableHomeAsUp { navigator.goBack() }
            it.initTags()
        }
    }


    fun getTagColor(txt: String) = colors[txt.map { it.toInt() }.sum().rem(colors.size)]

    fun onNewTagClick() = showDialog {
        AlertDialog.Builder(activity).apply {
            setView(
                    context.verticalLayout {
                        val et = editText {
                            hint = "Tag"
                        }

                        setPositiveButton("Ok") { dialog, which ->
                            view.addTag(et.editableText.toString())
                        }

                        setNegativeButton("Anuluj") { dialog, which -> }
                    }
            )
        }.create()

    }

}