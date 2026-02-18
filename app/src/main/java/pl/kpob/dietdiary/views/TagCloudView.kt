package pl.kpob.dietdiary.views

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.adroitandroid.chipcloud.Chip
import com.adroitandroid.chipcloud.ChipCloud
import com.adroitandroid.chipcloud.ChipListener
import com.wealthfront.magellan.BaseScreenView
import pl.kpob.dietdiary.dip
import pl.kpob.dietdiary.find
import pl.kpob.dietdiary.onClick
import pl.kpob.dietdiary.opaque
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.screens.TagCloudScreen

/**
 * Created by kpob on 13.12.2017.
 */
class TagCloudView(ctx: Context) : BaseScreenView<TagCloudScreen>(ctx), ToolbarManager {


    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val tagCloud by lazy { find<ChipCloud>(R.id.chip_cloud) }
    private val addBtn by lazy { find<View>(R.id.add) }

    private val chipTextSize by lazy { 16 }
    private val chipHeight by lazy { dip(48) }
    private val mode by lazy { ChipCloud.Mode.MULTI }

    init {
        View.inflate(ctx, R.layout.screen_tag_cloud, this)
        addBtn.onClick {
            screen.onNewTagClick()
        }
    }

    fun initTags() {
        arrayOf("Obiadki", "Deserki", "Mleczko", "Nabiał", "Oleje", "Inne").map {
            it.toTag()
        }.forEach { tagCloud.addView(it) }
    }

    fun addTag(name: String) {
        tagCloud.addView(name.toTag())
    }

    private fun String.toTag(): Chip = Chip.ChipBuilder()
            .index(tagCloud.childCount)
            .label(this)
            .textSize(dip(chipTextSize))
            .chipHeight(chipHeight)
            .unselectedFontColor(0x546e7a.opaque)
            .selectedFontColor(Color.WHITE)
            .mode(mode)
            .unselectedColor(screen.getTagColor(this).first)
            .selectedColor(screen.getTagColor(this).second)
            .build(context)
}