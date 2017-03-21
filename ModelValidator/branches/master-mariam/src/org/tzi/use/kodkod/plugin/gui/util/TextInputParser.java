package org.tzi.use.kodkod.plugin.gui.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.tzi.kodkod.model.iface.IAssociation;

public class TextInputParser {

	public enum ModelComponent {
		INTEGER, STRING, REAL, CLASS, ATTRIBUTE, ASSOCIATION
	}
	
	public static final class Result<T> {
		private final Set<T> parsedValues = new LinkedHashSet<T>();
		private final Set<String> errorValues = new LinkedHashSet<String>();
		
		public void addParsedValue(T val){
			parsedValues.add(val);
		}
		
		public void addErrorValue(String val){
			errorValues.add(val);
		}
		
		public Set<T> getParsedValues() {
			return parsedValues;
		}
		
		public Set<String> getErrorValues() {
			return errorValues;
		}
	}
	
	private static interface ValueParser<T> {
		void parse(String part, Result<T> res);
	}
	
	private final String input;
	
	public TextInputParser(String input) {
		this.input = input;
	}
	
	private <T> void parseInput(String theInput, String regex, Result<T> res, ValueParser<T> parser){
		Pattern reg = Pattern.compile(regex);
		Matcher matcher = reg.matcher(theInput);
		
		while(matcher.find()){
			String part = matcher.group().trim();
			if(!part.isEmpty()){
				parser.parse(part, res);
			}
		}
	}
	
	public Result<Integer> parseIntegerValues() {
		Result<Integer> res = new Result<Integer>();
		
		parseInput(input, "([^,]*)", res, new ValueParser<Integer>() {
			@Override
			public void parse(String part, Result<Integer> res) {
				try {
					int i = Integer.parseInt(part);
					res.addParsedValue(i);
				}
				catch (NumberFormatException e) {
					res.addErrorValue(part);
				}
			}
		});
		
		return res;
	}
	
	public Result<String> parseStringValues() {
		Result<String> res = new Result<String>();
		
		parseInput(input, "([^,]*)", res, new ValueParser<String>() {
			@Override
			public void parse(String part, Result<String> res) {
				Pattern p = Pattern.compile("\'(.+?)\'");
				Matcher m = p.matcher(part);
				
				if(m.find()){
					res.addParsedValue(m.group(0));
				}
				else {
					res.addErrorValue(part);
				}
			}
		});
		
		return res;
	}
	
	public Result<Double> parseRealValues() {
		Result<Double> res = new Result<Double>();
		
		parseInput(input, "([^,]*)", res, new ValueParser<Double>() {
			@Override
			public void parse(String part, Result<Double> res) {
				try {
					double d = Double.parseDouble(part);
					res.addParsedValue(d);
				}
				catch(NumberFormatException e) {
					res.addErrorValue(part);
				}
			}
		});
		
		return res;
	}

	private static final String objectNamePattern = "[$a-zA-Z_][a-zA-Z_0-9]*";
	public Result<String> parseClassValues() {
		Result<String> res = new Result<String>();
		
		parseInput(input, "([^,]*)", res, new ValueParser<String>() {
			@Override
			public void parse(String part, Result<String> res) {
				if(part.matches(objectNamePattern)){
					res.addParsedValue(part);
				}
				else {
					res.addErrorValue(part);
				}
			}
		});
		
		return res;
	}
	
	public Result<String> parseAttributeValues() {
		Result<String> res = new Result<String>();
		
		parseInput(input, "([^,]*)", res, new ValueParser<String>() {
			@Override
			public void parse(String part, Result<String> res) {
				res.addParsedValue(part);
			}
		});
		
		return res;
	}
	
	public Result<String> parseAssociationValues(final IAssociation assoc) {
		Result<String> res = new Result<String>();
		
		parseInput(input, "(\\(.*?\\)|[^,\\(]+)", res, new ValueParser<String>() {
			@Override
			public void parse(String part, Result<String> res) {
				int numEnds = assoc.associationEnds().size() + (assoc.isAssociationClass()? 1 : 0);
				if(StringUtils.countMatches(part, ",")+1 == numEnds){
					res.addParsedValue(part);
				} else {
					res.addErrorValue(part);
				}
			}
		});
		
		return res;
	}
	
}
