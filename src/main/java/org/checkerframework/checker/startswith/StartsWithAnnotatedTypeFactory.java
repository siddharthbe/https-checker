package org.checkerframework.checker.startswith;

import com.sun.source.tree.*;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.defaults.QualifierDefaults;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.checker.startswith.qual.*;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;

import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import java.util.*;

// The StartsWith checker ensures that URL strings start with the given strings.

public class StartsWithAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    // The @StartsWithBottom annotation.
    private final AnnotationMirror BOTTOM;

    // The @StartsWithUnknown annotation
    private final AnnotationMirror UNKNOWN;

    public StartsWithAnnotatedTypeFactory(BaseTypeChecker checker){
        super(checker);
        this.BOTTOM = AnnotationBuilder.fromClass(this.elements, StartsWithBottom.class);
        this.UNKNOWN = AnnotationBuilder.fromClass(this.elements, StartsWithUnknown.class);
        this.postInit();
    }

    @Override
    protected void addComputedTypeAnnotations(Tree tree, AnnotatedTypeMirror type, boolean iUserFlow) {
        this.setStringToStartsWith(tree, type);
        this.checkConcatenatedString(tree, type);
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

    /**
     * Creates a @StartsWith annotation whose values are the given strings in a list.
     * @param exprs
     * @return
     */
    public AnnotationMirror createStartsWith(Collection<String> exprs){
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, StartsWith.class);
        String[] exprArray = exprs.toArray(new String[0]);
        builder.setValue("value", exprArray);
        return builder.build();
    }

    /**
     * If tree is a literal tree of kind string literal then add the StartsWith annotation with the string as the
     * only element in the array so that dataflow can refine it
     * @param tree: Given AST
     * @param type: Type of the tree
     */
    private void setStringToStartsWith(Tree tree, AnnotatedTypeMirror type){
        if(tree.getKind() == Tree.Kind.STRING_LITERAL){
            LiteralTree literalTree = (LiteralTree) tree;
            QualifierDefaults defaults = new QualifierDefaults(this.elements, this);
            String s = literalTree.getValue().toString();
            defaults.addCheckedCodeDefault(createStartsWith(Collections.singleton(s)), TypeUseLocation.ALL);
            defaults.annotate(tree, type);
        }
    }

    /**
     * If tree is a binary tree of kind Concatenation and if the left operand has StartsWith annotation
     * then the tree will have StartsWith annotation
     * @param tree Given AST
     * @param type Type of the tree
    */
    private void checkConcatenatedString (Tree tree, AnnotatedTypeMirror type) {
        if (tree.getKind() == Tree.Kind.PLUS){
            BinaryTree binaryTree = (BinaryTree) tree;
            AnnotatedTypeMirror atm = getAnnotatedType(binaryTree.getLeftOperand());
            if(AnnotationUtils.containsSameByClass(atm.getAnnotations(), StartsWith.class)){
                QualifierDefaults defaults = new QualifierDefaults(this.elements, this);
                AnnotationMirror am = AnnotationUtils.getAnnotationByClass(atm.getAnnotations(), StartsWith.class);
                defaults.addCheckedCodeDefault(am, TypeUseLocation.ALL);
                defaults.annotate(tree, type);
            }
        }
    }

    /**Returns a copy of the top annotation for access of the type heirarchy. The method is package-private so that
     * can only be called by users within the package.
     */
    AnnotationMirror getCanonicalTopAnnotation() {
        return this.UNKNOWN;
    }

    /** The qualifier hierarchy for the StartsWith type system. StartsWithUnknown is the topmost type and is the default
     * type. StartsWithBottom is the bottom most type in the heirarchy. Types like StartsWith({"a"}) and
     * StartsWith({"b"}) are distinct and at the same level.
      */
    private final class StartsWithQualifierHierarchy extends MultiGraphQualifierHierarchy{
        public StartsWithQualifierHierarchy(MultiGraphQualifierHierarchy.MultiGraphFactory factory){
            super(factory);
        }

        /** The GLB of two StartsWith annotations is the intersection of the elements in the two arrays, or is the
         * bottom if the two sets don't intersect.
         */
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

        /** The LUB of two StartsWith annotations is the union of the elements in the two arrays.
         */
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
                return checkStartsWith(subArrays, superArrays);
            }
            return false;
        }

        /** Takes two lists of strings one being the subtype and the other being the supertype and returns
         * true if every string in subtype starts with atleast one or more string in the supertype
         */
        private boolean checkStartsWith(List<String> subArrays, List<String> superArrays){
            for (String s1: subArrays){
                boolean stringMatched = false;
                for(String s2: superArrays){
                    if(s1.startsWith(s2)) {
                        stringMatched = true;
                    }
                }
                if (!stringMatched){
                    return false;
                }
            }
            return true;
        }

        @Override
        public AnnotationMirror getTopAnnotation(AnnotationMirror start) {
            return UNKNOWN;
        }
    }
}
