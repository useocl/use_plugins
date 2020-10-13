package org.tzi.use.OCLComplexity;

import org.tzi.use.uml.ocl.expr.VarDeclList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Context data for the visitor.
 * This is used to keep track of the depth of collection operation and the context class.
 * For the depth of navigation some metric information are necessary as well.
 */
public class ComplexityVisitorContext {
    private int depth;
    private String contextClass;
    private VarDeclList contextVars;

    // Calc the depth of the Navigation
    // actually this should be tree but just want to store the depth value
    private int navInit = 0; // this is important for the depth, when a subtree is iterated
    private int navCounter = getNavInit(); // determine the depth of the tree (current branch)
    private int navAll = 0; // navigations occurred
    private boolean navStart = true;
    // -- TRACING --
    // private int NumberOfNavigatedRelationships = 0;
    private Set<String> NumberOfNavigatedRelationships = new HashSet<String>();
    // in out parameter für die Gewichtung
    private Map<String, Integer> WeightedNumberOfReferredOperationsThroughNavigations = new HashMap<>();
    private Set<String> NumberOfNavigatedClasses = new HashSet<String>();
    private Set<String> NumberOfUserDefinedDataTypeAttributes = new HashSet<String>();
    private Set<String> NumberOfUserDefinedDataTypeOperations = new HashSet<String>();
    private AbstractMetricsData metricsData;

    public ComplexityVisitorContext() {
        this.setDepth(1);
    }

    public ComplexityVisitorContext(AbstractMetricsData data) {
        this.setDepth(1);
        metricsData = data;
    }

    public int up() {
        return this.depth--;
    }

    public int down() {
        return depth++;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }


    public String getContextClass() {
        return contextClass;
    }

    public void setContextClass(String contextClass) {
        this.contextClass = contextClass;
    }

    public int getNavInit() {
        return navInit;
    }

    public void setNavInit(int navInit) {
        this.navInit = navInit;
    }

    public int getNavCounter() {
        return navCounter;
    }

    public void setNavCounter(int navCounter) {
        this.navCounter = navCounter;
    }

    public int getNavAll() {
        return navAll;
    }

    public void setNavAll(int navAll) {
        this.navAll = navAll;
    }

    public boolean isNavStart() {
        return navStart;
    }

    public void setNavStart(boolean navStart) {
        this.navStart = navStart;
    }

    public VarDeclList getContextVars() {
        return contextVars;
    }

    public void setContextVars(VarDeclList contextVars) {
        this.contextVars = contextVars;
    }


    public void addNumberOfNavigatedRelationships(String associationname) {
        NumberOfNavigatedRelationships.add(associationname);
        metricsData.setMetric("NumberOfNavigatedRelationships",  Double.valueOf(NumberOfNavigatedRelationships.size()));
    }

    public void addWeightedNumberOfReferredOperationsThroughNavigations(String name, Integer val) {
        WeightedNumberOfReferredOperationsThroughNavigations.put(name, val);
        metricsData.setMetric("WeightedNumberOfReferredOperationsThroughNavigations",
                Double.valueOf(WeightedNumberOfReferredOperationsThroughNavigations.values().stream().mapToInt(Integer::intValue).sum()));
    }

    public void addNumberOfNavigatedClass(String cls) {
        NumberOfNavigatedClasses.add(cls);
        metricsData.setMetric("NumberOfNavigatedClasses",  Double.valueOf(NumberOfNavigatedClasses.size()));
    }

    public Set<String> getNumberOfNavigatedClasses() {
        return NumberOfNavigatedClasses;
    }

    public void addNumberOfUserDefinedDataTypeAttributes(String expr) {
        NumberOfUserDefinedDataTypeAttributes.add(expr);
        metricsData.setMetric("NumberOfUserDefinedDataTypeAttributes",  Double.valueOf(NumberOfUserDefinedDataTypeAttributes.size()));
    }

    public void addNumberOfUserDefinedDataTypeOperations(String expr) {
        NumberOfUserDefinedDataTypeOperations.add(expr);
        metricsData.setMetric("NumberOfUserDefinedDataTypeOperations",  Double.valueOf(NumberOfUserDefinedDataTypeOperations.size()));
    }


}
