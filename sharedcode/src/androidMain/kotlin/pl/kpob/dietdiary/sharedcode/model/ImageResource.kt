package pl.kpob.dietdiary.sharedcode.model

class AndroidImageResource(override val intValue: Int): ImageResource {
    override val stringValue: String = ""
}

actual object MilkBottleImageResource : ImageResource by AndroidImageResource(0)
actual object PorridgeImageResource : ImageResource by AndroidImageResource(0)
actual object DinnerImageResource : ImageResource by AndroidImageResource(0)