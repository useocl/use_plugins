package org.tzi.use.OCLComplexity;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.*;
import org.tzi.use.uml.ocl.type.TupleType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.util.Log;
import org.tzi.use.util.StringUtil;

import java.util.Set;
import java.util.Stack;

/**
 * Visitor for calculating a ocl complexity metric
 */
public class ComplexityCalculationVisitor implements IMetricsVisitor {

    // container for the metric
    private final AbstractMetricsData complexityMetricsData;

    // store some context data to calculate the metric
    private final ComplexityVisitorContext contextData;

    private final boolean expandOperations;

    private final OverallWeightedComplexityMetric expressionComplexity;
    private Stack<MOperation> operationStack = new Stack<MOperation>();

    // some debug data for pretty printing
    private int debugDepth = 0;

    public ComplexityCalculationVisitor(boolean expandOperations) {
        this(expandOperations, "", null);
    }

    public ComplexityCalculationVisitor(boolean expandOperations, String contextClass) {
        this(expandOperations, contextClass, null);
    }

    public ComplexityCalculationVisitor(boolean expandOperations, String contextClass, VarDeclList varDeclList) {
        this.expandOperations = expandOperations;
        this.expressionComplexity = new OverallWeightedComplexityMetric();
        this.complexityMetricsData = new ComplexityMetricsData();
        this.contextData = new ComplexityVisitorContext(complexityMetricsData);
        contextData.setContextClass(contextClass);

        if (varDeclList == null) {
            varDeclList = new VarDeclList(true);
        }
        contextData.setContextVars(varDeclList);
    }

    public OverallWeightedComplexityMetric getExpressionComplexity() {
        return expressionComplexity;
    }

    /**
     * Calls classOrNameEquals for each string in array.
     *
     * @param expr     Expresion to check
     * @param strArray array to check
     * @return <code>true</code> if one call of classOrNameEquals is true
     */
    private boolean classOrNameEquals(Expression expr, String[] strArray) {
        for (String s : strArray) {
            if (classOrNameEquals(expr, s)) return true;
        }
        return false;
    }

    /**
     * Compare the expression name to string. Return <code>true</code> if equal.
     * If the expression is a ExpStdOp we maybe want to check for the type of Operation.
     *
     * @param expr Expresion to check
     * @param str  class or name
     * @return true if str equals to operation class or to the expression name
     */
    private boolean classOrNameEquals(Expression expr, String str) {
        String name = expr.getClass().getSimpleName();
        if (name.equals("ExpStdOp")) {
            name = ((ExpStdOp) expr).getOperation().getClass().getSimpleName();
        }
        return name.equals(str) || expr.name().equals(str);
    }

    /**
     * Get weight of the expression category and update the OverallWeightedCategories metric
     * @param expr
     */
    private void updateOverallWeightedComplexity(Expression expr) {
        float weight = expressionComplexity.getWeight(expr);
        int depth = contextData.getDepth();

        Log.debug("OWC: W" + weight +" D"+ depth + " + " + expr.toString());
        complexityMetricsData.incMetric("OverallWeightedComplexity", Double.valueOf(weight * depth));
    }

    public ComplexityVisitorContext getContextData() {
        return contextData;
    }

    public AbstractMetricsData getMetricsData() {
        return complexityMetricsData;
    }

    @Override
    public void visitAllInstances(ExpAllInstances exp) {
        debugLog(exp);
        exp.getSourceType();
        updateOverallWeightedComplexity(exp);
    }

    @Override
    public void visitAny(ExpAny exp) {
        visitQuery(exp);
    }

