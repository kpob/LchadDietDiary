package pl.kpob.dietdiary.sharedcode.model

class IOSImageResource(override val stringValue: String): ImageResource {
    override val intValue: Int
        get() = -1
}

actual object MilkBottleImageResource : ImageResource by IOSImageResource("ic_milk_bottle")
actual object PorridgeImageResource : ImageResource by IOSImageResource("ic_porridge")
actual object DinnerImageResource : ImageResource by IOSImageResource("ic_dinner")