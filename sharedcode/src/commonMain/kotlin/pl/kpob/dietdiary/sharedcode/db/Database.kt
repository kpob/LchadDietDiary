package pl.kpob.dietdiary.sharedcode.repository

interface Database<T> {

    fun query(vararg parts: QueryPart): List<T>
    fun querySingle(vararg parts: QueryPart): T?
}