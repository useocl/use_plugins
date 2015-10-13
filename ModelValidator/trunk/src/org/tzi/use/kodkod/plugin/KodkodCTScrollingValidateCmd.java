package org.tzi.use.kodkod.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.kodkod.UseCTScrollingKodkodModelValidator;
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

	@Override
	protected void noArguments() {
		LOG.info(LogMessages.pagingCmdError);
	}

	@Override
	protected void handleArguments(String[] arguments) {
		String firstArgument = arguments[0];
		
		if (firstArgument.equalsIgnoreCase("next")) {
			if (checkValidatorPresent()) {
				validator.nextSolution();
			}
		} else if (firstArgument.equalsIgnoreCase("previous")) {
			if (checkValidatorPresent()) {
				validator.previousSolution();
			}
		} else {
			String argumentsAsString = StringUtil.fmtSeq(arguments, " ");
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*(\\d+)\\s*\\)", Pattern.CASE_INSENSITIVE);
			Matcher m = showPattern.matcher(argumentsAsString);
			
			if (m.matches()) {
				if (checkValidatorPresent()) {
					int index = Integer.parseInt(m.group(1));
					validator.showSolution(index);
				}
			} else {
				resetValidator();
				try {
					if(!readClassifyingTerms()){
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
	protected boolean readClassifyingTerms() throws IOException {
		Expression result = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		UseCTScrollingKodkodModelValidator v = (UseCTScrollingKodkodModelValidator) validator;
		int terms = 1;
		System.out.println("Input classifying terms (leave empty to abort, enter `v' or `validate' to start validation)");
		
		do {
			System.out.print("Term " + terms + ": ");
			String line = br.readLine();
			StringWriter err = new StringWriter();
			
			if(line.trim().isEmpty()){
				// abort
				return false;
			}
			else if(line.trim().equalsIgnoreCase("v") || line.trim().equalsIgnoreCase("validate")){
				break;
			}
				
			result = OCLCompiler.compileExpression(session.system().model(), line, "<classifying term>", new PrintWriter(err), new VarBindings());

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
			v.addClassifyingTerm(result, obsTermKodkod);
			terms++;
		}
		while(true);
		
		// set of classifying terms must contain at least one
		return v.classifyingTermCount() > 0;
	}
	
	@Override
	protected void resetValidator() {
		validator = new UseCTScrollingKodkodModelValidator(session);
	}
}
