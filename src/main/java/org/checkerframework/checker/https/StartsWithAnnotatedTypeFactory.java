package org.checkerframework.checker.https;

import com.sun.source.tree.*;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.defaults.QualifierDefaults;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.checker.https.qual.*;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;

import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import java.util.*;

public class StartsWithAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    /** The @HTTPS annotation. */

    private final AnnotationMirror BOTTOM;

    private final AnnotationMirror UNKNOWN;

    private final ArrayList<String> ACCEPTED_STRINGS;

    public StartsWithAnnotatedTypeFactory(BaseTypeChecker checker){
        super(checker);
        this.BOTTOM = AnnotationBuilder.fromClass(this.elements, StartsWithBottom.class);
        this.UNKNOWN = AnnotationBuilder.fromClass(this.elements, StartsWithUnknown.class);
        this.ACCEPTED_STRINGS = new ArrayList<String> (Arrays.asList("https", "file", "path"));
        this.postInit();
    }

    @Override
    protected void addComputedTypeAnnotations(Tree tree, AnnotatedTypeMirror type, boolean iUserFlow) {
        this.setStringToHTTPS(tree, type);
        //this.checkConcatenatedString(tree, type);
        super.addComputedTypeAnnotations(tree, type, iUserFlow);
    }

    @Override
    public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
        return new StartsWithQualifierHierarchy(factory);
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
            return new LinkedHashSet<>(
                Arrays.asList(
                        StartsWith.class,
                        StartsWithBottom.class,
                        StartsWithUnknown.class,
                        PolyStartsWith.class));
    }

    public AnnotationMirror createStartsWith(Collection<String> exprs){
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, StartsWith.class);
        String[] exprArray = exprs.toArray(new String[0]);
        builder.setValue("value", exprArray);
        return builder.build();
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
            for(String s: this.ACCEPTED_STRINGS) {
                System.out.println(literalTree.getValue().toString().startsWith(s));
                if (literalTree.getValue().toString().startsWith(s)) {
                    QualifierDefaults defaults = new QualifierDefaults(this.elements, this);
                    defaults.addCheckedCodeDefault(createStartsWith(this.ACCEPTED_STRINGS), TypeUseLocation.ALL);
                    defaults.annotate(tree, type);
                }
            }
        }
        System.out.println(type);
    }

    /**
     * If tree is a binary tree of kind Concatenation and if the left operand has HTTPS annotation
     * then the tree will have HTTPS annotation
     * @param tree Given AST
     * @param type Type of the tree
     */
    /**private void checkConcatenatedString (Tree tree, AnnotatedTypeMirror type) {
        if (tree.getKind() == Tree.Kind.PLUS){
            BinaryTree binaryTree = (BinaryTree) tree;
            AnnotatedTypeMirror atm = getAnnotatedType(binaryTree.getLeftOperand());
            if(AnnotationUtils.containsSameByClass(atm.getAnnotations(), HTTPS.class)){
                QualifierDefaults defaults = new QualifierDefaults(this.elements, this);
                defaults.addCheckedCodeDefault(this.STARTS_WITH, TypeUseLocation.ALL);
                defaults.annotate(tree, type);
            }
        }
    }*/

    private final class StartsWithQualifierHierarchy extends MultiGraphQualifierHierarchy{
        public StartsWithQualifierHierarchy(MultiGraphQualifierHierarchy.MultiGraphFactory factory){
            super(factory);
        }

        @Override
        public AnnotationMirror greatestLowerBound(AnnotationMirror a1, AnnotationMirror a2){
            if(AnnotationUtils.hasElementValue(a1, "value") && AnnotationUtils.hasElementValue(a2, "value")) {
                List<String> a1Val = AnnotationUtils.getElementValueArray(a1, "value", String.class, true);
                List<String> a2Val = AnnotationUtils.getElementValueArray(a2, "value", String.class, true);
                if(Collections.disjoint(a1Val, a2Val)) {
                    return BOTTOM;
                }else{
                    a1Val.retainAll(a2Val);
                    return createStartsWith(a1Val);
                }
            }else{
                if(AnnotationUtils.areSameByClass(a1, StartsWithUnknown.class)){
                    return a2;
                }else if(AnnotationUtils.areSameByClass(a2, StartsWithUnknown.class)){
                    return a1;
                }else{
                    return BOTTOM;
                }
            }
        }

        @Override
        public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
            if (AnnotationUtils.hasElementValue(a1, "value") && AnnotationUtils.hasElementValue(a2, "value")) {
                List<String> a1Val = AnnotationUtils.getElementValueArray(a1, "value", String.class, true);
                List<String> a2Val = AnnotationUtils.getElementValueArray(a2, "value", String.class, true);
                Set<String> result = new TreeSet<>(a1Val);
                for(String s : a2Val) {
                    result.add(s);
                }
                return createStartsWith(result);
            } else {
                if (AnnotationUtils.areSameByClass(a1, StartsWithBottom.class)) {
                    return a2;
                } else if (AnnotationUtils.areSameByClass(a2, StartsWithBottom.class)) {
                    return a1;
                } else if (AnnotationUtils.areSameByClass(a1, PolyStartsWith.class)
                        && AnnotationUtils.areSameByClass(a2, PolyStartsWith.class)) {
                    return a1;
                } else {
                    return UNKNOWN;
                }
            }
        }

        @Override
        public boolean isSubtype(AnnotationMirror subAnno, AnnotationMirror superAnno) {
            if (AnnotationUtils.areSameByClass(subAnno, StartsWithBottom.class)) {
                return true;
            } else if (AnnotationUtils.areSameByClass(superAnno, StartsWithUnknown.class)) {
                return true;
            } else if (AnnotationUtils.areSameByClass(subAnno, PolyStartsWith.class)) {
                return AnnotationUtils.areSameByClass(superAnno, PolyStartsWith.class);
            } else if (AnnotationUtils.hasElementValue(subAnno, "value")
                    && AnnotationUtils.hasElementValue(superAnno, "value")) {
                List<String> subArrays = AnnotationUtils.getElementValueArray(subAnno, "value", String.class, true);
                List<String> superArrays = AnnotationUtils.getElementValueArray(superAnno, "value", String.class, true);;
                if (superArrays.containsAll(subArrays)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public AnnotationMirror getTopAnnotation(AnnotationMirror start) {
            return UNKNOWN;
        }
    }
}
