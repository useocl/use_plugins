package org.tzi.use.kodkod.plugin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import kodkod.ast.Formula;
import kodkod.ast.Node;
import kodkod.ast.Variable;
import kodkod.engine.Evaluator;

import org.tzi.kodkod.KodkodQueryCache;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.kodkod.transform.ocl.DefaultExpressionVisitor;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.uml.ocl.expr.Expression;

/**
 * Cmd-Class for queries on a existing solution.
 * 
 * @author Hendrik Reitmann
 * 
 */

public class KodkodQueryCmd extends AbstractPlugin implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		initialize(pluginCommand.getSession());

		String arguments = pluginCommand.getCmdArguments().trim();

		if (arguments.toLowerCase().equals("enable")) {
			KodkodQueryCache.INSTANCE.setQueryEnabled(true);
		} else if (arguments.toLowerCase().equals("disable")) {
			KodkodQueryCache.INSTANCE.setQueryEnabled(false);
		} else if (arguments.toLowerCase().equals("enabled")) {
			LOG.info(KodkodQueryCache.INSTANCE.isQueryEnabled());
		} else {
			Expression expr = createExpression(arguments);

			if (expr == null)
				return;

			evaluateQuery(transformExpression(expr));
		}
	}

	private Object transformExpression(Expression expr) {
		Map<String, Node> variables = new TreeMap<String, Node>();
		Map<String, IClass> variableClasses = new TreeMap<String, IClass>();

		Object object = null;
		try {

			DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model(), variables, variableClasses, new HashMap<String, Variable>(),
					new ArrayList<String>());
			expr.processWithVisitor(visitor);

			object = visitor.getObject();
		} catch (Exception e) {
			LOG.error("Cannot transform query. " + " " + e.getMessage());
		}
		return object;
	}

	private void evaluateQuery(Object object) {
		if (object != null) {
			try {
				Evaluator evaluator = KodkodQueryCache.INSTANCE.getEvaluator();
				if (object instanceof Formula) {
					LOG.info(evaluator.evaluate((Formula) object));
				} else {
					LOG.info(evaluator.evaluate((kodkod.ast.Expression) object));
				}
			} catch (Exception e) {
				LOG.error(LogMessages.queryEvaluationError);
				e.printStackTrace();
			}
		}
	}

	private Expression createExpression(String arguments) {
		InputStream stream = new ByteArrayInputStream(arguments.getBytes());
		Expression expr = OCLCompiler.compileExpression(mSystem.model(), mSystem.state(), stream, "<input>", new PrintWriter(System.err),
				mSystem.varBindings());
		return expr;
	}

}
