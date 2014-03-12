package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Collect_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->collect(i | 1)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: Int[1] | true}) - Undefined_Set) < #(univ . {i: none, res: Int[1] | true})) => (((univ . {i: none, res: Int[1] | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: Int[1] | true})) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{}->collect(i | true)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: Boolean_True | true}) - Undefined_Set) < #(univ . {i: none, res: Boolean_True | true})) => (((univ . {i: none, res: Boolean_True | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: Boolean_True | true})) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{}->collect(i | false)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: Boolean_False | true}) - Undefined_Set) < #(univ . { i: none, res: Boolean_False | true})) => (((univ . {i: none, res: Boolean_False | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: Boolean_False | true})) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{}->collect(i | ada)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: Person_ada | true}) - Undefined_Set) < #(univ . {i: none, res: Person_ada | true})) => (((univ . {i: none, res: Person_ada | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: Person_ada | true})) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{}->collect(i | Undefined)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: Undefined | true}) - Undefined_Set) < #(univ . {i: none, res: Undefined | true})) => (((univ . {i: none, res: Undefined | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: Undefined | true})) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{}->collect(i | i)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: i | true}) - Undefined_Set) < #(univ . {i: none, res: i | true})) => (((univ . {i: none, res: i | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: i | true})) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{}->collect(i | i * i)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) < #(univ . {i: none, res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) => (((univ . {i: none, res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{}->collect(i | Set{5}->including(i))";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((#((univ . {i: none, res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) < #(univ . {i: none, res: ((( Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) => (((univ . {i: none, res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) + Undefined) else (univ . {i: none, res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{}->collect(i | i = 3)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((no none) => none else ((none = ((none = Undefined_Set) => Undefined_Set else {i: none | i = Int[3]})) => Boolean_True else ((some ((none = Undefined_Set) => Undefined_Set else {i: none | i = Int[3]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{}->collect(i | i <> 3)";
		String expected = "(none = Undefined_Set) => Undefined_Set else ((no none) => none else ((none = ((none = Undefined_Set) => Undefined_Set else {i: none | !(i = Int[3])})) => Boolean_True else ((some ((none = Undefined_Set) => Undefined_Set else {i: none | !(i = Int[3])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{3}->collect(i | 1)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Int[1] | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Int[1] | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Int[1] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Int[1] | true})) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{3}->collect(i | true)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_True | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_True | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_True | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_True | true})) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{3}->collect(i | false)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_False | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_False | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_False | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Boolean_False | true})) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{3}->collect(i | ada)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Person_ada | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Person_ada | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Person_ada | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Person_ada | true})) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{3}->collect(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Undefined | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Undefined | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Undefined | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: Undefined | true})) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{3}->collect(i | i)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: i | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: i | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: i | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: i | true})) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{3}->collect(i | i * i)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{3}->collect(i | Set{5}->including(i))";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) => (((univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{3}->collect(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]))) => none else ((((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = ((((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]) | i = Int[3]})) => Boolean_True else ((some ((((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]) | i = Int[3]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{3}->collect(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]))) => none else ((((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = ((((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]) | !(i = Int[3])})) => Boolean_True else ((some ((((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]) | !(i = Int[3])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{1,2,3}->collect(i | 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Int[1] | true}) - Undefined_Set) < #( univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Int[1] | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Int[1] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Int[1] | true})) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{1,2,3}->collect(i | true)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_True | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_True | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_True | true}) - Undefined_Set ) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_True | true})) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{1,2,3}->collect(i | false)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_False | true}) - Undefined_Set ) < #(univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_False | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_False | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Boolean_False | true})) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{1,2,3}->collect(i | ada)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Person_ada | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Person_ada | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Person_ada | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Person_ada | true})) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{1,2,3}->collect(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Undefined | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Undefined | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Undefined | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: Undefined | true})) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{1,2,3}->collect(i | i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: i | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: i | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: i | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: i | true})) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "Set{1,2,3}->collect(i | i * i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{1,2,3}->collect(i | Set{5}->including(i))";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) => (((univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{1,2,3}->collect(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[3]})) => Boolean_True else ((some ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[3]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{1,2,3}->collect(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[3])})) => Boolean_True else ((some ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[3])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{1,2,3}->collect(i | i = 4)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[4]})) => Boolean_True else ((some ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[4]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "Set{1,2,3}->collect(i | i <> 4)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[4])})) => Boolean_True else ((some ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[4])})) => (Boolean_True + Boolean_False) else Boolean_False)))";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "Set{ada}->collect(p | p.alive)";
		String expected = "(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Person_alive) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Person_alive) | true})) => (((univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Person_alive) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Person_alive) | true})) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "Set{Undefined,2}->collect(i | 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Int[1] | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Int[1] | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Int[1] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Int[1] | true})) ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "Set{Undefined,2}->collect(i | true)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_True | true}) - Undefined_Set) < #( univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_True | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_True | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_True | true})) ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "Set{Undefined,2}->collect(i | false)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_False | true}) - Undefined_Set) < #( univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_False | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_False | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Boolean_False | true})) ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "Set{Undefined,2}->collect(i | ada)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Person_ada | true}) - Undefined_Set) < #( univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Person_ada | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Person_ada | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Person_ada | true})) ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "Set{Undefined,2}->collect(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Undefined | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Undefined | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Undefined | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: Undefined | true})) ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "Set{Undefined,2}->collect(i | i)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: i | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: i | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: i | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: i | true})) ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "Set{Undefined,2}->collect(i | i * i)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "Set{Undefined,2}->collect(i | Set{5}->including(i))";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "Set{Undefined,2}->collect(i | i = 2)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]))) => none else ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | i = Int[2]})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | i = Int[2]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "Set{Undefined,2}->collect(i | i <> 2)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]))) => none else ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | !(i = Int[2])})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | !(i = Int[2])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "Set{Undefined,2}->collect(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]))) => none else ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | i = Int[3]})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | i = Int[3]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "Set{Undefined,2}->collect(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]))) => none else ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | !(i = Int[3])})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | !(i = Int[3])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "Set{Undefined,2}->collect(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]))) => none else ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | i = Undefined})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | i = Undefined})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "Set{Undefined,2}->collect(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]))) => none else ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | !(i = Undefined)})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]) | !(i = Undefined)})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "Set{Undefined,2,3}->collect(i | 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Int[1] | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Int[1] | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Int[1] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Int[1] | true})) ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "Set{Undefined,2,3}->collect(i | true)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_True | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_True | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_True | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_True | true})) ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "Set{Undefined,2,3}->collect(i | false)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_False | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_False | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_False | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Boolean_False | true})) ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "Set{Undefined,2,3}->collect(i | ada)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Person_ada | true}) - Undefined_Set ) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Person_ada | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Person_ada | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Person_ada | true})) ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "Set{Undefined,2,3}->collect(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Undefined | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Undefined | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Undefined | true}) - Undefined_Set ) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: Undefined | true})) ";
		test("test52", ocl, expected);
	}

	@Test
	public void test53() {
		String ocl = "Set{Undefined,2,3}->collect(i | i)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: i | true}) - Undefined_Set) < #( univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: i | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: i | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: i | true})) ";
		test("test53", ocl, expected);
	}

	@Test
	public void test54() {
		String ocl = "Set{Undefined,2,3}->collect(i | i * i)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) ";
		test("test54", ocl, expected);
	}

	@Test
	public void test55() {
		String ocl = "Set{Undefined,2,3}->collect(i | Set{5}->including(i))";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5]) ) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5]) ) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) => (((univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5] )) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]), res: (((Undefined_Set in (none + Int[5] )) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) ";
		test("test55", ocl, expected);
	}

	@Test
	public void test56() {
		String ocl = "Set{Undefined,2,3}->collect(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | i = Int[3]})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | i = Int[3]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test56", ocl, expected);
	}

	@Test
	public void test57() {
		String ocl = "Set{Undefined,2,3}->collect(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | !(i = Int[3])})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | !(i = Int[3])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test57", ocl, expected);
	}

	@Test
	public void test58() {
		String ocl = "Set{Undefined,2,3}->collect(i | i = 4)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | i = Int[4]})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | i = Int[4]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test58", ocl, expected);
	}

	@Test
	public void test59() {
		String ocl = "Set{Undefined,2,3}->collect(i | i <> 4)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | !(i = Int[4])})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | !(i = Int[4])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test59", ocl, expected);
	}

	@Test
	public void test60() {
		String ocl = "Set{Undefined,2,3}->collect(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | i = Undefined})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | i = Undefined})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test60", ocl, expected);
	}

	@Test
	public void test61() {
		String ocl = "Set{Undefined,2,3}->collect(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]))) => none else ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | !(i = Undefined)})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]) | !(i = Undefined)})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test61", ocl, expected);
	}

	@Test
	public void test62() {
		String ocl = "Set{Undefined}->collect(i | 1)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Int[1] | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Int[1] | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Int[1] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Int[1] | true})) ";
		test("test62", ocl, expected);
	}

	@Test
	public void test63() {
		String ocl = "Set{Undefined}->collect(i | true)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_True | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_True | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_True | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_True | true})) ";
		test("test63", ocl, expected);
	}

	@Test
	public void test64() {
		String ocl = "Set{Undefined}->collect(i | false)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_False | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_False | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_False | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Boolean_False | true})) ";
		test("test64", ocl, expected);
	}

	@Test
	public void test65() {
		String ocl = "Set{Undefined}->collect(i | ada)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Person_ada | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Person_ada | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Person_ada | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Person_ada | true})) ";
		test("test65", ocl, expected);
	}

	@Test
	public void test66() {
		String ocl = "Set{Undefined}->collect(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Undefined | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Undefined | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Undefined | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: Undefined | true})) ";
		test("test66", ocl, expected);
	}

	@Test
	public void test67() {
		String ocl = "Set{Undefined}->collect(i | i)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: i | true}) - Undefined_Set) < #(univ . {i: ( Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: i | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: i | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: i | true})) ";
		test("test67", ocl, expected);
	}

	@Test
	public void test68() {
		String ocl = "Set{Undefined}->collect(i | i * i)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) ";
		test("test68", ocl, expected);
	}

	@Test
	public void test69() {
		String ocl = "Set{Undefined}->collect(i | Set{5}->including(i))";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) < #(univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) => (((univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) + Undefined) else (univ . {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) ";
		test("test69", ocl, expected);
	}

	@Test
	public void test70() {
		String ocl = "Set{Undefined}->collect(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => none else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Int[3]})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Int[3]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test70", ocl, expected);
	}

	@Test
	public void test71() {
		String ocl = "Set{Undefined}->collect(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => none else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Int[3])})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Int[3])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test71", ocl, expected);
	}

	@Test
	public void test72() {
		String ocl = "Set{Undefined}->collect(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => none else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Undefined})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Undefined})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test72", ocl, expected);
	}

	@Test
	public void test73() {
		String ocl = "Set{Undefined}->collect(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else ((no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => none else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Undefined)})) => Boolean_True else ((some ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Undefined)})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test73", ocl, expected);
	}

	@Test
	public void test74() {
		String ocl = "ada.luckyNumbers->collect(i | 1)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Int[1] | true}) - Undefined_Set) < #( univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Int[1] | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Int[1] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Int[1] | true})) ";
		test("test74", ocl, expected);
	}

	@Test
	public void test75() {
		String ocl = "ada.luckyNumbers->collect(i | true)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_True | true}) - Undefined_Set ) < #(univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_True | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_True | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_True | true})) ";
		test("test75", ocl, expected);
	}

	@Test
	public void test76() {
		String ocl = "ada.luckyNumbers->collect(i | false)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_False | true}) - Undefined_Set) < #(univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_False | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_False | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Boolean_False | true})) ";
		test("test76", ocl, expected);
	}

	@Test
	public void test77() {
		String ocl = "ada.luckyNumbers->collect(i | ada)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Person_ada | true}) - Undefined_Set) < #(univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Person_ada | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Person_ada | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Person_ada | true})) ";
		test("test77", ocl, expected);
	}

	@Test
	public void test78() {
		String ocl = "ada.luckyNumbers->collect(i | Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Undefined | true}) - Undefined_Set) < #(univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Undefined | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Undefined | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: Undefined | true})) ";
		test("test78", ocl, expected);
	}

	@Test
	public void test79() {
		String ocl = "ada.luckyNumbers->collect(i | i)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: i | true}) - Undefined_Set) < #(univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: i | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: i | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: i | true})) ";
		test("test79", ocl, expected);
	}

	@Test
	public void test80() {
		String ocl = "ada.luckyNumbers->collect(i | i * i)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) < #(univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (i = Undefined || i = Undefined) => Undefined else Int[int[i] * int[i]] | true})) ";
		test("test80", ocl, expected);
	}

	@Test
	public void test81() {
		String ocl = "ada.luckyNumbers->collect(i | Set{5}->including(i))";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((#((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) < #(univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) => (((univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true}) - Undefined_Set) + Undefined) else (univ . {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), res: (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[5])) => Undefined_Set else (none + Int[5])) + i) | true})) ";
		test("test81", ocl, expected);
	}

	@Test
	public void test82() {
		String ocl = "ada.luckyNumbers->collect(i | i = 3)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => none else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Int[3]})) => Boolean_True else ((some ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Int[3]})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test82", ocl, expected);
	}

	@Test
	public void test83() {
		String ocl = "ada.luckyNumbers->collect(i | i <> 3)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => none else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Int[3])})) => Boolean_True else ((some ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Int[3])})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test83", ocl, expected);
	}

	@Test
	public void test84() {
		String ocl = "ada.luckyNumbers->collect(i | i = Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => none else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Undefined})) => Boolean_True else ((some ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Undefined})) => (Boolean_True + Boolean_False) else Boolean_False))) ";
		test("test84", ocl, expected);
	}

	@Test
	public void test85() {
		String ocl = "ada.luckyNumbers->collect(i | i <> Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else ((no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => none else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Undefined)})) => Boolean_True else ((some ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Undefined)})) => (Boolean_True + Boolean_False) else Boolean_False)))";
		test("test85", ocl, expected);
	}

	@Test
	public void test86() {
		String ocl = "ibm.employeeA->collect(p | p.employerA)";
		String expected = "(((Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test86", ocl, expected);
	}

	@Test
	public void test87() {
		String ocl = "ibm.employeeA->collect(p | p.childA)";
		String expected = "(((Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test87", ocl, expected);
	}

	@Test
	public void test88() {
		String ocl = "Set{ada}->collect(p | p.employerA)";
		String expected = "(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test88", ocl, expected);
	}

	@Test
	public void test89() {
		String ocl = "Set{bob}->collect(p | p.employerA)";
		String expected = "(((Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test89", ocl, expected);
	}

	@Test
	public void test90() {
		String ocl = "Set{ada}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test90", ocl, expected);
	}

	@Test
	public void test91() {
		String ocl = "Set{bob}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_bob)) => Undefined_Set else (none + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test91", ocl, expected);
	}

	@Test
	public void test92() {
		String ocl = "Set{cyd}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Person_cyd)) => Undefined_Set else (none + Person_cyd)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_cyd)) => Undefined_Set else (none + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Person_cyd)) => Undefined_Set else (none + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_cyd)) => Undefined_Set else (none + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_cyd)) => Undefined_Set else (none + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test92", ocl, expected);
	}

	@Test
	public void test93() {
		String ocl = "Set{ada,bob}->collect(p | p.employerA)";
		String expected = "(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test93", ocl, expected);
	}

	@Test
	public void test94() {
		String ocl = "Set{ada,bob}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test94", ocl, expected);
	}

	@Test
	public void test95() {
		String ocl = "Set{ada,bob,cyd}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd) ) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test95", ocl, expected);
	}

	@Test
	public void test96() {
		String ocl = "Set{Undefined,ada}->collect(p | p.employerA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test96", ocl, expected);
	}

	@Test
	public void test97() {
		String ocl = "Set{Undefined,bob}->collect(p | p.employerA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test97", ocl, expected);
	}

	@Test
	public void test98() {
		String ocl = "Set{Undefined,ada}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test98", ocl, expected);
	}

	@Test
	public void test99() {
		String ocl = "Set{Undefined,bob}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_bob)) => Undefined_Set else (none + Undefined + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test99", ocl, expected);
	}

	@Test
	public void test100() {
		String ocl = "Set{Undefined,cyd}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_cyd)) => Undefined_Set else (none + Undefined + Person_cyd)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_cyd)) => Undefined_Set else (none + Undefined + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Undefined + Person_cyd)) => Undefined_Set else (none + Undefined + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_cyd)) => Undefined_Set else (none + Undefined + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_cyd)) => Undefined_Set else (none + Undefined + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test100", ocl, expected);
	}

	@Test
	public void test101() {
		String ocl = "Set{Undefined,ada,bob}->collect(p | p.employerA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test101", ocl, expected);
	}

	@Test
	public void test102() {
		String ocl = "Set{Undefined,ada,bob}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test102", ocl, expected);
	}

	@Test
	public void test103() {
		String ocl = "Set{Undefined,ada,bob,cyd}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + Undefined + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob + Person_cyd), res: (p = Undefined ) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + Undefined + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob + Person_cyd), res: (p = Undefined ) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + Undefined + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob + Person_cyd), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test103", ocl, expected);
	}

	@Test
	public void test104() {
		String ocl = "Set{uf.person}->collect(p | p.employerA)";
		String expected = "(((Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined else (p . Job_A) | true}))";
		test("test104", ocl, expected);
	}

	@Test
	public void test105() {
		String ocl = "Set{uf.person}->collect(p | p.childA)";
		String expected = "(((Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (Undefined_Set in ( none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (Undefined_Set in (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) => Undefined_Set else (none + ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}))";
		test("test105", ocl, expected);
	}

	@Test
	public void test106() {
		String ocl = "bob.employerA.employeeA->collect(p | p.employerA)";
		String expected = "(((((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)))) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) < #(univ . {p: (((Person_bob = Undefined ) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined else (p . Job_A) | true})) => (((univ . {p: (((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined else (p . Job_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined else (p . Job_A) | true})) ";
		test("test106", ocl, expected);
	}

	@Test
	public void test107() {
		String ocl = "bob.employerA.employeeA->collect(p | p.childA)";
		String expected = "(((((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)))) = Undefined_Set) => Undefined_Set else ((#((univ . {p: (((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) < #(univ . {p: (((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) => (((univ . {p: (((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true}) - Undefined_Set) + Undefined) else (univ . {p: (((Person_bob = Undefined) => Undefined else (Person_bob . Job_A)) = Undefined) => Undefined_Set else (Job_A . ((Person_bob = Undefined) => Undefined else (Person_bob . Job_A))), res: (p = Undefined) => Undefined_Set else (p . Parent_A) | true})) ";
		test("test107", ocl, expected);
	}

}
