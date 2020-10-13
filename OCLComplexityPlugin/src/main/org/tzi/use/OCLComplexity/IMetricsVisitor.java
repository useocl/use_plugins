package org.tzi.use.OCLComplexity;

import org.tzi.use.uml.ocl.expr.ExpressionVisitor;

import java.util.Set;

public interface IMetricsVisitor extends ExpressionVisitor {
    /**
     * Return a set including all metrics.
     * @return
     */
    public Set<Metric> getMetrics();
}
