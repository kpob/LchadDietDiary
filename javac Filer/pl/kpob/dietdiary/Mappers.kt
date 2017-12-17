package pl.kpob.dietdiary

import pl.kpob.dietdiary.db.TagDTO

class MapperTag {
  fun map(input: TagDTO) = Tag(id=input.id,ct=input.creationTime,name=input.name,color=input.color,activeColor=input.activeColor,textColor=input.textColor,activeTextColor=input.activeTextColor)
}
