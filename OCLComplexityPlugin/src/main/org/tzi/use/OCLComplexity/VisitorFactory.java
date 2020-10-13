package org.tzi.use.OCLComplexity;

import org.tzi.use.uml.ocl.expr.VarDeclList;

/**
 * Factory for creating a metric visitor.
 */
public class VisitorFactory {
    private String visitor;
    public VisitorFactory(String visitor) {
        this.visitor = visitor;
    }

    public IMetricsVisitor createVisitor(boolean expandExpresion) {
        if(visitor.equalsIgnoreCase("COMPLEXITY")) {
            return new ComplexityCalculationVisitor(expandExpresion);
        }
        return null;
    }

    public IMetricsVisitor createVisitor(boolean expandExpresion, String context) {
        return createVisitor(expandExpresion, context, null);
    }


    public IMetricsVisitor createVisitor(boolean expandExpresion, String context, VarDeclList varDecls) {
        if(visitor.equalsIgnoreCase("COMPLEXITY")) {
            return new ComplexityCalculationVisitor(expandExpresion, context, varDecls);
        }
        return null;
    }
}
