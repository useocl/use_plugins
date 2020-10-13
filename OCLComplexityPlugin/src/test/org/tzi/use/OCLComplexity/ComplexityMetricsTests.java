package org.tzi.use.OCLComplexity;

import junit.framework.TestCase;
import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MPrePostCondition;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.util.Log;

import java.io.*;
import java.util.*;


public class ComplexityMetricsTests extends TestCase {
    private static final Map<String, MModel> compiledModels = new HashMap<>();
    private static final String TEST_PATH =
            System.getProperty("user.dir")
                    + "/src/test/org/tzi/use/OCLComplexity/models".replace('/', File.separatorChar);

    private PrintWriter printWriter = null;
    private boolean expandOperatos = true;

    protected void setUp() {
        Log.setDebug(true);
    }

    /**
     * Return the cached compiled model or compile new once
     *
     * @param model filename of Model (this musst be relative to TEST_PATH)
     * @return compiled model
     */
    private MModel getCompiledModel(String model) {
        if (printWriter == null) {
            printWriter = new PrintWriter(System.err);
        }
        MModel result;
        if (model == null || model.isEmpty()) {
            String defaultModelName = "TestDummy";
            result = compiledModels.get(defaultModelName);
            if (result == null) {
                result = new ModelFactory().createModel(defaultModelName);
                compiledModels.put(defaultModelName, result);
            }
            return result;
        }
        model = TEST_PATH + File.separator + model;
        result = compiledModels.get(model);

        if (result == null) {
            try (FileInputStream modelStream = new FileInputStream(model)) {
                result = USECompiler.compileSpecification(modelStream,
                        model, printWriter, new ModelFactory());
                compiledModels.put(model, result);
            } catch (IOException e) {
                // This can be ignored
                e.printStackTrace();
            }
        }
        return result;
    }



    private VarDeclList createVars(MClass cls) {
        VarDeclList parameter = new VarDeclList(false);
        if(cls != null) {
            try {
                parameter.add(new VarDecl("self", cls));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parameter;
    }

    private Expression compileExpression(String expressionString, MModel model, MClass cls) {
        return compileExpression(expressionString, model, cls, null);
    }

    private Expression compileExpression(String expressionString, MModel model, MClass cls, VarDeclList varDeclList) {
        InputStream stream = new ByteArrayInputStream(expressionString.getBytes());

        // declare the self class
        Symtable vars = new Symtable();
        if (varDeclList == null) {
            varDeclList = createVars(cls);
        }

        Expression allInstances = null;
        if(cls != null) {
            try {
                varDeclList.addVariablesToSymtable(vars);
                allInstances = new ExpAllInstances(cls);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Expression expr =
                OCLCompiler.compileExpression(
                        model,
                        expressionString,
                        "<input>",
                        printWriter,
                        vars);

        /*
        // enable expaned expression
        if (allInstances!=null) {
            try {
                expr = new ExpForAll(parameter, allInstances, expr);
            } catch (ExpInvalidException e) {
                e.printStackTrace();
            }
        }*/

        assertNotNull(expr + " compiles", expr);
        return expr;
    }

    private AbstractMetricsData getMetricsData(String expressionStr) {
        return getMetricsData(expressionStr, "", "");
    }

    private AbstractMetricsData getMetricsData(String expressionStr, String model, String contextClass) {
        MModel compiledModel = getCompiledModel(model);
        if (contextClass != null && !contextClass.isEmpty()) {
            // This class should exist
            assertNotNull(compiledModel.getClass(contextClass));
        } else {
            contextClass = "";
        }
        VarDeclList varDeclList = createVars(compiledModel.getClass(contextClass));
        Expression expr = compileExpression(expressionStr, compiledModel, compiledModel.getClass(contextClass), varDeclList);

        ComplexityCalculationVisitor complVisitor = new ComplexityCalculationVisitor(expandOperatos, contextClass, varDeclList);

        expr.processWithVisitor(complVisitor);
        return complVisitor.getMetricsData();
    }

    public void testNumberOfOCLKeyWords() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("OrderedSet{-9..-8}->including(8)->including(9)->isUnique(i|i*i)=false", 1.0);
        expressions.put("let TupleSet=Set{Tuple{s:'M',t:'P'},Tuple{s:'P',t:'A'},Tuple{s:'A',t:'B'}," +
                "Tuple{s:'M',t:'Z'},Tuple{s:'Z',t:'V'},Tuple{t:'B',s:'V'}} in TupleSet->closure(T1| " +
                "TupleSet->select(T2|T1.t=T2.s)->collect(T2|Tuple{s:T1.s,t:T2.t})->asSet())", 1.0);
        expressions.put("Set{-2..2}->iterate(i:Integer;r:Set(Sequence(OclAny))=Set{}| " +
                "r->including(Sequence{if i.mod(2)=0 and not (1=1) or true then 'E' else 'O' endif}))", 5.0);


        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfOCLKeywords"));
        }
    }

    public void testNumberOfVariablesDefinedByLet() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("let var1=1, var2=2 in var1 + var2", 2.0);
        expressions.put("let var1=1 in let var2=2 in var1 + var2", 2.0);
        expressions.put("OrderedSet{-9..-8}->including(8)->including(9)->isUnique(i|i*i)=false", 0.0);
        expressions.put("let TupleSet=Set{Tuple{s:'M',t:'P'},Tuple{s:'P',t:'A'},Tuple{s:'A',t:'B'}," +
                "Tuple{s:'M',t:'Z'},Tuple{s:'Z',t:'V'},Tuple{t:'B',s:'V'}} in TupleSet->closure(T1| " +
                "TupleSet->select(T2|T1.t=T2.s)->collect(T2|Tuple{s:T1.s,t:T2.t})->asSet())", 1.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfVariablesDefinedByLet"));
        }
    }


    public void testNumberOfExplicitSelf() {

        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 2.0);
        expressions.put("Person.allInstances.forAll(employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.lastName = self.lastName)))", 1.0);
        // self is a iteration var in this case
        expressions.put("Person.allInstances.forAll(employer->forAll(self | self.employee->exists(lastName = lastName)))", 0.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfExplicitSelf"));
        }
    }

