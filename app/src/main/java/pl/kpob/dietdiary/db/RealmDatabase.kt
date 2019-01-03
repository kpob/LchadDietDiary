package pl.kpob.dietdiary.db

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.Sort
import pl.kpob.dietdiary.sharedcode.repository.*

class RealmDatabase<T: RealmObject>(
        val realm: Realm,
        val clazz: Class<T>): Database<T> {

    override fun query(vararg parts: QueryPart): List<T> {
        val query = buildQuery(parts)

        val sortParts = parts.filter { it is Sorted }.map { it as Sorted }

        val result = if (sortParts.isEmpty()) {
            query.findAll()
        } else {
            val fields = sortParts.map { it.sort.field }.toTypedArray()
            val sorts = sortParts.map { if (it.sort is Ascending) Sort.ASCENDING else Sort.DESCENDING }.toTypedArray()
            query.findAllSorted(fields, sorts)
        }

        val limit = parts.firstOrNull { it is Limit } as? Limit
        return if (limit == null) result else result.take(limit.i)
    }

    override fun querySingle(vararg parts: QueryPart): T? {
        val query = buildQuery(parts)
        return query.findFirst()
    }

    private fun buildQuery(parts: Array<out QueryPart>): RealmQuery<T> {
        var rq = realm.where(clazz)

        parts.forEach {
            rq = when(it) {
                is EqualsString -> rq.equalTo(it.field, it.value)
                is InInts -> rq.`in`(it.field, it.array)
                is InStrings -> rq.`in`(it.field, it.array)
                is InLongs -> rq.`in`(it.field, it.array)
                is BetweenLongs -> rq.between(it.field, it.start, it.end)
                else -> rq
            }
        }
        return rq
    }
}