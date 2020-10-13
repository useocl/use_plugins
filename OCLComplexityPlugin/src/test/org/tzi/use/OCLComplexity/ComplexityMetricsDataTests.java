package org.tzi.use.OCLComplexity;

import junit.framework.TestCase;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ComplexityMetricsDataTests extends TestCase {
    AbstractMetricsData data;

    final Set<String> METRIC_NAMES = Stream.of(
        "NumberOfOCLKeywords",
        "NumberOfExplicitSelf",
        "NumberOfIfExpressions",
        "NumberOfVariablesDefinedByLet",
        "NumberOfSet",
        "NumberOfOrderedSet",
        "NumberOfBag",
        "NumberOfSequence",
        "NumberOfTuple",
        "NumberOfBooleanOperators",
        "NumberOfComparisonOperators",
        "NumberOfExplicitIterators",
        "NumberOfImplicitIterators",
        "NumberOfAttributesClassifierSelf",
        "NumberOfOperationsClassifierSelf",
        "NumberOfTypeOf",
        "NumberOfPropertiesPostfixed",
        "NumberOfNavigatedRelationships",
        "NumberOfAttributesThroughNavigations",
        "WeightedNumberOfReferredOperationsThroughNavigations",
        "NumberOfNavigatedClasses",
        "NumberOfParametersTypeClass",
        "NumberOfUserDefinedDataTypeAttributes",
        "NumberOfUserDefinedDataTypeOperations",
        "WeightedNumberOfNavigations",
        "DepthOfNavigation",
        "WeightedNumberOfCollectionOperations",
        "OverallWeightedComplexity"
    ).collect(Collectors.toSet());


    public void setUp() {
        this.data = new ComplexityMetricsData();
    }

    public void testGetMetric() {
        assertEquals(data.getMetric("NotSetYet"), 0.0);
    }

    public void testIncMetric() {
        data.incMetric("aMetric");
        assertEquals(data.getMetric("aMetric"), 1.0);

        data.incMetric("aMetric");
        assertEquals(data.getMetric("aMetric"), 2.0);
    }

    public void testIncMetric2() {
        data.incMetric("aMetric", 3.0);
        assertEquals(data.getMetric("aMetric"), 3.0);

        data.incMetric("aMetric");
        assertEquals(data.getMetric("aMetric"), 4.0);
    }

    public void testsetMetric() {
        data.setMetric("aMetric", 3.0);
        assertEquals(data.getMetric("aMetric"), 3.0);

        data.setMetric("aMetric", 2.0);
        assertEquals(data.getMetric("aMetric"), 2.0);
    }

    public void testBuild() {
        Path resourceDirectory = Paths.get("resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        Set<Metric> metrics = data.build();
        metrics.stream().forEach(metric ->METRIC_NAMES.contains(metric.getName()));
        assertEquals(metrics.size(), 28);
    }


}
