package pl.kpob.dietdiary.di.modules.common

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.sharedcode.view.popup.PopupDisplayer

@Module
class PopupModule(private val popupDisplayer: PopupDisplayer) {

    @Provides
    fun providePopupDisplayer(): PopupDisplayer = popupDisplayer
}