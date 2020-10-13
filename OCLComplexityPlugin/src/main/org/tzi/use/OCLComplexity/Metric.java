package org.tzi.use.OCLComplexity;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a single metric entry.
 */
public class Metric {
    private String name;
    private String description;
    private String token;
    private double value;

    public Metric(String name, String description, double value, String token) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.token = token;
    }

    public Metric(Metric metric) {
        name = metric.getName();
        description = metric.getDescription();
        value = metric.getValue();
        token = metric.getToken();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Metric)) return false;
        Metric metric = (Metric) o;
        // FIXME: Maybe we do not want to the check for the value etc. A Metric is identified by it's name and token?
        return /*Double.compare(metric.getValue(), getValue()) == 0 &&*/
                Objects.equals(getName(), metric.getName()) &&
                        Objects.equals(getDescription(), metric.getDescription()) &&
                        Objects.equals(getToken(), metric.getToken());
    }

    @Override
    public int hashCode() {
        // return Objects.hash(getName(), getDescription(), getToken(), getValue());
        return Objects.hash(getName(), getDescription(), getToken());
    }

    @Override
    public String toString() {
        String token = "";
        if (!this.token.equals("")) {
            token = "(" + this.token + ")";
        }
        return name + token + ": " + value;
    }

    /**
     * Combine two set of metrics. The result metric is sum of the metric values.
     * @param first
     * @param second
     * @return
     */
    public static Set<Metric> combine(Set<Metric> first, Set<Metric> second) {
        // sum up alle values
        for (Metric metric : second) {
            Metric resMetric = first.stream().filter(m -> m.equals(metric)).findFirst().orElse(null);
            // if there is no equivalent metric in the first set, a new metric is added
            if (resMetric == null) {
                first.add(new Metric(metric));
            } else {
                resMetric.setValue(resMetric.getValue() + metric.getValue());
            }
        }
        return first;
    }

    /**
     * Helper function for finding the first metric with the specific name.
     * @param set
     * @param name
     * @return
     */
    public static Metric getFirstByName(Set<Metric> set, String name) {
        return set.stream().filter(metric -> metric.name.equals(name)).findFirst().get();
    }

    /**
     * Helper function for finding the first metric with the specific token.
     * @param set
     * @param token
     * @return
     */
    public static Metric getFirstByToken(Set<Metric> set, String token) {
        return set.stream().filter(metric -> metric.token.equals(token)).findFirst().get();
    }

}
