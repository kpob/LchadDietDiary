package pl.kpob.dietdiary.repo

/**
 * Created by kpob on 11.12.2017.
 */
interface Mapper<in I, out O> {

    fun map(input: I?): O?
    fun map(input: List<I>): List<O>

}