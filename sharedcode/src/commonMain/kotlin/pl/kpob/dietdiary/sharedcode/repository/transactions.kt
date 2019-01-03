package pl.kpob.dietdiary.sharedcode.repository

interface Transaction<T> {

    fun execute(input: T, db: Database<T>)
    fun execute(input: Collection<T>, db: Database<T>)
}

interface AddTransaction<T>: Transaction<T>
interface UpdateTransaction<T>: Transaction<T>
interface RemoveTransaction<T>: Transaction<T>

interface ChainTransaction<T> {

    val data: List<ChainTransactionData<T>>

    fun execute(db: Database<T>)
}

interface ChainTransactionData<T> {
    val transaction: Transaction<T>
    val data: List<T>?
    val item: T?
}


data class MultiData<T>(
        override val transaction: Transaction<T>,
        override val data: List<T>?): ChainTransactionData<T> {
    override val item: T? = null
}

data class SingleData<T>(
        override val transaction: Transaction<T>,
        override val item: T?): ChainTransactionData<T> {
    override val data: List<T>? = null
}