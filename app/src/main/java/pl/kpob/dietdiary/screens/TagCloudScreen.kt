package pl.kpob.dietdiary.screens

import android.content.Context
import android.support.v7.app.AlertDialog
import com.wealthfront.magellan.Screen
import org.jetbrains.anko.*
import pl.kpob.dietdiary.di.components.DaggerTagsComponent
import pl.kpob.dietdiary.di.modules.TagsModule
import pl.kpob.dietdiary.di.modules.common.PopupModule
import pl.kpob.dietdiary.sharedcode.presenter.TagCloudPresenter
import pl.kpob.dietdiary.sharedcode.view.popup.*
import pl.kpob.dietdiary.views.TagCloudView
import javax.inject.Inject

/**
 * Created by kpob on 13.12.2017.
 */
class TagCloudScreen : ScopedScreen<TagCloudView>(), AnkoLogger, PopupDisplayer {

    @Inject lateinit var presenter: TagCloudPresenter

    override fun createView(context: Context?) = TagCloudView(context!!).also {
        it.enableHomeAsUp { navigator.goBack() }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        DaggerTagsComponent.builder()
                .appComponent(appComponent)
                .popupModule(PopupModule(this))
                .tagsModule(TagsModule())
                .build().inject(this)
        presenter.onShow(view)
    }

    fun getTagColor(txt: String) = presenter.getTagColor(txt)

    fun onNewTagClick() = presenter.onNewTagClick()

    override fun display(viewModel: PopupViewModel) {
        if (viewModel !is NewTagPopup) return

        val callbacks = viewModel.callbacks
        showDialog {
            AlertDialog.Builder(activity).apply {
                setView(
                        context.verticalLayout {
                            val et = editText {
                                hint = viewModel.hint
                            }

                            setNegativeButton(callbacks.cancel?.title) { _, _ -> callbacks.cancel?.invoke() }
                            setPositiveButton(callbacks.ok?.title) { _, _ ->
                                val name = et.editableText.toString()
                                callbacks.ok?.invoke(NewTagPayload(name))
                            }
                        }
                )
            }.create()
        }
    }

}