    public void testNumberOfIfExpressions() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Set{-2..2}->iterate(i:Integer;r:Set(Sequence(OclAny))=Set{}| " +
                "r->including(Sequence{if i.mod(2)=0 and not (1=1) or true then 'E' else 'O' endif}))", 1.0);
        expressions.put("if 'A' = 'A' then if true then 1 else 2 endif else 3 endif", 2.0);
        expressions.put("if 'A' = 'A' then " +
                "if true then 1 else 2 endif " +
                "else " +
                "if false then 3 else if true then 4 else 5 endif endif " +
                "endif", 4.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfIfExpressions"));
        }
    }

    public void testNumberOfSet() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Set{-2..2}->collect(i|i)", 1.0);
        expressions.put("Set{-2..2}->iterate(i:Integer; s:Set(Integer)=Set{}|s->including(i))", 2.0);


        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfSet"));
        }
    }

    public void testNumberOfOrderedSet() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("OrderedSet{-2..2}->collect(i|i)", 1.0);
        expressions.put("OrderedSet{-2..2}->iterate(i:Integer; s:OrderedSet(Integer)=OrderedSet{}|s->including(i))", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfOrderedSet"));
        }
    }

    public void testNumberOfBag() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Bag{-2..2}->collect(i|i)", 1.0);
        expressions.put("Bag{-2..2}->iterate(i:Integer; s:Bag(Integer)=Bag{}|s->including(i))", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfBag"));
        }
    }

    public void testNumberOfSequence() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Sequence{-2..2}->collect(i|i)", 1.0);
        expressions.put("Sequence{-2..2}->iterate(i:Integer; s:Sequence(Integer)=Sequence{}|s->including(i))", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfSequence"));
        }
    }

    public void testNumberOfTuple() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Set{Tuple{name:String='use', age:Integer=2}}->collect(i|i)", 1.0);
        expressions.put("Set{Tuple{name:String='use', age:Integer=2}}->iterate(" +
                "i:Tuple(name:String, age:Integer); " +
                "s:Set(Tuple(name:String, age:Integer))=Set{Tuple{name='t', age=24}}|s->including(i))", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfTuple"));
        }
    }

    public void testNumberOfBooleanOperators() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Set{-2..2}->iterate(i:Integer;r:Set(Sequence(OclAny))=Set{}| " +
                "r->including(Sequence{if i.mod(2)=0 and not (1=1) or true then 'E' else 'O' endif}))", 3.0);
        expressions.put("if 'A' = 'A' and (true xor true) then if true or false then 1 else 2 endif else 3 endif", 3.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfBooleanOperators"));
        }
    }

    public void testNumberOfComparisonOperators() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Set{-2..2}->iterate(i:Integer;r:Set(Sequence(OclAny))=Set{}| " +
                "r->including(Sequence{if i.mod(2) = 0 and not (1 = 1) or true then 'E' else 'O' endif}))", 2.0);
        expressions.put("if 'A' = 'A' and (1 < 2) then if true <> false then 1 else 2 endif else 3 endif", 3.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey());
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfComparisonOperators"));
        }
    }

    public void testNumberOfExplicitIterator() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 1.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.lastName = self.lastName))", 2.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.lastName = self.lastName and iter2.lastName <> ''))", 3.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfExplicitIterators"));
        }
    }

    public void testNumberOfImplicitIterator() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 1.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.lastName = self.lastName))", 0.0);
        expressions.put("self.employer->forAll(employee->exists(lastName = self.lastName))", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfImplicitIterators"));
        }
    }

    public void testNumberOfAttributesClassifierSelf() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 1.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.lastName = self.lastName))", 1.0);
        expressions.put("Person.allInstances.forAll(employer->forAll(name <> ''))", 0.0);
        expressions.put("self.lastName <> ''", 1.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfAttributesClassifierSelf"));
        }
    }

    public void testNumberOfOperationsClassifierSelf() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 0.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.fullName() = self.fullName()))", 1.0);
        expressions.put("Person.allInstances.forAll(isBirthday() and fullName() = 'Max Mustermann')", 0.0); // implicit iterator
        expressions.put("self.isBirthday() and self.fullName() = 'Max Mustermann'", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfOperationsClassifierSelf"));
        }
    }

    public void testNumberOfTypeOf() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("Programmer.allInstances.forAll(e | e.oclIsKindOf(Person))", 1.0);
        expressions.put("Programmer.allInstances.forAll(e | e.oclIsTypeOf(Person))", 1.0);
        expressions.put("Person.allInstances.forAll(e | e.oclAsType(Designer).oclIsTypeOf(Person))", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "Inheritance.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfTypeOf"));
        }
    }

    public void testNumberOfPropertiesPostfixed() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("post_inc", 1.0);
        expressions.put("post_dobule_and_square", 2.0);

        MModel compiledModel = getCompiledModel("Postfixed.use");
        Collection<MPrePostCondition> postConditions = compiledModel.postConditions();
        ComplexityCalculationVisitor complVisitor;
        for (MPrePostCondition entry : postConditions) {
            complVisitor = new ComplexityCalculationVisitor(expandOperatos, entry.cls().name());
            entry.expression().processWithVisitor(complVisitor);
            data = complVisitor.getMetricsData();
            assertEquals(entry.name(),  expressions.get(entry.name()), data.getMetric("NumberOfPropertiesPostfixed"));
        }

    }

    public void testNumberOfNavigatedRelationships() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        //expressions.put("self.own", 1);
        expressions.put("self.product.seller = self.order", 2.0);
        expressions.put("self.product.seller = self.order.seller", 3.0);
        expressions.put("self.order.seller.product.customer = self.product.seller.order.customer", 3.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "NavigatedRelationships.use", "Customer");
            assertEquals(entry.getKey(), entry.getValue(), data.getMetric("NumberOfNavigatedRelationships"));
        }
    }

    public void testNumberOfAttributesThroughNavigations() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employee.forAll(firstName = 'Max')", 1.0);
        expressions.put("self.employee.forAll(lastName = 'Mustermann' and self.name = 'Example')", 1.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Company");
            assertEquals(entry.getKey(), entry.getValue(), data.getMetric("NumberOfAttributesThroughNavigations"));
        }
    }

    public void testWeightedNumberOfReferredOperationsThroughNavigations() {
        //FIXME: USE does not apply to OCL definition... This metric just assumes, that a tuple imply out parameter...
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employee.forAll(p| p.income().result > 9000)", 3.0); // income over 9000
        expressions.put("self.employee.forAll(p| p.income().result > 9000 and p.income().bonus > 0)", 3.0);
        expressions.put("self.employee.exists(p| p.isBirthday())", 2.0);
        expressions.put("self.employee.forAll(p| self.hire(p))", 0.0);
        expressions.put("self.employee.forAll(p| p.income_history('20.20.2020').result > 9000 and p.income_history('20.20.2020').bonus > 0)", 6.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Company");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("WeightedNumberOfReferredOperationsThroughNavigations"));
        }
    }

    public void testNumberOfNavigatedClass() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.b.c", 2.0);
        expressions.put("self.b.a.b", 2.0);
        expressions.put("self.b.a.b.a.c", 3.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "NavigatedClasses.use", "A");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfNavigatedClasses"));
        }
    }

    public void testNumberOfParametersTypeClass() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employee.forAll(p| self.fire(p))", 1.0);
        expressions.put("self.employee.forAll(p| self.hire(p))", 1.0);
        expressions.put("self.employee.forAll(p| self.hire(p) and self.fire(p))", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Company");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfParametersTypeClass"));
        }
    }

    public void testNumberOfUserDefinedDataTypeAttributes() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.lastName", 0.0);
        expressions.put("self.secret.id", 1.0);
        expressions.put("self.secret <> null and self.secret.verifyID()", 0.0);
        expressions.put("self.employer.forAll(name <> null)", 0.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "UserDefinedData.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfUserDefinedDataTypeAttributes"));
        }
    }

    public void testNumberOfUserDefinedDataTypeOperations() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.lastName", 0.0);
        expressions.put("self.secret.id", 0.0);
        expressions.put("self.secret <> null and self.secret.verifyID()", 1.0);
        expressions.put("self.employer.forAll(startProject() <> null)", 0.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "UserDefinedData.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("NumberOfUserDefinedDataTypeOperations"));
        }
    }

    public void testWeightedNumberOfNavigations() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 3.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.employer->one(iter3 | iter3.name = iter1.name)))", 6.0);
        expressions.put("self.employer.employee <> self.employer", 4.0);
        expressions.put("Person.allInstances.forAll(isBirthday() and fullName() = 'Max Mustermann')", 0.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("WeightedNumberOfNavigations"));
        }
    }
    public void testDepthOfNavigation() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 4.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.employer->one(iter3 | iter3.name = iter1.name)))", 7.0);
        expressions.put("self.employer.employee <> self.employer", 4.0); // self.employer.collect(...) <> self.employer
        expressions.put("self.employer.exists(name = 'TEST') and self.employer.exists(name <> 'Test1')", 1.0); // self.employer.collect(...) <> self.employer

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("DepthOfNavigation"));
        }
    }
    public void testWeightedNumberOfCollectionOperations() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(lastName = self.lastName))", 3.0);
        expressions.put("self.employer->forAll(iter1 | iter1.employee->exists(iter2 | iter2.employer->one(iter3 | iter3.name = iter1.name)))", 6.0);
        expressions.put("self.employer.employee <> self.employer", 1.0); // self.employer.collect(...) <> self.employer
        expressions.put("Person.allInstances.employer.employee <> self.employer", 2.0);

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(),  entry.getValue(), data.getMetric("WeightedNumberOfCollectionOperations"));
        }
    }

    public void testWeightedCategories() {
        AbstractMetricsData data;
        // Add some expression to test
        Map<String, Double> expressions = new HashMap<>();

        expressions.put("self.employer->forAll(" + // 1 + 2 = 3
                "iter1 | iter1.employee->exists(" + // 2 * ( 1 + 2) = 6
                "lastName = self.lastName))", 18.0); // 3* ( 1 + 2) = 9
        expressions.put("self.employer->forAll(" + //1 + 2 = 3
                "iter1 | iter1.employee->exists(" + // 2* ( 1 + 2) = 6
                "iter2 | iter2.employer->one(" + // 3* (1+2) = 9
                "iter3 | iter3.name = iter1.name)))", 30.0); // 4 * (1 + 1 + 1) = 12


        expressions.put("self.employer.employee <> self.employer", 7.0); // (self.employer->collect($e : Company | $e.employee) <> self.employer)
        expressions.put("Person.allInstances.employer.employee <> self.employer", 12.0); //Person.allInstances->collect($e : Person | $e.employer)->collect($e : Company | $e.employee) <> self.employer

        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "PersonCompany.use", "Person");
            assertEquals(entry.getKey(), entry.getValue(), data.getMetric("OverallWeightedComplexity"));
        }

        expressions = new HashMap<>();
        expressions.put("self.oclIsKindOf(Person)", 1.0);
        expressions.put("Programmer.allInstances.forAll(oclIsKindOf(Person))", 6.0);
        for (Map.Entry<String, Double> entry : expressions.entrySet()) {
            data = getMetricsData(entry.getKey(), "Inheritance.use", "Programmer");
            assertEquals(entry.getKey(), entry.getValue(), data.getMetric("OverallWeightedComplexity"));
        }
    }


    public void testNewWeights() {
        Log.setDebug(false);

        float step = 0.5f;
        double best = calcExpertScore(false);

        for(float a=0; a < 3.0 ; a+=step) {
            OverallWeightedComplexityMetric.ComplexityCategory.NAVIGATION.weight = a;
            for(float b=0; b< 3.0; b+=step) {
                OverallWeightedComplexityMetric.ComplexityCategory.COLLECTION.weight = b;
                for(float c=0; c< 3.0; c+=step) {
                    OverallWeightedComplexityMetric.ComplexityCategory.ATTRIBUTE.weight = c;
                    for(float d=0; d< 3.0; d+=step) {
                        OverallWeightedComplexityMetric.ComplexityCategory.BOOLEANOPERATIONS.weight = d;
                        for(float e=0; e< 3.0; e+=step) {
                            OverallWeightedComplexityMetric.ComplexityCategory.COMPARISONOPERATIONS.weight = e;
                            double score = calcExpertScore(false);
                            if (score <= best || score <= 44) {
                                best = score;
                                System.out.println("New Score " + score);
                                System.out.println("NAVIGATION " + a + " | COLLECTION " + b + " | ATTRIBUTE " + c + " | BOOLEANOPERATIONS " + d +" | COMPARISONOPERATIONS " + e);
                            }
                        }
                    }
                }
            }
        }

        assert best < 63.f;
        OverallWeightedComplexityMetric.ComplexityCategory.NAVIGATION.weight = 2.5f;
        OverallWeightedComplexityMetric.ComplexityCategory.COLLECTION.weight = 1f;
        OverallWeightedComplexityMetric.ComplexityCategory.BOOLEANOPERATIONS.weight = 1.0f;
        OverallWeightedComplexityMetric.ComplexityCategory.ATTRIBUTE.weight = 0.5f;
        OverallWeightedComplexityMetric.ComplexityCategory.COMPARISONOPERATIONS.weight = 0.5f;
        OverallWeightedComplexityMetric.ComplexityCategory.IF.weight = 0.5f;
        OverallWeightedComplexityMetric.ComplexityCategory.NOTSPECIFIED.weight = 0.5f;

        System.out.println("FINAL: " + calcExpertScore(true));
    }

    public double calcExpertScore(boolean verbose){
        AbstractMetricsData data;
        Hashtable expressions = new Hashtable();


        //context p1,p2:Person inv uniqueName_STRAIGHT:
        expressions.put("Person.allInstances->forAll(p1,p2 | p1<> p2 implies p1.name<> p2.name)", new Object[]{0.0, new Double[]{2.0,1.0,4.0,2.0,2.0       }});

        //context Person inv uniqueName_ISUNIQUE:
        expressions.put("Person.allInstances->forAll(Person.allInstances->isUnique(p | p.name))", new Object[]{0.0, new Double[]{1.0,	2.0,	6.0,	1.0,	1.0,
        }});

        //context p1:Person inv uniqueName_SELECT:
        expressions.put("Person.allInstances->forAll(p1|Person.allInstances->select(p2 | p1 <> p2 and p1.name = p2.name)->isEmpty())",new Object[]{0.0, new Double[]{	3.0,	4.0,	5.0,	3.0,	7.0
        }});

        //context p:Person inv parentsOlderChildren:
        expressions.put("Person.allInstances->forAll(p| p.child->forAll(c | p.yearBirth + 15 < c.yearBirth))",new Object[]{0.0, new Double[]{4.0,	3.0,	3.0,	5.0,	3.0
        }});

        //context gp:Person inv grandparentsMuchOlderGrandchildren:
        expressions.put("Person.allInstances->forAll(gp| gp.child->forAll(p | p.child->forAll(gc |" +
                "gp.yearBirth + 40 < gc.yearBirth)))",new Object[]{0.0, new Double[]{6.0, 8.0, 7.0, 6.0, 6.0}});

        //context p:Person inv noBigamy:
        expressions.put("Person.allInstances->forAll(p| not(p.proposer->notEmpty() and p.accepter -> notEmpty()))",new Object[]{0.0, new Double[]{7.0,	5.0,	1.0,	8.0,	4.0 }});

        //context p:Person inv partnerAboutSameAge:
        expressions.put("Person.allInstances->forAll(p| p.accepter->notEmpty() implies" +
                "(p.yearBirth - p.accepter.yearBirth).abs < 10)",new Object[]{0.0, new Double[]{5.0,	6.0,	2.0,	7.0,	5.0}});

        //context Person inv grandparentExists:
        expressions.put("Person.allInstances->forAll(Person.allInstances->exists(gp, p, gc |" +
                "gp.child->includes(p) and p.child -> includes(gc)))",new Object[]{0.0,new Double[]{8.0,	7.0,	8.0,	4.0,	8.0   }});


        for (Enumeration k = expressions.keys(); k.hasMoreElements();)  {
            String key = String.valueOf(k.nextElement());
            data = getMetricsData(key, "Person.use", "Person");
            ((Object[])expressions.get(key))[0]= data.getMetric("OverallWeightedComplexity");
            if(verbose) {
                System.out.println(key +  " : " + data.getMetric("OverallWeightedComplexity"));
            }
        }

        List<Map.Entry<String, Object[]>> list = new ArrayList<Map.Entry<String, Object[]>>(expressions.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Object[]>>(){
            @Override
            public int compare(Map.Entry<String, Object[]> o1, Map.Entry<String, Object[]> o2) {
                double o1_d = (double) o1.getValue()[0];
                double o2_d = (double) o2.getValue()[0];
                return Double.compare(o1_d, o2_d);
            }
        });

        for(int i = 1; i <= list.size(); i++) {
            Map.Entry<String, Object[]> entry_i = list.get(i - 1 );
            int counter = 0;
            double sum = i;
            while(i + counter < list.size() && ((double)entry_i.getValue()[0]) == ((double)list.get(i + counter ).getValue()[0])) {
                counter++;
                sum += i + counter;
            }
            //entry_i.getValue()[0] = sum / (counter + 1);
            for(int j = i -1; j <= (i - 1 + counter); j++) {
                Map.Entry<String, Object[]> entry_j = list.get(j);
                entry_j.getValue()[0] = sum / (counter + 1);
                //System.out.println(entry_j.getValue()[0]);
            }
            i = i + counter;
        }
        double diff = 0.0;
        for(int i = 0; i < list.size(); i++) {
            Map.Entry<String, Object[]> entry_i = list.get(i);
            for(int j = 0; j< 5; j++) {
                diff += Math.abs(((double) entry_i.getValue()[0]) - ((Double[]) entry_i.getValue()[1])[j]);
            }
       }
        return diff;
    }

    // TODO REMOVE THIS TEST
    public void test_MetricDir() {
        _complexityMetric();
    }

    private void _complexityMetric() {
        MetricsCalculator metricsCalculator = new MetricsCalculator(new VisitorFactory("COMPLEXITY"));
        Set<Metric> res = metricsCalculator.calculateMetricOfDirectory("examples");
        Log.println();
        Log.println("Metrics of the examples directory");
        for(Metric m : res) {
            Log.println(m.toString());
        }
    }

}
