package org.checkerframework.checker.startswith;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.TreeUtils;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.cfg.node.ConditionalAndNode;
import org.checkerframework.dataflow.cfg.node.MethodAccessNode;
import org.checkerframework.dataflow.analysis.ConditionalTransferResult;
import org.checkerframework.dataflow.analysis.RegularTransferResult;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.analysis.FlowExpressions;
import org.checkerframework.dataflow.analysis.FlowExpressions.Receiver;
import org.checkerframework.checker.startswith.qual.StartsWithUnknown;
import com.sun.source.tree.Tree;


public class StartsWithTransfer extends CFTransfer{

    /**The {@code java.lang.String#startsWith()} method.*/
    private final ExecutableElement stringStartsWith;

    private StartsWithAnnotatedTypeFactory aTypeFactory;

    public StartsWithTransfer(CFAnalysis analysis) {
        super(analysis);
        this.aTypeFactory = (StartsWithAnnotatedTypeFactory) analysis.getTypeFactory();
        this.stringStartsWith = TreeUtils.getMethod("java.lang.String", "startsWith", 1,
                                                                        aTypeFactory.getProcessingEnv());
    }

    /**For all then branches which have a startsWith condition it annotates the receiver with the same annotation as
     * the argument of startsWith.
     */
    @Override
    protected void processConditionalPostconditions(MethodInvocationNode node, ExecutableElement methodElement,
                                                    Tree tree, CFStore thenStore, CFStore elseStore) {
        Node receiver = node.getTarget().getReceiver();
        Receiver receiverReceiver = FlowExpressions.internalReprOf(aTypeFactory, receiver);
        if (methodElement.equals(stringStartsWith)) {
            AnnotatedTypeMirror atm = aTypeFactory.getAnnotatedType(node.getArgument(0).getTree());
            thenStore.insertValue(receiverReceiver,
                                  atm.getAnnotationInHierarchy(aTypeFactory.getCanonicalTopAnnotation()));
        }
    }

    /**For all then branches which have a (startsWith && startsWith) condition with the same receivers it annotates the
     * receiver with the GLB of the annotations of the arguments of startsWith
     */
    @Override
    public TransferResult<CFValue, CFStore> visitConditionalAnd(ConditionalAndNode node,
                                                                TransferInput<CFValue, CFStore> input){
        TransferResult<CFValue, CFStore> result = super.visitConditionalAnd(node, input);
        Node leftOperand = node.getLeftOperand();
        Node rightOperand = node.getRightOperand();
        if(leftOperand.getTree().getKind() == Tree.Kind.METHOD_INVOCATION &&
                rightOperand.getTree().getKind() == Tree.Kind.METHOD_INVOCATION){
            MethodInvocationNode leftNode = (MethodInvocationNode) leftOperand;
            MethodInvocationNode rightNode = (MethodInvocationNode) rightOperand;
            MethodAccessNode leftTarget = leftNode.getTarget();
            MethodAccessNode rightTarget = rightNode.getTarget();
            Node leftReceiver = leftTarget.getReceiver();
            if(leftReceiver.equals(rightTarget.getReceiver())) {
                ExecutableElement methodElementLeft = leftTarget.getMethod();
                ExecutableElement methodElementRight = rightTarget.getMethod();
                if (methodElementLeft.equals(stringStartsWith) && methodElementRight.equals(stringStartsWith)) {
                    Receiver receiverReceiver = FlowExpressions.internalReprOf(aTypeFactory, leftReceiver);
                    AnnotatedTypeMirror atmLeft = aTypeFactory.getAnnotatedType(leftNode.getArgument(0).getTree());
                    AnnotatedTypeMirror atmRight = aTypeFactory.getAnnotatedType(rightNode.getArgument(0).getTree());
                    AnnotationMirror canonicalTopAnnotation = aTypeFactory.getCanonicalTopAnnotation();
                    AnnotationMirror finalType = aTypeFactory.getQualifierHierarchy().greatestLowerBound(
                            atmLeft.getAnnotationInHierarchy(canonicalTopAnnotation),
                            atmRight.getAnnotationInHierarchy(canonicalTopAnnotation));
                    result.getThenStore().insertValue(receiverReceiver, finalType);
                }
            }
        }
        return result;
    }
}