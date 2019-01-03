package pl.kpob.dietdiary.sharedcode.model

interface MealIngredientDTO {
    var ingredientId: String
    var weight: Float
}
interface MealDTO {
    var id: String
    var time: Long
    var name: String
    var ingredients: List<MealIngredientDTO>
}
interface IngredientDTO {
    var id: String
    var name: String
    var mtc: Float
    var lct: Float
    var carbohydrates: Float
    var protein: Float
    var salt: Float
    var roughage: Float
    var calories: Float
    var category: Int
    var useCount: Int
}
interface TagDTO {
    var id: String
    var creationTime: Long
    var name: String
    var color: Int
    var activeColor: Int
    var textColor: Int
    var activeTextColor: Int
}
interface MealTemplateDTO {
    var id: String
    var name: String
    var type: String
    var ingredientIds: List<String>
}