package org.tzi.use.kodkod.transform.ocl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.junit.BeforeClass;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.ocl.OCLGroupRegistry;
import org.tzi.kodkod.ocl.operation.AnyOperationGroup;
import org.tzi.kodkod.ocl.operation.BooleanOperationGroup;
import org.tzi.kodkod.ocl.operation.ClassOperationGroup;
import org.tzi.kodkod.ocl.operation.CollectionConstructorGroup;
import org.tzi.kodkod.ocl.operation.ConditionalOperationGroup;
import org.tzi.kodkod.ocl.operation.IntegerOperationGroup;
import org.tzi.kodkod.ocl.operation.SetOperationGroup;
import org.tzi.kodkod.ocl.operation.VariableOperationGroup;
import org.tzi.use.config.Options;
import org.tzi.use.config.Options.WarningType;
import org.tzi.use.kodkod.plugin.PluginModelFactory;
import org.tzi.use.main.Session;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.sys.MSystem;

/**
 * @author Hendrik
 */
public class OCLTest {

	protected static IModel model;
	private static MSystem mSystem;
	private static PrintWriter errorWriter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File file = new File("test/org/tzi/use/kodkod/transform/ocl/testModel.use");

		FileInputStream specStream = new FileInputStream(file);
		MModel mModel = USECompiler.compileSpecification(specStream, "testModel.use", new PrintWriter(System.err), new ModelFactory());

		Session session = new Session();
		mSystem = new MSystem(mModel);
		session.setSystem(mSystem);
		errorWriter = new PrintWriter(System.out);

		Options.setCheckWarningsUnrelatedTypes(WarningType.IGNORE);
		Options.doPLUGIN = false;
		Shell.createInstance(session, null);
		Shell shell = Shell.getInstance();
		shell.processLineSafely("!create ada:Person");
		shell.processLineSafely("!create bob:Person");
		shell.processLineSafely("!create cyd:Person");
		shell.processLineSafely("!create dan:Person");
		shell.processLineSafely("!create eve:Person");
		shell.processLineSafely("!create ibm:Company");
		shell.processLineSafely("!create apple:Company");
		shell.processLineSafely("!create uf:UndefinedFactory");
		shell.processLineSafely("!create jobAdaIbm:Job_AC between (ada,ibm)");
		shell.processLineSafely("!create parentAdaBob:Parent_AC between (ada,bob)");
		shell.processLineSafely("!create parentAdaCyd:Parent_AC between (ada,cyd)");
		shell.processLineSafely("!create petShop:Company");
		shell.processLineSafely("!create wolfi:Animal");
		shell.processLineSafely("!create stan:Animal");
		shell.processLineSafely("!create jobBobApple:Job_AC between (bob,apple)");
		shell.processLineSafely("!create jobBobIbm:Job_AC between (bob,ibm)");
		shell.processLineSafely("!create buyAdaPetShopWolfi:Buy_AC between (ada,petShop,wolfi)");
		shell.processLineSafely("!create buyBobPetShopStan:Buy_AC between (bob,petShop,stan)");
		shell.processLineSafely("!create buyAdaPetShopStan:Buy_AC between (ada,petShop,stan)");
		shell.processLineSafely("!create examAdaBobCyd:Exam_AC between (ada,bob,cyd)");
		shell.processLineSafely("!create examDanBobCyd:Exam_AC between (dan,bob,cyd)");

		model = PluginModelFactory.INSTANCE.getModel(mSystem.generator().gModel());

