package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.sharedcode.presenter.TagCloudPresenter
import pl.kpob.dietdiary.sharedcode.view.popup.PopupDisplayer

@Module
class TagsModule {

    @Provides
    fun providePresenter(popupDisplayer: PopupDisplayer): TagCloudPresenter = TagCloudPresenter(popupDisplayer)
}