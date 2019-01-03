package pl.kpob.dietdiary.sharedcode.repository

sealed class Sort(val field: String)

class Descending(field: String) : Sort(field)
class Ascending(field: String) : Sort(field)
