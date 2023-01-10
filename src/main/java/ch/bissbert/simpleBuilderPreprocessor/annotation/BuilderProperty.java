package ch.bissbert.simpleBuilderPreprocessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a field to be used in the builder.
 * !IMPORTANT! The class that contains the field must be annotated with {@link Builder}.
 * The builder will have a method for each field of the class that is annotated with {@link BuilderProperty}.
 * The builder will have a #build() method that returns the object.
 *
 * @author Bissbert
 * @version 1.0
 * @see Builder
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface BuilderProperty {
}
