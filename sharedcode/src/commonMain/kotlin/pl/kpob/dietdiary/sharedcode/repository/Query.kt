package pl.kpob.dietdiary.sharedcode.repository

sealed class QueryPart

class InStrings(val field: String, val array: Array<String>): QueryPart()
class InInts(val field: String, val array: Array<Int>): QueryPart()
class InLongs(val field: String, val array: Array<Long>): QueryPart()

class ContainsString(val field: String, val value: String): QueryPart()

class EqualsString(val field: String, val value: String): QueryPart()

object All: QueryPart()
object First: QueryPart()

class Sorted(val sort: Sort): QueryPart()

class BetweenInts(val field: String, val start: Int, val end: Int): QueryPart()
class BetweenLongs(val field: String, val start: Long, val end: Long): QueryPart()

class Limit(val i: Int): QueryPart()

class QueryExecutor(val parts: List<QueryPart>) {

//    fun execute()
}