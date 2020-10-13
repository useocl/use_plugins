package org.tzi.use.OCLComplexity;

import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.Expression;

import java.util.HashMap;
import java.util.Map;

class OverallWeightedComplexityMetric {
    private Map<String, ComplexityCategory> map = new HashMap<>();

    public OverallWeightedComplexityMetric() {
        // map expression name or class to category
        map.put("allInstances", ComplexityCategory.COLLECTION);
        map.put("any", ComplexityCategory.COLLECTION);
        map.put("closure", ComplexityCategory.COLLECTION);
        map.put("collect", ComplexityCategory.COLLECTION);
        map.put("collectNested", ComplexityCategory.COLLECTION);
        map.put("excludes", ComplexityCategory.COLLECTION);
        map.put("excludesAll", ComplexityCategory.COLLECTION);
        map.put("exists", ComplexityCategory.COLLECTION);
        map.put("forAll", ComplexityCategory.COLLECTION);
        map.put("includes", ComplexityCategory.COLLECTION);
        map.put("includesAll", ComplexityCategory.COLLECTION);
        map.put("iterate", ComplexityCategory.COLLECTION);
        map.put("isUnique", ComplexityCategory.COLLECTION);
        map.put("one", ComplexityCategory.COLLECTION);
        map.put("reject", ComplexityCategory.COLLECTION);
        map.put("select", ComplexityCategory.COLLECTION);
        map.put("sortedBy", ComplexityCategory.COLLECTION);

        // TODO Check the names
        map.put("ExpIf", ComplexityCategory.IF);

        map.put("ExpNavigation", ComplexityCategory.NAVIGATION);
        map.put("ExpNavigationClassifierSource", ComplexityCategory.NAVIGATION);

        map.put("ExpAttrOp", ComplexityCategory.ATTRIBUTE);

        map.put("<", ComplexityCategory.COMPARISONOPERATIONS);
        map.put("<=", ComplexityCategory.COMPARISONOPERATIONS);
        map.put(">", ComplexityCategory.COMPARISONOPERATIONS);
        map.put(">=", ComplexityCategory.COMPARISONOPERATIONS);
        map.put("=", ComplexityCategory.COMPARISONOPERATIONS);
        map.put("<>", ComplexityCategory.COMPARISONOPERATIONS);

        map.put("and", ComplexityCategory.BOOLEANOPERATIONS);
        map.put("or", ComplexityCategory.BOOLEANOPERATIONS);
        map.put("xor", ComplexityCategory.BOOLEANOPERATIONS);
        map.put("not", ComplexityCategory.BOOLEANOPERATIONS);
        map.put("implies", ComplexityCategory.BOOLEANOPERATIONS);


        map.put("Op_number_unaryminus", ComplexityCategory.UNARYOPERATIONS);
        map.put("Op_number_unaryplus", ComplexityCategory.UNARYOPERATIONS);
    }

    /**
     * Return the weight of the expression
     * @param expr
     * @return
     */
    public float getWeight(Expression expr) {
        return getType(expr).weight;
    }

    /**
     * Return the category of the expression
     * @param expr
     * @return
     */
    public ComplexityCategory getType(Expression expr) {
        // check the more specific classname first
        String name = expr.getClass().getSimpleName();
        if (name.equals("ExpStdOp")) {
            name = ((ExpStdOp) expr).getOperation().getClass().getSimpleName();
        }
        // then check the name. This can be useful for different types.
        // E.g. the Op-Name >= is used for number and Strings.
        if (!map.containsKey(name)) {
            name = expr.name();
        }
        return map.getOrDefault(name, ComplexityCategory.NOTSPECIFIED);
    }

    /**
     * Assign ocl elements to categories. Considering the complexity each category can have different weight.
     */
    public enum ComplexityCategory {
        ATTRIBUTE(1f),
        BOOLEANOPERATIONS(1f),
        COLLECTION(2f),
        COMPARISONOPERATIONS(1f),
        IF(1f),
        NAVIGATION(1f),
        NOTSPECIFIED(1f),
        UNARYOPERATIONS(0f);

        public float weight;

        private ComplexityCategory(float weight) {
            this.weight = weight;
        }
    }
}
