package ch.bissbert.simpleBuilderPreprocessor;

import ch.bissbert.simpleBuilderPreprocessor.annotation.BuilderProperty;
import ch.bissbert.simpleBuilderPreprocessor.file.SimpleNameTypeMirror;
import ch.bissbert.simpleBuilderPreprocessor.file.Visibility;
import ch.bissbert.simpleBuilderPreprocessor.file.builder.AttributeStringBuilder;
import ch.bissbert.simpleBuilderPreprocessor.file.builder.method.JavaMethodParamStringBuilder;
import ch.bissbert.simpleBuilderPreprocessor.file.builder.method.MethodStringBuilder;
import ch.bissbert.simpleBuilderPreprocessor.file.writer.ClassWriter;
import ch.bissbert.simpleBuilderPreprocessor.util.NameUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("ch.bissbert.simpleBuilderPreprocessor.annotation.BuilderProperty")
public class BuilderProcessorWithClasses extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //get all elements annotated with BuilderProperty
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(BuilderProperty.class);
        TypeElement elementClass = ((TypeElement) annotatedElements.stream().findAny().get()
                .getEnclosingElement());
        List<Element> allSetterMethods = ((List<Element>) elementClass.getEnclosedElements())
                .stream()
                .filter(element -> element.asType() instanceof ExecutableType)
                .filter(
                        element -> ((ExecutableType) element.asType()).getParameterTypes().size() == 1
                                && element.getSimpleName().toString().startsWith("set")
                ).toList();
        List<Element> filteredSetterMethods = checkElementsAndReturnSetters(annotatedElements, allSetterMethods);
        if (filteredSetterMethods.size() > 0) {
            try {
                createBuilderAndInject(elementClass, filteredSetterMethods);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    private void createBuilderAndInject(TypeElement typeElement, List<Element> setterMethods) throws IOException {
        final String className = typeElement.getQualifiedName().toString();
        final String builderClassName = className + "Builder";
        final String builderSimpleClassName = typeElement.getSimpleName().toString() + "Builder";

        final int lastIndexOfDot = className.lastIndexOf(".");
        String packageName = null;
        if (lastIndexOfDot > 0) {
            packageName = className.substring(0, lastIndexOfDot);
        }
        Map<String, String> setterParamMap = setterMethods.stream()
                .filter(element -> element.asType() instanceof ExecutableType)
                .collect(
                        Collectors.toMap(
                                element -> element.getSimpleName().toString(),
                                element -> ((ExecutableType) element.asType()).getParameterTypes().get(0).toString()
                        )
                );

        //generating file
        JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(builderClassName);

        try (
                ClassWriter classWriter =
                        new ClassWriter(
                                new PrintWriter(fileObject.openWriter()),
                                builderSimpleClassName,
                                packageName,
                                Visibility.PUBLIC
                        )
        ) {

            List<AttributeStringBuilder> attributeStringBuilders = classWriter.getAttributeStringBuilders();
            List<MethodStringBuilder> methodStringBuilders = classWriter.getMethodStringBuilders();

            //Object of the class to be built
            AttributeStringBuilder objectToBuild = new AttributeStringBuilder(false, false, "object", new SimpleNameTypeMirror(className), Visibility.PRIVATE);
            attributeStringBuilders.add(objectToBuild);


            //build method
            MethodStringBuilder buildMethod = new MethodStringBuilder(false, "build", new SimpleNameTypeMirror(className), Visibility.PUBLIC, "return object;");
            methodStringBuilders.add(buildMethod);

            //setter Methods
            for (Map.Entry<String, String> setterEntry : setterParamMap.entrySet()) {
                final String attributeName = NameUtil.getAttributeNameFromSetter(setterEntry.getKey());
                final String attributeType = setterEntry.getValue();

                //setter method
                MethodStringBuilder setterMethod = new MethodStringBuilder(
                        false,
                        attributeName,
                        new SimpleNameTypeMirror(builderSimpleClassName),
                        Visibility.PUBLIC,
                        "object." + setterEntry.getKey() + "(" + attributeName + ");return this;",
                        new JavaMethodParamStringBuilder(new SimpleNameTypeMirror(attributeType), attributeName)
                );
                methodStringBuilders.add(setterMethod);
            }
        }
    }

    private List<Element> checkElementsAndReturnSetters(Set<? extends Element> annotatedElements, List<Element> setterMethods) {
        Map<String, Element> setterMap = new HashMap<>();
        for (Element setter : setterMethods) {
            final String setterValueFirstLetterLowercase = NameUtil.getAttributeNameFromSetter(setter.getSimpleName().toString());
            setterMap.put(setterValueFirstLetterLowercase, setter);
        }
        List<Element> setterElements = new ArrayList<>();
        for (Element element : annotatedElements) {
            if (setterMap.containsKey(element.getSimpleName().toString())) {
                setterElements.add(setterMap.get(element.getSimpleName().toString()));
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@BuilderProperty requires a setter matching its name to exist", element);
            }
        }
        return setterElements;
    }
}
