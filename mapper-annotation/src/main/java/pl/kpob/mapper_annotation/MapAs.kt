package pl.kpob.mapper_annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class MapAs(val mapAs: String)