		OCLGroupRegistry registry = OCLGroupRegistry.INSTANCE;
		registry.unregisterAll();
		registry.registerOperationGroup(new VariableOperationGroup(model.typeFactory()));
		registry.registerOperationGroup(new IntegerOperationGroup(model.typeFactory()));
		registry.registerOperationGroup(new BooleanOperationGroup(model.typeFactory()));
		registry.registerOperationGroup(new ClassOperationGroup(model.typeFactory()));
		registry.registerOperationGroup(new AnyOperationGroup(model.typeFactory(), true));
		registry.registerOperationGroup(new ConditionalOperationGroup(model.typeFactory()));
		registry.registerOperationGroup(new SetOperationGroup(model.typeFactory()));
		registry.registerOperationGroup(new CollectionConstructorGroup(model.typeFactory()));
	}

	protected org.tzi.use.uml.ocl.expr.Expression toOCLExpression(String ocl) {
		return OCLCompiler.compileExpression(mSystem.model(), mSystem.state(), ocl, "Test", errorWriter, mSystem.varBindings());
	}

	protected Object toKodkod(org.tzi.use.uml.ocl.expr.Expression expression) {
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, new HashMap<String, Node>(), new HashMap<String, IClass>(), new HashMap<String, Variable>(), new ArrayList<String>());
		expression.processWithVisitor(visitor);
		return visitor.getObject();
	}

	protected String formatText(String strGivenText) {
		StringBuffer sbFormattedText = new StringBuffer(strGivenText);

		for (int i = 0; i < sbFormattedText.length(); i++) {
			if (sbFormattedText.charAt(i) == '\n')
				sbFormattedText.deleteCharAt(i);

			if (sbFormattedText.charAt(i) == '\r')
				sbFormattedText.deleteCharAt(i);

			if (sbFormattedText.charAt(i) == '\t')
				sbFormattedText.deleteCharAt(i);
		}

		return sbFormattedText.toString();
	}

	protected String removeWhitespaces(String strGivenText) {
		strGivenText = strGivenText.trim();
		StringBuffer buffer = new StringBuffer();

		SortedSet<Integer> numbers = new TreeSet<Integer>();
		for (int i = 0; i < strGivenText.length(); i++) {
			if (strGivenText.charAt(i) == ' ') {
				numbers.add(i);
			}
		}

		Integer start = null;
		Integer end = null;
		int beginIndex = 0;

		for (Integer num : numbers) {
			if (start == null || end == null) {
				start = num;
				end = num;
			} else if (end.equals(num - 1)) {
				end = num;
			} else {
				if (start.equals(end)) {
				} else if (start.equals(end - 1)) {
					buffer.append(strGivenText.substring(beginIndex, end));
					beginIndex = end + 1;
				} else {
					buffer.append(strGivenText.substring(beginIndex, start + 1));
					beginIndex = end + 1;
				}

				start = num;
				end = num;
			}
		}

		if (start != null && end != null) {
			String append = null;
			if (start.equals(end)) {
				buffer.append(strGivenText.substring(beginIndex));
				append = strGivenText.substring(beginIndex);
			} else if (start.equals(end - 1)) {
				buffer.append(strGivenText.substring(beginIndex, end));
				append = strGivenText.substring(beginIndex, end);
				beginIndex = end + 1;
			} else {
				buffer.append(strGivenText.substring(beginIndex, start + 1));
				append = strGivenText.substring(beginIndex, start + 1);
				beginIndex = end + 1;
			}
			if (buffer.toString().length() <= beginIndex) {
				if (!append.equals(strGivenText.substring(beginIndex))) {
					buffer.append(strGivenText.substring(beginIndex));
				}
			}

		} else {
			buffer.append(strGivenText.substring(beginIndex));
		}

		for (int i = 0; i < buffer.length(); i++) {
			if (buffer.charAt(i) == ' ') {
				if ((i - 4) >= 0 && buffer.substring(i - 4, i).equals("Int[")) {
					buffer.deleteCharAt(i);
					i--;
				} else {
					if ((i + 1) < buffer.length() && buffer.charAt(i + 1) == ']') {
						buffer.deleteCharAt(i);
						i--;
					} else {
						if ((i - 3) >= 0 && buffer.substring(i - 3, i).equals("Int")) {
							buffer.deleteCharAt(i);
							i--;
						}
					}
				}
			}
		}

		return buffer.toString();
	}

	protected void test(String test, String ocl, String expected) {
		System.out.println(test + " ocl:");
		System.out.println("\t" + ocl);

		org.tzi.use.uml.ocl.expr.Expression expression = toOCLExpression(ocl);
		Object transform = toKodkod(expression);
		if (transform != null) {

			if (transform instanceof Expression) {
				Expression kodkodExpression = (Expression) transform;
				checkEquality(expected, kodkodExpression);

			} else {
				Formula formula = (Formula) transform;
				checkEquality(expected, formula);
			}
		} else {
			assertTrue(false);
		}
	}

	private void checkEquality(String expected, Node node) {
		String format = removeWhitespaces(formatText(PrintHelper.prettyKodkod(node)));
		System.out.println("expected:");
		System.out.println("\t" + expected.trim());
		System.out.println("actual:");
		System.out.println("\t" + format);
		System.out.println();
		assertEquals(expected.trim(), format);
	}

	public static void main(String[] args) {
		String test = "(!(((Undefined_Set in (none + Int[1])) =>  " + "Undefined_Set else " + "(none + Int[1])) = Undefined_Set) && "
				+ "one ((Undefined_Set in (none + Int[1])) => " + "Undefined_Set else " + "(none + Int[1])) && "
				+ "(some i: (Undefined_Set in (none + Int[1])) => " + "Undefined_Set else " + "(none + Int[1]) | "
				+ "  Boolean_True = Boolean_True)) => " + "((Undefined_Set in (none + Int[1])) => " + "Undefined_Set else "
				+ "(none + Int[1])) else " + "Undefined";

		OCLTest t = new OCLTest();
		System.out.println(t.formatText(test));
		System.out.println(t.removeWhitespaces(t.formatText(test)));
	}
}
