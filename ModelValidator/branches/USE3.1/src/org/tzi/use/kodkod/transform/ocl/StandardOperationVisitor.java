package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kodkod.ast.IntExpression;
import kodkod.ast.IntToExprCast;
import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.apache.log4j.Logger;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.uml.ocl.expr.ExpStdOp;

/**
 * Extension of DefaultExpressionVisitor to visit the general operation
 * expressions.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class StandardOperationVisitor extends DefaultExpressionVisitor {

	protected static final Logger LOG = Logger.getLogger(StandardOperationVisitor.class);

	private UUID id;
	private List<Object> arguments;

	public StandardOperationVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, List<Variable>> replaceVariables, List<String> collectionVariables) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables);
		id = UUID.randomUUID();
	}

	@Override
	public void visitStdOp(ExpStdOp exp) {
		arguments = new ArrayList<Object>();

		LOG.debug(id + " - op: " + exp.opname());
		for (org.tzi.use.uml.ocl.expr.Expression expArg : exp.args()) {
			LOG.debug(id + " - arg: " + expArg);
			DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
			expArg.processWithVisitor(visitor);
			arguments.add(visitor.getObject());
			set = set || visitor.isSet();
			object_type_nav = object_type_nav || visitor.isObject_type_nav();
		}

		if (exp.opname() == "-" && !set && arguments.size() == 1) {
			handleMinus();
		} else {
			invokeMethod(exp.opname(), arguments, set);
		}
	}

	private void handleMinus() {
		if (arguments.get(0) instanceof IntToExprCast) {
			IntToExprCast intToExprCast = (IntToExprCast) arguments.get(0);

			IntExpression negate = intToExprCast.intExpr().negate();
			String stringValue = negate.toString();
			stringValue = stringValue.substring(1).replace(")", "");
			LOG.debug(id + " : " + stringValue);
			if (!stringValue.startsWith("--")) {
				Integer integerValue = Integer.parseInt(stringValue);

				arguments.clear();
				arguments.add(negate.toExpression());
				visitConstInteger(integerValue);
				return;
			}

		}

		invokeMethod("negation", arguments, set);
	}
}
