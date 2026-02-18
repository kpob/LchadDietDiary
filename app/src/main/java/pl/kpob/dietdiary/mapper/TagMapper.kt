package pl.kpob.dietdiary.mapper

import pl.kpob.dietdiary.db.TagDTO
import pl.kpob.dietdiary.domain.Tag
import pl.kpob.dietdiary.repo.Mapper

class TagMapper : Mapper<TagDTO, Tag> {

    override fun map(input: TagDTO?): Tag? {
        if (input == null) return null
        return Tag(
                id = input.id,
                tagName = input.name,
                color = input.color,
                activeColor = input.activeColor,
                textColor = input.textColor,
                activeTextColor = input.activeTextColor
        )
    }

    override fun map(input: List<TagDTO>): List<Tag> = input.mapNotNull { map(it) }
}
