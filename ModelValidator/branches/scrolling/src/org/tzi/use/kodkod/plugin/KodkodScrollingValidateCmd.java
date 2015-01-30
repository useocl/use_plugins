package org.tzi.use.kodkod.plugin;

import java.io.BufferedReader;
import java.io.File;
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

import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.config.Options;
import org.tzi.use.kodkod.UseScrollingKodkodModelValidator;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.kodkod.transform.ocl.DefaultExpressionVisitor;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;
import org.tzi.use.uml.ocl.value.VarBindings;

/**
 * Cmd-Class for the scrolling in the solutions.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class KodkodScrollingValidateCmd extends KodkodValidateCmd {

	protected static UseScrollingKodkodModelValidator validator;

	@Override
	protected void noArguments() {
		LOG.info(LogMessages.pagingCmdError);
	}

	@Override
	protected void handleArguments(String arguments) {
		arguments = arguments.trim();
		
		if (arguments.equalsIgnoreCase("next")) {
			if (checkValidatorPresent()) {
				validator.nextSolution();
			}
		} else if (arguments.equalsIgnoreCase("previous")) {
			if (checkValidatorPresent()) {
				validator.previousSolution();
			}
		} else {
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*(\\d+)\\s*\\)", Pattern.CASE_INSENSITIVE);
			Matcher m = showPattern.matcher(arguments);
			
			if (m.matches()) {
				if (checkValidatorPresent()) {
					int index = Integer.parseInt(m.group(1));
					validator.showSolution(index);
				}
			} else {
				String fileToOpen = Shell.getInstance().getFilenameToOpen(arguments, false);
				fileToOpen = Options.getFilenameToOpen(fileToOpen);
				File file = new File(fileToOpen);
	
				if (file.exists() && file.canRead() && !file.isDirectory()) {
					
					resetValidator();
					try {
						if(!readObservationTermAndSetValidator()){
							System.out.println("Aborting.");
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					
					extractConfigureAndValidate(file);
				} else {
					LOG.error(LogMessages.pagingCmdError);
				}
			}
		}
	}

	private void resetValidator() {
		validator = null;
	}

	private boolean readObservationTermAndSetValidator() throws IOException {
		Expression result = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		do {
			System.out.print("Input observation term (leave empty to abort): ");
			String line = br.readLine();
			StringWriter err = new StringWriter();
			
			if(line.trim().isEmpty()){
				// abort
				return false;
			}
				
			result = OCLCompiler.compileExpression(session.system().model(), line, "<observation term>", new PrintWriter(err), new VarBindings());

			// error checking
			if(result == null){
				System.out.println(err.toString());
				System.out.println();
				continue;
			}
			
			if(result.type().isTypeOfInteger() || (result.type().isKindOfCollection(VoidHandling.EXCLUDE_VOID) && ((CollectionType)result.type()).elemType().isTypeOfInteger())){
				
				// transform into kodkod
				kodkod.ast.Expression obsTermKodkod;
				try {
					DefaultExpressionVisitor ev = new DefaultExpressionVisitor(
							PluginModelFactory.INSTANCE.getModel(session.system().model()),
							new HashMap<String, Node>(), new HashMap<String, IClass>(),
							new HashMap<String, Variable>(), new ArrayList<String>());
					result.processWithVisitor(ev);
					obsTermKodkod = (kodkod.ast.Expression)ev.getObject();
				}
				catch(TransformationException ex){
					System.out.println("The expression cannot be transformed by the model validator.");
					System.out.println("Reason: " + ex.getMessage());
					System.out.println();
					continue;
				}
				
				// success
				createValidator();
				validator.setObservationTerm(result);
				validator.setObservationTermKodkod(obsTermKodkod);
				break;
			}
			
			System.out.println("The expression must result in type `Integer' or `Set(Integer)'.");
			System.out.println();
			continue;
		}
		while(true);
		
		return true;
	}

	private boolean checkValidatorPresent() {
		if (validator == null) {
			LOG.error(LogMessages.pagingCmdFileFirst);
			return false;
		}
		return true;
	}

	@Override
	protected KodkodModelValidator createValidator() {
		if(validator == null){
			validator = new UseScrollingKodkodModelValidator(session);
		}
		return validator;
	}
}
