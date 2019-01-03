package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.viewmodel.TagsViewModel

interface TagCloudView: TitledView {
    fun initTags(viewModel: TagsViewModel)
    fun addTag(name: String)

}