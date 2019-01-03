package pl.kpob.dietdiary.sharedcode.repository

import pl.kpob.dietdiary.sharedcode.repository.mapper.Mapper

class Repository<In, out Out>(
        private val mapper: Mapper<In, Out>,
        private val database: Database<In>
) {

    fun data(spec: Specification<In>): List<In> = spec.getCollection(database)

    fun query(spec: Specification<In>): List<Out> {
        val collection = spec.getCollection(database)
        return mapper.map(collection)
    }

    fun querySingle(spec: Specification<In>): Out? {
        val item: In = spec.getItem(database) ?: return null
        return mapper.map(item)
    }

    fun update(spec: Specification<In>, transaction: Transaction<In>) {
        val collection = spec.getCollection(database)
        transaction.execute(collection, database)
    }

    fun executeChainTransaction(transaction: ChainTransaction<In>) {
        transaction.execute(database)
    }

    fun executeTransaction(lists: List<In>, transaction: Transaction<In>) {
        transaction.execute(lists, database)
    }

    fun executeTransaction(item: In, transaction: Transaction<In>) {
        transaction.execute(item, database)
    }


    fun insert(item: In, transaction: AddTransaction<In>) {
        transaction.execute(item, database)
    }

    fun insert(list: List<In>, transaction: AddTransaction<In>) {
        transaction.execute(list, database)
    }

    fun delete(spec: Specification<In>, transaction: RemoveTransaction<In>) {
        val collection = spec.getCollection(database)
        transaction.execute(collection, database)
    }


}