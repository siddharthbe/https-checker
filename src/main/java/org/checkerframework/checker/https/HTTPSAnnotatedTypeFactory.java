package org.checkerframework.checker.https;

import com.sun.source.tree.*;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.defaults.QualifierDefaults;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.checker.https.qual.HTTPS;

import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import java.util.Set;

public class HTTPSAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    /** The @HTTPS annotation. */
    private final AnnotationMirror HTTPS;

    public HTTPSAnnotatedTypeFactory(BaseTypeChecker checker){
        super(checker);
        this.HTTPS = AnnotationBuilder.fromClass(this.elements, HTTPS.class);
        this.postInit();
    }

    @Override
    protected void addComputedTypeAnnotations(Tree tree, AnnotatedTypeMirror type, boolean iUserFlow) {
        this.setStringToHTTPS(tree, type);
        this.checkConcatenatedString(tree, type);
        super.addComputedTypeAnnotations(tree, type, iUserFlow);
    }

    /**
     * If tree is a literal tree of kind string literal and if the string starts with "https"
     * then add the HTTPS annotation so that dataflow can refine it
     * @param tree: Given AST
     * @param type: Type of the tree
     */
    private void setStringToHTTPS(Tree tree, AnnotatedTypeMirror type){
        if(tree.getKind() == Tree.Kind.STRING_LITERAL){
            LiteralTree literalTree = (LiteralTree) tree;
            if(literalTree.getValue().toString().startsWith("https")){
                QualifierDefaults defaults = new QualifierDefaults(this.elements, this);
                defaults.addCheckedCodeDefault(this.HTTPS, TypeUseLocation.ALL);
                defaults.annotate(tree, type);
            }
        }
    }

    /**
     * If tree is a binary tree of kind Concatenation and if the left operand has HTTPS annotation
     * then the tree will have HTTPS annotation
     * @param tree Given AST
     * @param type Type of the tree
     */
    private void checkConcatenatedString (Tree tree, AnnotatedTypeMirror type) {
        if (tree.getKind() == Tree.Kind.PLUS){
            BinaryTree binaryTree = (BinaryTree) tree;
            AnnotatedTypeMirror atm = getAnnotatedType(binaryTree.getLeftOperand());
            if(AnnotationUtils.containsSameByClass(atm.getAnnotations(), HTTPS.class)){
                QualifierDefaults defaults = new QualifierDefaults(this.elements, this);
                defaults.addCheckedCodeDefault(this.HTTPS, TypeUseLocation.ALL);
                defaults.annotate(tree, type);
            }
        }
    }
}
