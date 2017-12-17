package pl.kpob.mapper_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kpob on 16.12.2017.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoMapping {
    boolean generateDomainModel() default true;
    boolean generateFirebaseModel() default true;
    boolean generateRepository() default true;
    boolean generateContract() default true;

}
