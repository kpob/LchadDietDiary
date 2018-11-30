package pl.kpob.mapper_annotation


/**
 * Created by kpob on 16.12.2017.
 */

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class AutoMapping(
        val generateDomainModel: Boolean = true,
        val generateFirebaseModel: Boolean = true,
        val generateRepository: Boolean = true,
        val generateContract: Boolean = true)
