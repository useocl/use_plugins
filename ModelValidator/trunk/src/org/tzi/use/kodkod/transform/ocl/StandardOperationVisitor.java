package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kodkod.ast.IntExpression;
import kodkod.ast.IntToExprCast;
import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;
import org.tzi.use.util.StringUtil;

/**
 * Extension of DefaultExpressionVisitor to visit the general operation
 * expressions.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class StandardOperationVisitor extends DefaultExpressionVisitor {

	private List<Object> arguments;

	public StandardOperationVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, Variable> replaceVariables, List<String> collectionVariables) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables);
	}

	@Override
	public void visitStdOp(ExpStdOp exp) {
		arguments = new ArrayList<Object>();

		for (org.tzi.use.uml.ocl.expr.Expression expArg : exp.args()) {
			DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
			expArg.processWithVisitor(visitor);
			arguments.add(visitor.getObject());
			set = set || visitor.isSet();
			object_type_nav = object_type_nav || visitor.isObject_type_nav();
		}
		
		printSumWarning(exp);

		if (exp.opname().equals("-") && !set && arguments.size() == 1) {
			handleMinus();
		} else {
			invokeMethod(exp.opname(), arguments, set);
		}
	}

	/**
	 * Prints a warning if the expression is a sum operation executed on a Bag or Sequence.
	 */
	private void printSumWarning(ExpStdOp exp) {
		if (exp.opname().equals("sum")
				&& exp.args().length > 0
				&& exp.args()[0] != null
				&& (exp.args()[0].type().isKindOfBag(VoidHandling.EXCLUDE_VOID) || exp.args()[0].type().isKindOfSequence(VoidHandling.EXCLUDE_VOID))
				&& ((CollectionType) exp.args()[0].type()).elemType().isKindOfInteger(VoidHandling.EXCLUDE_VOID)) {
			LOG.warn("The evaluation of sum expression "
					+ StringUtil.inQuotes(exp.toString())
					+ " might be wrong if source contains duplicates (Collection is interpreted as Set).");
		}
	}

	private void handleMinus() {
		if (arguments.get(0) instanceof IntToExprCast) {
			IntToExprCast intToExprCast = (IntToExprCast) arguments.get(0);

			IntExpression negate = intToExprCast.intExpr().negate();
			String stringValue = negate.toString();
			stringValue = stringValue.substring(1).replace(")", "");
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
