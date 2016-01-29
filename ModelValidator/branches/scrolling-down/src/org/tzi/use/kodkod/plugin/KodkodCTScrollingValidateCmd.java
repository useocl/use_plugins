package org.tzi.use.kodkod.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.kodkod.UseCTScrollingKodkodModelValidator;
import org.tzi.use.kodkod.UseScrollingKodkodModelValidator;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.kodkod.transform.ocl.DefaultExpressionVisitor;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.util.StringUtil;

/**
 * Cmd-Class for the scrolling in the solutions using classifier terms.
 *
 * @author Frank Hilken
 */
public class KodkodCTScrollingValidateCmd extends KodkodScrollingValidateCmd {

	protected UseScrollingKodkodModelValidator rootValidator = null;
	
	@Override
	protected void noArguments() {
		LOG.info(LogMessages.pagingCmdError);
	}

	@Override
	protected void handleArguments(String[] arguments) {
		String firstArgument = arguments[0];
		
		if (firstArgument.equalsIgnoreCase("next")) {
			if (checkValidatorPresent()) {
				activeValidator.nextSolution();
			}
		} else if (firstArgument.equalsIgnoreCase("previous")) {
			if (checkValidatorPresent()) {
				activeValidator.previousSolution();
			}
		} else if (firstArgument.equalsIgnoreCase("down")){
			if(checkValidatorPresent()){
				try {
					down(true);
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
					return;
				}
			}
		} else if (firstArgument.equalsIgnoreCase("up")){
			if(checkValidatorPresent()){
				up();
			}
		} else {
			String argumentsAsString = StringUtil.fmtSeq(arguments, " ");
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*([\\d\\.]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
			Matcher m = showPattern.matcher(argumentsAsString);
			
			if (m.matches()) {
				if (checkValidatorPresent()) {
					show(m.group(1));
//					int index = Integer.parseInt(m.group(1));
//					activeValidator.showSolution(index);
				}
			} else {
				// check if properties file exists and cancel command if it does not
				if(!new File(firstArgument).exists()){
					LOG.error("Properties file not found.");
					return;
				}
				
				resetValidator();
				try {
					if(!readClassifyingTerms((UseCTScrollingKodkodModelValidator) activeValidator)){
						LOG.info("Aborting.");
						return;
					}
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
					return;
				}
				
				super.handleArguments(arguments, true);
			}
		}
	}

	/**
	 * Reads classifying terms from the shell, checks and converts them and adds
	 * them to the validator.
	 */
	protected boolean readClassifyingTerms(UseCTScrollingKodkodModelValidator val) throws IOException {
		Expression result = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int terms = 1;
		
		//XXX we request the model once to issue the transformation and avoid the transformation output in between term inputs
		model();
		
		System.out.println("Input classifying terms (leave empty to abort, enter `v' or `validate' to start validation)");
		
		do {
			System.out.print("Term " + terms + ": ");
			String line = br.readLine().trim();
			StringWriter err = new StringWriter();
			
			if(line.isEmpty()){
				// abort
				return false;
			}
			else if(line.equalsIgnoreCase("v") || line.equalsIgnoreCase("validate")){
				break;
			}
				
			result = OCLCompiler.compileExpression(session.system().model(), line, "<classifying term>", new PrintWriter(err, true), new VarBindings());

			// error checking
			if(result == null){
				LOG.error(err.toString());
				continue;
			}
			
			if(!result.type().isTypeOfInteger() && !result.type().isTypeOfBoolean()){
				LOG.error("The expression must result in type `Boolean' or `Integer'.");
				continue;
			}
			
			// transform into kodkod
			Node obsTermKodkod;
			try {
				DefaultExpressionVisitor ev = new DefaultExpressionVisitor(model(),
						new HashMap<String, Node>(), new HashMap<String, IClass>(),
						new HashMap<String, Variable>(), new ArrayList<String>());
				result.processWithVisitor(ev);
				obsTermKodkod = (Node) ev.getObject();
			}
			catch(TransformationException ex){
				LOG.error("The expression cannot be transformed by the model validator. Reason: " + ex.getMessage());
				continue;
			}
			
			// success
			val.addClassifyingTerm(result, obsTermKodkod);
			terms++;
		} while(true);
		
		// set of classifying terms must contain at least one
		return val.classifyingTermCount() > 0;
	}
	
	@Override
	protected void resetValidator() {
		activeValidator = new UseCTScrollingKodkodModelValidator(session);
		rootValidator = activeValidator;
	}

	protected final Map<UseScrollingKodkodModelValidator, List<UseScrollingKodkodModelValidator>> children = new HashMap<>();
	protected final Map<UseScrollingKodkodModelValidator, UseScrollingKodkodModelValidator> parents = new HashMap<>();
	
	public void down(boolean useCT) throws IOException {
		int solutionIndex = activeValidator.getSolutionIndex();
		List<UseScrollingKodkodModelValidator> childrenList = getChildrenListSafe(activeValidator);
		
		if(childrenList.size() > solutionIndex && childrenList.get(solutionIndex) != null){
			activeValidator = childrenList.get(solutionIndex);
			activeValidator.showSolution(activeValidator.getSolutionIndex());
			return;
		}
		
		UseScrollingKodkodModelValidator newVal;
		if(useCT){
			if(!(activeValidator instanceof UseCTScrollingKodkodModelValidator)){
				//TODO
				LOG.error("Cannot go down from a non-CT validator.");
				return;
			}
			newVal = new UseCTScrollingKodkodModelValidator(session, (UseCTScrollingKodkodModelValidator) activeValidator);
			if(!readClassifyingTerms((UseCTScrollingKodkodModelValidator) newVal)){
				// abort
				LOG.info("Must use at least one classifying term. Aborting.");
				return;
			}
			
			// copy first result of parent validator to newVal (N.1 == N)
			((UseCTScrollingKodkodModelValidator) newVal).copyParentSolutionAndUpdateSolutionTerms(session.system().state());
		} else {
			//TODO
			throw new UnsupportedOperationException();
//			newVal = new UseScrollingKodkodModelValidator(session);
		}
		
		parents.put(newVal, activeValidator);
		getChildrenListSafe(activeValidator).add(solutionIndex, newVal);
		
		activeValidator = newVal;
		newVal.validate(model());
	}
	
	protected List<UseScrollingKodkodModelValidator> getChildrenListSafe(UseScrollingKodkodModelValidator activeValidator) {
		if(!children.containsKey(activeValidator)){
			children.put(activeValidator, new ArrayList<UseScrollingKodkodModelValidator>());
		}
		return children.get(activeValidator);
	}

	public void up() {
		activeValidator = parents.get(activeValidator);
		activeValidator.showSolution(activeValidator.getSolutionIndex());
	}
	
	public void show(String dings) {
		
		UseScrollingKodkodModelValidator newVal = rootValidator;
		
		String[] split = dings.split(".");
		for (int i = 0; i < split.length; i++) {
			int idx = Integer.parseInt(split[i]);
			newVal = children.get(newVal).get(idx);
		}
		
		activeValidator = newVal;
		activeValidator.showSolution(activeValidator.getSolutionIndex());
	}
	
}