    @Override
    public void visitAsType(ExpAsType exp) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);
        // Needed?
        debugDepth++;
        exp.getSourceExpr().processWithVisitor(this);
        debugDepth--;
        complexityMetricsData.incMetric("NumberOfTypeOf");
    }

    @Override
    public void visitAttrOp(ExpAttrOp exp) {
        debugLog(exp.attr().toString());
        debugDepth++;
        exp.objExp().processWithVisitor(this);
        debugDepth--;
        updateOverallWeightedComplexity(exp);

        if (exp.objExp() instanceof  ExpVariable) {
            if (contextData.getContextVars().containsName(((ExpVariable) exp.objExp()).getVarname())) {
                complexityMetricsData.incMetric("NumberOfAttributesClassifierSelf");
            } else if(contextData.getNumberOfNavigatedClasses().contains(exp.attr().owner().shortName()) && contextData.getNavInit() > 0) {
                // navigating a collection will produces a collection operation.
                // this one might fail! Check if owener class was navigated + current navigating + variable is not in context vars
                complexityMetricsData.incMetric("NumberOfAttributesThroughNavigations");
            }
        }
        if(exp.objExp() instanceof  ExpNavigation){
            // obj is navigation
            complexityMetricsData.incMetric("NumberOfAttributesThroughNavigations");
        }

        if (exp.isPre()) {
            complexityMetricsData.incMetric("NumberOfPropertiesPostfixed");
        }


        Type owner = exp.attr().owner();
        if (owner.isTypeOfClass()) {
            // Datatypes have no navigations
            if (((MClass) owner).associations().size() == 0) {
                contextData.addNumberOfUserDefinedDataTypeAttributes(((MClass) owner).name() + '.' + exp.attr().name());
            }
        }
    }

    @Override
    public void visitBagLiteral(ExpBagLiteral exp) {
        debugLog(exp);
        visitCollectionLiteral(exp);
        complexityMetricsData.incMetric("NumberOfBag");
    }

    @Override
    public void visitCollect(ExpCollect exp) {
        visitQuery(exp);
    }

    @Override
    public void visitCollectNested(ExpCollectNested exp) {
        visitQuery(exp);
    }

    @Override
    public void visitConstBoolean(ExpConstBoolean exp) {
        debugLog(exp);
        complexityMetricsData.incMetric("NumberOfOCLKeywords");
    }

    @Override
    public void visitConstEnum(ExpConstEnum exp) {
        debugLog(exp);
    }

    @Override
    public void visitConstInteger(ExpConstInteger exp) {
        debugLog(exp);
    }

    @Override
    public void visitConstReal(ExpConstReal exp) {
        debugLog(exp);
    }

    @Override
    public void visitConstString(ExpConstString exp) {
        debugLog(exp);
    }

    @Override
    public void visitEmptyCollection(ExpEmptyCollection exp) {
        debugLog(exp);
    }

    @Override
    public void visitExists(ExpExists exp) {
        visitQuery(exp);
    }

    @Override
    public void visitForAll(ExpForAll exp) {
        visitQuery(exp);
    }

    @Override
    public void visitIf(ExpIf exp) {
        debugLog(exp);
        debugDepth++;
        exp.getCondition().processWithVisitor(this);
        debugDepth--;
        updateOverallWeightedComplexity(exp);

        // next depth level
        contextData.down();
        debugDepth++;
        exp.getThenExpression().processWithVisitor(this);
        exp.getElseExpression().processWithVisitor(this);
        debugDepth--;
        contextData.up();

        complexityMetricsData.incMetric("NumberOfOCLKeywords");
        complexityMetricsData.incMetric("NumberOfIfExpressions");
    }

    @Override
    public void visitIsKindOf(ExpIsKindOf exp) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);
        debugDepth++;
        exp.getSourceExpr().processWithVisitor(this);
        debugDepth--;
        complexityMetricsData.incMetric("NumberOfTypeOf");
    }

    @Override
    public void visitIsTypeOf(ExpIsTypeOf exp) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);
        debugDepth++;
        exp.getSourceExpr().processWithVisitor(this);
        debugDepth--;
        complexityMetricsData.incMetric("NumberOfTypeOf");
        debugLog(exp);
    }

    @Override
    public void visitIsUnique(ExpIsUnique exp) {
        visitQuery(exp);
    }

    @Override
    public void visitIterate(ExpIterate exp) {
        visitQuery(exp, exp.getAccuInitializer());
    }

    @Override
    public void visitLet(ExpLet exp) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);
        // next depth level
        contextData.down();
        debugDepth++;
        exp.getVarExpression().processWithVisitor(this);
        exp.getInExpression().processWithVisitor(this);
        debugDepth--;
        contextData.up();

        complexityMetricsData.incMetric("NumberOfOCLKeywords");
        complexityMetricsData.incMetric("NumberOfVariablesDefinedByLet");
    }

    @Override
    public void visitNavigation(ExpNavigation exp) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);
        // navigation depth
        if (contextData.isNavStart()) {
            // new navigation
            contextData.setNavCounter(contextData.getNavInit());
            contextData.setNavStart(false);
        }
        contextData.setNavCounter(contextData.getNavCounter() + 1);
        contextData.setNavAll(contextData.getNavAll() + 1);

        if(!classOrNameEquals(exp.getObjectExpression(), new String[]{ "ExpNavigation", "ExpNavigationClassifierSource"})) {
            // new max length ?
            if (contextData.getNavCounter() > complexityMetricsData.getMetric("DepthOfNavigation")) {
                complexityMetricsData.setMetric("DepthOfNavigation" , Double.valueOf(contextData.getNavCounter()));
            }
            // next nav is a new subtree
            contextData.setNavStart(true);
        }
        // end navigation depth
        debugDepth++;
        exp.getObjectExpression().processWithVisitor(this);
        for (Expression e : exp.getQualifierExpression()) {
            e.processWithVisitor(this);
        }
        debugDepth--;

        contextData.addNumberOfNavigatedRelationships(exp.getDestination().association().shortName());
        contextData.addNumberOfNavigatedClass(exp.getDestination().cls().shortName());
        complexityMetricsData.incMetric("WeightedNumberOfNavigations", Double.valueOf(contextData.getDepth()));
    }

    @Override
    public void visitNavigationClassifierSource(
            ExpNavigationClassifierSource exp) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);
        debugDepth++;
        exp.getObjectExpression().processWithVisitor(this);
        debugDepth--;
        contextData.addNumberOfNavigatedRelationships(exp.getDestination().association().shortName());
        contextData.addNumberOfNavigatedClass(exp.getDestination().cls().shortName());
    }

    @Override
    public void visitObjAsSet(ExpObjAsSet exp) {
        debugLog(exp);
        debugDepth++;
        exp.getObjectExpression().processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public void visitObjOp(ExpObjOp exp) {
        debugLog(exp);
        debugDepth++;
        for (Expression ex : exp.getArguments()) {
            ex.processWithVisitor(this);
        }
        debugDepth--;
        updateOverallWeightedComplexity(exp);

        MOperation opt = exp.getOperation();

        Type resultType = opt.resultType();
        if (expandOperations && opt.hasExpression()
                && !operationStack.contains(opt)) {
            operationStack.push(opt);
            debugDepth++;
            opt.expression().processWithVisitor(this);
            debugDepth--;
            operationStack.pop();
        }

        if(exp.getArguments()[0] instanceof  ExpVariable && contextData.getContextVars().containsName(((ExpVariable) exp.getArguments()[0]).getVarname())) {
            complexityMetricsData.incMetric("NumberOfOperationsClassifierSelf");
        }


        if (exp.getArguments()[0] instanceof  ExpNavigation ||
                (exp.getArguments()[0] instanceof  ExpVariable && contextData.getNumberOfNavigatedClasses().contains(opt.cls().shortName()) &&
                        contextData.getNavInit() > 0 && !contextData.getContextVars().containsName(((ExpVariable) exp.getArguments()[0]).getVarname()))) {
        //if (!opt.cls().shortName().equals(contextData.getContextClass())) {
            // navigated operation
            int num_params = opt.paramList().size();
            int pout = 0;

            // FIXME: dies ist noch nicht korrekt... Wie werden out parameter deklariert
            if (resultType.isTypeOfTupleType()) {
                pout = ((TupleType) resultType).getParts().size() - 1;
            }
            String name = opt.qualifiedName();
            // (1 + |Par(m)|)(1 + Result + |Pout, in/out(m)|)
            int result = (1 + num_params) * (1 + 1 + pout);
            contextData.addWeightedNumberOfReferredOperationsThroughNavigations(name, result);
        }

        MClass cls;
        for (VarDecl var : opt.paramList()) {
            if (var.type().isTypeOfClass()) {
                cls = (MClass) var.type();
                if (cls != null) {
                    complexityMetricsData.incMetric("NumberOfParametersTypeClass");
                }
            }
        }
        if (resultType.isTypeOfClass()) {
            cls = (MClass) resultType;
            if (cls.model() != null) {
                complexityMetricsData.incMetric("NumberOfParametersTypeClass");
            }
        }
        MClass owner = opt.cls();
        // Datatypes have no navigations
        if (owner.associations().size() == 0) {
            contextData.addNumberOfUserDefinedDataTypeOperations(owner.name() + '.' + opt.name());
        }
    }

    @Override
    public void visitObjRef(ExpObjRef exp) {
        debugLog(exp);
        debugDepth++;
        exp.processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public void visitOne(ExpOne exp) {
        visitQuery(exp);
    }

    @Override
    public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
        debugLog(exp);
        visitCollectionLiteral(exp);
        complexityMetricsData.incMetric("NumberOfOrderedSet");
    }

    public void visitQuery(ExpQuery exp, VarInitializer accuInitializer) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);
        // navigation depth
        int navBefore = contextData.getNavAll();
        debugDepth++;
        // call the left side and check if there is some navigation
        exp.getRangeExpression().processWithVisitor(this);
        debugDepth--;
        int navDiff = contextData.getNavAll() - navBefore;
        // if there is a navigation "tree" and a collection operation, we will add a "subtree".
        boolean premise = navDiff > 0 && expressionComplexity.getType(exp) == OverallWeightedComplexityMetric.ComplexityCategory.COLLECTION;
        if (premise) {
            // collection operation: connection to subtree counting 2
            contextData.setNavInit(contextData.getNavInit() + navDiff + 2);
        }
        // end navigation depth

        if (OverallWeightedComplexityMetric.ComplexityCategory.COLLECTION == this.expressionComplexity.getType(exp)) {
            complexityMetricsData.incMetric("WeightedNumberOfCollectionOperations", Double.valueOf(contextData.getDepth()));
        }

        contextData.down();
        debugDepth++;
        exp.getVariableDeclarations().processWithVisitor(this);
        if (accuInitializer != null) {
            accuInitializer.getVarDecl().processWithVisitor(this);
            accuInitializer.initExpr().processWithVisitor(this);
        }
        exp.getQueryExpression().processWithVisitor(this);
        debugDepth--;
        contextData.up();

        // navigation depth
        // reset init value
        if (premise) {
            contextData.setNavInit(contextData.getNavInit() - navDiff - 2);
        }
    }

    @Override
    public void visitQuery(ExpQuery exp) {
        visitQuery(exp, null);
    }

    @Override
    public void visitReject(ExpReject exp) {
        visitQuery(exp);
    }

    @Override
    public void visitWithValue(ExpressionWithValue exp) {
        debugLog(exp);
    }

    @Override
    public void visitSelect(ExpSelect exp) {
        visitQuery(exp);
    }

    @Override
    public void visitSequenceLiteral(ExpSequenceLiteral exp) {
        debugLog(exp);
        visitCollectionLiteral(exp);
        complexityMetricsData.incMetric("NumberOfSequence");
    }

    @Override
    public void visitSetLiteral(ExpSetLiteral exp) {
        debugLog(exp);
        visitCollectionLiteral(exp);
        complexityMetricsData.incMetric("NumberOfSet");
    }

    @Override
    public void visitSortedBy(ExpSortedBy exp) {
        visitQuery(exp);
    }

    @Override
    public void visitStdOp(ExpStdOp exp) {
        debugLog(exp);
        updateOverallWeightedComplexity(exp);

        if (exp.getOperation().isBooleanOperation()) {
            complexityMetricsData.incMetric("NumberOfOCLKeywords");
            complexityMetricsData.incMetric("NumberOfBooleanOperators");
        }

        if (classOrNameEquals(exp, new String[]{"<", ">", "<=", ">=", "<>", "="})) {
            complexityMetricsData.incMetric("NumberOfComparisonOperators");
        }
        debugDepth++;
        for (Expression expArg : exp.args()) {
            expArg.processWithVisitor(this);
        }
        debugDepth--;
    }

    @Override
    public void visitTupleLiteral(ExpTupleLiteral exp) {
        debugLog(exp);
        debugDepth++;
        for (ExpTupleLiteral.Part part : exp.getParts()) {
            part.getExpression().processWithVisitor(this);
        }
        debugDepth--;
        complexityMetricsData.incMetric("NumberOfTuple");
    }

    @Override
    public void visitTupleSelectOp(ExpTupleSelectOp exp) {
        debugLog(exp);
        debugDepth++;
        exp.getTupleExp().processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public void visitUndefined(ExpUndefined exp) {
        complexityMetricsData.incMetric("NumberOfOCLKeywords");
        debugLog(exp);

    }

    @Override
    public void visitVariable(ExpVariable exp) {
        debugLog(exp.toString());
        if (exp.getVarname().equals("self") && exp.type().shortName().equals(contextData.getContextClass())) {
            complexityMetricsData.incMetric("NumberOfExplicitSelf");
        } else if (exp.getVarname().startsWith("$elem")) {
            complexityMetricsData.incMetric("NumberOfImplicitIterators");
        } else {
            complexityMetricsData.incMetric("NumberOfExplicitIterators");
        }
    }

    protected void visitCollectionLiteral(ExpCollectionLiteral exp) {
        debugLog(exp);
        debugDepth++;
        for (Expression ex : exp.getElemExpr()) {
            ex.processWithVisitor(this);
        }
        debugDepth--;
    }

    @Override
    public void visitClosure(ExpClosure expClosure) {
        visitQuery(expClosure);
    }

    @Override
    public void visitOclInState(ExpOclInState expOclInState) {
        debugLog(expOclInState);
        debugDepth++;
        expOclInState.getSourceExpr().processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public void visitVarDeclList(VarDeclList varDeclList) {
        debugLog(varDeclList.toString());
        debugDepth++;
        for (int i = 0; i < varDeclList.size(); ++i) {
            varDeclList.varDecl(i).processWithVisitor(this);
        }
        debugDepth--;
    }

    @Override
    public void visitVarDecl(VarDecl varDecl) {
        debugLog(varDecl.toString());
        if (varDecl.name().equals("self") && varDecl.type().shortName().equals(contextData.getContextClass())) {
            // declaration is not used
            // complexityMetricsData.incMetric("NumberOfExplicitSelf");
        } else if (varDecl.name().startsWith("$elem")) {
            // declaration is not used
            // complexityMetricsData.incMetric("NumberOfImplicitIterator");
        } else {
            // declaration is not used
            // complexityMetricsData.incMetric("NumberOfExplicitIterator");
        }
    }

    @Override
    public void visitObjectByUseId(ExpObjectByUseId expObjectByUseId) {
        debugLog(expObjectByUseId);
        debugDepth++;
        expObjectByUseId.getIdExpression().processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public void visitConstUnlimitedNatural(
            ExpConstUnlimitedNatural expressionConstUnlimitedNatural) {
        debugLog(expressionConstUnlimitedNatural);

    }

    @Override
    public void visitSelectByKind(ExpSelectByKind expSelectByKind) {
        debugLog(expSelectByKind);
        debugDepth++;
        expSelectByKind.getSourceExpression().processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public void visitExpSelectByType(ExpSelectByType expSelectByType) {
        debugLog(expSelectByType);
        debugDepth++;
        expSelectByType.getSourceExpression().processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public void visitRange(ExpRange exp) {
        debugLog(exp);
        debugDepth++;
        exp.getStart().processWithVisitor(this);
        exp.getEnd().processWithVisitor(this);
        debugDepth--;
    }

    @Override
    public Set<Metric> getMetrics() {
        return complexityMetricsData.build();
    }


    private void debugLog(Expression exp) {
        debugLog(exp.name());
    }

    private void debugLog(String text) {
        String placeholder = StringUtil.repeat("    ", debugDepth);
        if(debugDepth > 0) {
            placeholder += "+-- ";
        }

        Log.debug(placeholder + text);

    }
}
