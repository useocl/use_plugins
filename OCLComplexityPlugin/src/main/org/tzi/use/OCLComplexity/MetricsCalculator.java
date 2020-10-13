/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2010 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// $Id$

package org.tzi.use.OCLComplexity;

import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class provides operations to calculate the model metriccs of OCl expressions.
 *
 * @author Timo Stüber
 */
public class MetricsCalculator {

    protected VisitorFactory visitorFactory;

    public MetricsCalculator(VisitorFactory visitorFactory) {
        this.visitorFactory = visitorFactory;
        boolean isDebugSet = Boolean.getBoolean("debug");
        if(isDebugSet) {
            Log.setDebug(true);
        }else {
            Log.setDebug(false);
        }
    }

    /**
     * This method calculates the complexity of the selected directory and accumulates results.
     *
     * @param directory where the use files are located
     * @return set of metrics which is the average over all expressions
     */
    public Set<Metric> calculateMetricOfDirectory(String directory) {
        String fullPath = (System.getProperty("user.dir") + File.separatorChar +
                directory).replace('/', File.separatorChar);

        PrintWriter printWriter = new PrintWriter(System.err);
        Set<Metric> result = new HashSet<>();
        long file_counts = 0;
        try {
            file_counts = Files.walk(Paths.get(fullPath)).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".use")).count();
            Log.println("Use Files founds: " + file_counts);
            Files.walk(Paths.get(fullPath)).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".use")).forEach(path -> {
                Log.println("Processing File ... " + path.toString());
                String model = path.toString();
                try (FileInputStream modelStream = new FileInputStream(model)) {
                    MModel mModel = USECompiler.compileSpecification(modelStream,
                            model, printWriter, new ModelFactory());
                    // add to result
                    Metric.combine(result, calculateTotalMetrics(mModel, true).get(mModel));
                } catch (IOException e) {
                    // This can be ignored
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // divide through collection size
        for (Metric metric : result) {
            metric.setValue(metric.getValue() / file_counts);
        }
        return result;
    }

    /**
     * Calculate the ocl metric for each invariant in a given model.
     *
     * @param model
     * @param expandOperations Option for the visitor. If <code>true</code>, operation expressions will also be considered.
     * @return Map<MModelElement, Set < Metric>>
     */
    public Map<MModelElement, Set<Metric>> calculateInvariantMetrics(
            MModel model, boolean expandOperations) {

        Map<MModelElement, Set<Metric>> result = new HashMap<MModelElement, Set<Metric>>();

        IMetricsVisitor localVisitor;

        for (MClassInvariant invariant : model.classInvariants()) {
            /*if(invariant.name().equals("uniqueName_STRAIGHT")) {
                Log.setDebug(true);
            }*/
            Log.debug("INVARIANT: " + invariant.name());
            Log.debug("INVARIANT: " + invariant.expandedExpression().toString());

            localVisitor = visitorFactory.createVisitor(expandOperations, invariant.cls().shortName(), invariant.vars());
            // call the visitor
            invariant.expandedExpression().processWithVisitor(localVisitor);
            result.put(invariant, localVisitor.getMetrics());
        }

        return result;
    }

    /**
     * Calculate the ocl metric for each pre condition in a given model.
     *
     * @param model
     * @param expandOperations Option for the visitor. If <code>true</code>, operation expressions will also be considered.
     * @return Map<MModelElement, Set < Metric>>
     */
    public Map<MModelElement, Set<Metric>> calculatePreConditionMetrics(
            MModel model, boolean expandOperations) {

        Map<MModelElement, Set<Metric>> result = new HashMap<MModelElement, Set<Metric>>();
        IMetricsVisitor localVisitor;

        for (MPrePostCondition preCondition : model.preConditions()) {

            localVisitor = visitorFactory.createVisitor(expandOperations);
            preCondition.expression().processWithVisitor(localVisitor);
            result.put(preCondition, localVisitor.getMetrics());
        }

        return result;
    }

    /**
     * Calculate the ocl metric for each post condition in a given model.
     *
     * @param model
     * @param expandOperations option for the visitor
     * @return Map<MModelElement, Set < Metric>>
     */
    public Map<MModelElement, Set<Metric>> calculatePostConditionMetrics(
            MModel model, boolean expandOperations) {

        Map<MModelElement, Set<Metric>> result = new HashMap<MModelElement, Set<Metric>>();

        IMetricsVisitor localVisitor;

        for (MPrePostCondition postCondition : model.postConditions()) {

            localVisitor = visitorFactory.createVisitor(expandOperations);
            postCondition.expression().processWithVisitor(localVisitor);
            result.put(postCondition, localVisitor.getMetrics());
        }

        return result;
    }

    /**
     * Calculate the ocl metric for contract (prepost) in a given model.
     *
     * @param model
     * @param expandOperations Option for the visitor. If <code>true</code>, operation expressions will also be considered.
     * @return Map<MModelElement, Set < Metric>>
     */
    public Map<MModelElement, Set<Metric>> calculateContractMetrics(
            MModel model, boolean expandOperations) {

        Map<MModelElement, Set<Metric>> result = new HashMap<MModelElement, Set<Metric>>();
        IMetricsVisitor localVisitor;

        for (MPrePostCondition ppc : model.prePostConditions()) {

            localVisitor = visitorFactory.createVisitor(expandOperations);
            ppc.expression().processWithVisitor(localVisitor);
            result.put(ppc, localVisitor.getMetrics());
        }
        return result;
    }

    /**
     * Calculate the ocl metric for each operation in a given model.
     * @param model
     * @param expandOperations Option for the visitor. If <code>true</code>, operation expressions will also be considered.
     * @return Map<MModelElement, Set<Metric>>
     */
    public Map<MModelElement, Set<Metric>> calculateOperationMetrics(
            MModel model, boolean expandOperations) {

        Map<MModelElement, Set<Metric>> result = new HashMap<MModelElement, Set<Metric>>();
        IMetricsVisitor localVisitor;

        for (MClass mClass : model.classes()) {
            for (MOperation mOperation : mClass.operations()) {
                if (!mOperation.hasExpression()) {
                    continue;
                }
                localVisitor = visitorFactory.createVisitor(expandOperations);
                mOperation.expression().processWithVisitor(localVisitor);
                result.put(mOperation, localVisitor.getMetrics());
            }
        }
        return result;
    }

    /**
     * Calculate the ocl metrics for the model and for each
     * invariant, pre, post condition and operations.
     *
     * @param model
     * @param expandOperations Option for the visitor. If <code>true</code>, operation expressions will also be
     *                         considered.
     * @return A {@link Map} which contains the data for each model element.
     */
    public Map<MModelElement, Set<Metric>> calculateTotalMetrics(
            MModel model, boolean expandOperations) {

        Map<MModelElement, Set<Metric>> result = new HashMap<MModelElement, Set<Metric>>();

        result.putAll(calculateInvariantMetrics(model, expandOperations));
        result.putAll(calculatePreConditionMetrics(model, expandOperations));
        result.putAll(calculatePostConditionMetrics(model, expandOperations));
        result.putAll(calculateOperationMetrics(model, expandOperations));
        return result;
    }
}
