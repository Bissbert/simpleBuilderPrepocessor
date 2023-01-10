package ch.bissbert.simpleBuilderPreprocessor;

import ch.bissbert.simpleBuilderPreprocessor.annotation.Builder;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes({"ch.bissbert.simpleBuilderPreprocessor.annotation.BuilderProperty", "ch.bissbert.simpleBuilderPreprocessor.annotation.Builder"})
public class BuilderProcessor extends AbstractProcessor {
    private Trees trees;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<Element> builderClassElements = (List<Element>) roundEnv.getElementsAnnotatedWith(Builder.class);
        return false;
    }
}
