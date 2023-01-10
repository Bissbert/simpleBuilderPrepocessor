package ch.bissbert.simpleBuilderPreprocessor.file.builder.method;

import ch.bissbert.simpleBuilderPreprocessor.ExampleClass;
import ch.bissbert.simpleBuilderPreprocessor.file.SimpleNameTypeMirror;
import ch.bissbert.simpleBuilderPreprocessor.file.Visibility;
import ch.bissbert.simpleBuilderPreprocessor.file.builder.method.ConstructorStringBuilder;
import ch.bissbert.simpleBuilderPreprocessor.file.builder.method.JavaMethodParamStringBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.lang.model.type.TypeMirror;

import static org.junit.Assert.assertEquals;

public class ConstructorStringBuilderTest {

    private TypeMirror stringType;

    @Before
    public void setUp() {
        stringType = new SimpleNameTypeMirror("java.lang.String");
    }

    @Test
    public void toJavaString() {
        JavaMethodParamStringBuilder paramBuilder = new JavaMethodParamStringBuilder(stringType, "name", true);
        ConstructorStringBuilder builder = new ConstructorStringBuilder(
                ExampleClass.class.getName(),
                Visibility.PUBLIC,
                "this.name = name;",
                paramBuilder
        );
        assertEquals("public ExampleClass(final java.lang.String name){this.name = name;}", builder.toJavaString());
        System.out.println(builder.toJavaString());
    }
}
