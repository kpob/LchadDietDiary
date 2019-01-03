package pl.kpob.dietdiary.sharedcode.model

interface ImageResource {
    val intValue: Int
    val stringValue: String
}
expect object MilkBottleImageResource: ImageResource
expect object PorridgeImageResource: ImageResource
expect object DinnerImageResource: ImageResource


fun createApplicationScreenMessage() : String {
    return "Kotlin Rocks on as ..."
}


