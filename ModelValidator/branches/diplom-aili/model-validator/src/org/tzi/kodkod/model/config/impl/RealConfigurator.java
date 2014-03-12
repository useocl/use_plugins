package org.tzi.kodkod.model.config.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.impl.Range;
import org.tzi.kodkod.model.type.ConfigurableType;

/**
 * Configurator for the real type.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class RealConfigurator extends TypeConfigurator{

	private double step = 0.5;
	private DecimalFormat decimalFormat;
	
	public RealConfigurator(){
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		dfs.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat("#0.00",dfs); 
	}
	
	@Override
	public TupleSet lowerBound(ConfigurableType type, int arity, TupleFactory tupleFactory) {
		TupleSet lower = tupleFactory.noneOf(1);
		
		for (String[] specific : allValues()) {
			lower.add(tupleFactory.tuple(type.name() + "_" + decimalFormat.format(Double.parseDouble(specific[0]))));
		}

		for(Range range : ranges){
			for (double i = range.getLower(); i <= range.getUpper(); i+=step) {
				lower.add(tupleFactory.tuple(type.name() + "_" + decimalFormat.format(i)));
			}
		}
		
		return lower;
	}
	
	@Override
	public TupleSet upperBound(ConfigurableType type, int arity, TupleFactory tupleFactory) {
		return lowerBound(type, arity, tupleFactory);
	}
	
	@Override
	public List<Object> atoms(ConfigurableType type, List<Object> literals) {
		Set<Object >atoms=new HashSet<Object>();
		atoms.addAll(literals);		

		for(Range range : ranges){
			for (double i = range.getLower(); i <= range.getUpper(); i+=step) {
				atoms.add(type.name() + "_" + decimalFormat.format(i));
			}
		}

		for (String[] specific : allValues()) {
			atoms.add(type.name() + "_" + decimalFormat.format(Double.parseDouble(specific[0])));
		}
		
		return new ArrayList<Object>(atoms);
	}

	public void setStep(double step) {
		this.step = step;
	}

}
