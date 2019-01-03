package pl.kpob.dietdiary.sharedcode.utils

import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.model.MealDetails
import pl.kpob.dietdiary.sharedcode.model.MealIngredient
import pl.kpob.dietdiary.sharedcode.repository.MealsInRangeSpecification
import pl.kpob.dietdiary.sharedcode.repository.Repository


class MealsInRangeProvider(
        private val start: Long,
        private val end: Long,
        private val repo: Repository<MealDTO, MealDetails>) {


    val data: List<MealDetails> get() {
        return try {
            val spec = MealsInRangeSpecification(start, end)
            repo.query(spec)
        } catch (e: Exception) {
            listOf()
        }
    }

    val ingredients: List<MealIngredient> by lazy {
        data.map { it.ingredients }
                .flatten()
                .groupBy { it.id }
                .map {
                    val totalWeight = it.value.map { it.weight }.sum()
                    MealIngredient(it.value[0].id, it.value[0].name, it.value[0].calories, totalWeight)
                }
                .sortedByDescending { it.weight }
    }
}