package ch.bissbert.simpleBuilderPreprocessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a class to have a builder generated.
 * The builder will be able to be accessed via the #builder() method of the class.
 * The builder will have a method for each field of the class that is annotated with {@link BuilderProperty}.
 * The builder will have a #build() method that returns the object.
 *
 * @author Bissbert
 * @version 1.0
 * @see BuilderProperty
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Builder {
}
