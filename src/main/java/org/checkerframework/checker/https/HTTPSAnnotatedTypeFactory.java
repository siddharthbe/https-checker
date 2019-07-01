import com.sun.source.tree.Tree;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.defaults.QualifierDefaults;
import org.checkerframework.javacutil.AnnotationBuilder;
import qual.HTTPS;

import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import java.util.Set;

public class HTTPSAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    private final AnnotationMirror HTTPS;

    public HTTPSAnnotatedTypeFactory(BaseTypeChecker checker){
        super(checker);
        this.HTTPS = AnnotationBuilder.fromClass(this.elements, HTTPS.class);
        this.postInit();
    }

    @Override
    protected void addComputedTypeAnnotations(Tree tree, AnnotatedTypeMirror type, boolean iUseFlow) {
        this.setStringToHTTPS(tree, type);
        super.addComputedTypeAnnotations(tree, type, iUseFlow);
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return this.getBundledTypeQualifiersWithoutPolyAll();
    }

    private void setStringToHTTPS(Tree tree, AnnotatedTypeMirror type){
        if(tree.getKind().equals(Tree.Kind.STRING_LITERAL)){
            if(tree.toString().startsWith("https")){
                QualifierDefaults defaults = new QualifierDefaults(this.elements, this);
                defaults.addCheckedCodeDefault(this.HTTPS, TypeUseLocation.ALL);
                defaults.annotate(tree, type);
            }
        }
    }
}
