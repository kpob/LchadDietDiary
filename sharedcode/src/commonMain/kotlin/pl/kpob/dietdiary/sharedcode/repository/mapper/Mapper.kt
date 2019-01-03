package pl.kpob.dietdiary.sharedcode.repository.mapper

interface Mapper<in I, out O> {

    fun map(input: I?): O?
    fun map(input: List<I>): List<O>

}