package org.tzi.use.OCLComplexity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMetricsData {
    private Map<String, Double> metricMap = new HashMap<>();


    /**
     * Get the metric value.
     * @param name
     */
    public Double getMetric(String name) {
        if(metricMap.containsKey(name)) {
            return metricMap.get(name);
        }
        return 0.0;
    }

    /**
     * Set the metric value.
     * @param name
     * @param value
     */
    public void setMetric(String name, Double value) {
        metricMap.put(name, value);
    }

    /**
     * Increment the metric entry by one.
     * @param name
     */
    public void incMetric(String name) {
        incMetric(name, 1.0);
    }

    /**
     * Increment the metric by value.
     * @param name
     * @param value
     */
    public void incMetric(String name, Double value) {
        if(!metricMap.containsKey(name)) {
            metricMap.put(name, value);
        } else {
            metricMap.put(name, metricMap.get(name) + value);
        }
    }

    /**
     * Construct a set of metrics.
     * @return
     */
    abstract public Set<Metric> build();

}
