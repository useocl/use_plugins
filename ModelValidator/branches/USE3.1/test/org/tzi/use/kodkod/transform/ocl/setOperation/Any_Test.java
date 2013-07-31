package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Any_Test extends OCLTest {

	@Test
	public void test1() {
		String ocl = "Set{}->any(i | true)";
		String expected = "(!(none = Undefined_Set) && one none && (some i: none | Boolean_True = Boolean_True)) => none else Undefined ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{}->any(i | false)";
		String expected = "(!(none = Undefined_Set) && one none && (some i: none | Boolean_False = Boolean_True)) => none else Undefined ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{}->any(i | i = 1)";
		String expected = "(none = Undefined_Set || no ((none = Undefined_Set) => Undefined_Set else {i: none | i = Int[1]}) || #((none = Undefined_Set) => Undefined_Set else {i: none | i = Int[1]}) > 1) => Undefined else ((none = Undefined_Set) => Undefined_Set else {i: none | i = Int[1]}) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{}->any(i | i <> 1)";
		String expected = "(none = Undefined_Set || no ((none = Undefined_Set) => Undefined_Set else {i: none | !(i = Int[1])}) || #((none = Undefined_Set) => Undefined_Set else {i: none | !(i = Int[1])}) > 1) => Undefined else ((none = Undefined_Set) => Undefined_Set else {i: none | !(i = Int[1])}) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{}->any(i | Undefined)";
		String expected = "(!(none = Undefined_Set) && one none && (some i: none | Undefined = Boolean_True)) => none else Undefined ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{1}->any(i | true)";
		String expected = "(!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) && (some i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Boolean_True = Boolean_True)) => ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) else Undefined ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{1}->any(i | false)";
		String expected = "(!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) && (some i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Boolean_False = Boolean_True)) => ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) else Undefined ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{1}->any(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[1]}) || #((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[1]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[1]}) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{1}->any(i | i = 2)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[2]}) || #((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[2]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[2]}) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{1}->any(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[1])}) || #((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[1])}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[1])}) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{1}->any(i | i <> 2)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[2])}) || #((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[2])}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[2])}) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{1}->any(i | Undefined)";
		String expected = "(!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) && (some i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Undefined = Boolean_True)) => ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) else Undefined ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{1,2}->any(i | false)";
		String expected = "(!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) && (some i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Boolean_False = Boolean_True)) => ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) else Undefined ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{1,2}->any(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[1]}) || #((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[1]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[1]}) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{1,2}->any(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[3]}) || #((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[3]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[3]}) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{1,2}->any(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[1])}) || #((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[1])}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[1])}) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{1,2}->any(i | Undefined)";
		String expected = "(!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) && (some i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Undefined = Boolean_True)) => ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) else Undefined ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{1,2,3}->any(i | false)";
		String expected = "(!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) && (some i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Boolean_False = Boolean_True)) => ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) else Undefined ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{1,2,3}->any(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[1]}) || #((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[1]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[1]}) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{1,2,3}->any(i | i = 4)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[4]}) || #((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[4]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[4]}) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{1,2,3}->any(i | Undefined)";
		String expected = "(!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) && (some i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Undefined = Boolean_True)) => ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) else Undefined";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{ada}->any(p | p.alive)";
		String expected = "(!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && one ((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) && (some p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | ((p = Undefined) => Undefined else (p . Person_alive)) = Boolean_True)) => ((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) else Undefined";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{Undefined,1}->any(i | false)";
		String expected = "(!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && one ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) && (some i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | Boolean_False = Boolean_True)) => ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) else Undefined ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{Undefined,1}->any(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[1]}) || #((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[1]}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[1]}) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{Undefined,1}->any(i | i = 2)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[2]}) || #((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[2]}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[2]}) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{Undefined,1}->any(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Undefined}) || #((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Undefined}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Undefined}) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "Set{Undefined,1}->any(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[1])}) || #((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[1])}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[1])}) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{Undefined,1}->any(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Undefined)}) || #((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Undefined)}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Undefined)}) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{Undefined,1}->any(i | Undefined)";
		String expected = "(!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && one ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) && (some i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | Undefined = Boolean_True)) => ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) else Undefined ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{Undefined,1,2}->any(i | false)";
		String expected = "(!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && one ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) && (some i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Boolean_False = Boolean_True)) => ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) else Undefined ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{Undefined,1,2}->any(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[1]}) || #((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[1]}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[1]}) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "Set{Undefined,1,2}->any(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[3]}) || #((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[3]}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[3]}) ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "Set{Undefined,1,2}->any(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Undefined}) || #((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Undefined}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Undefined}) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "Set{Undefined,1,2}->any(i | Undefined)";
		String expected = "(!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && one ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) && (some i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Undefined = Boolean_True)) => ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) else Undefined ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "Set{Undefined}->any(i | true)";
		String expected = "(!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && one ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) && (some i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Boolean_True = Boolean_True)) => ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) else Undefined ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "Set{Undefined}->any(i | false)";
		String expected = "(!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && one ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) && (some i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Boolean_False = Boolean_True)) => ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) else Undefined ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "Set{Undefined}->any(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set || no ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Int[1]}) || #((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Int[1]}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Int[1]}) ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "Set{Undefined}->any(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set || no ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Undefined}) || #((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Undefined}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Undefined}) ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "Set{Undefined}->any(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set || no ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Int[1])}) || #((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Int[1])}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Int[1])}) ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "Set{Undefined}->any(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set || no ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Undefined)}) || #((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Undefined)}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Undefined)}) ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "Set{Undefined}->any(i | Undefined)";
		String expected = "(!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && one ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) && (some i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Undefined = Boolean_True)) => ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) else Undefined";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "ada.luckyNumbers->any(i | true)";
		String expected = "(!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && one ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) && (some i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Boolean_True = Boolean_True)) => ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) else Undefined ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "ada.luckyNumbers->any(i | false)";
		String expected = "(!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && one ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) && (some i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Boolean_False = Boolean_True)) => ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) else Undefined ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "ada.luckyNumbers->any(i | i = 1)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set || no ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Int[1]}) || #((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Int[1]}) > 1) => Undefined else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Int[1]}) ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "ada.luckyNumbers->any(i | i <> 1)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set || no ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Int[1])}) || #((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Int[1])}) > 1) => Undefined else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Int[1])}) ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "ada.luckyNumbers->any(i | i = Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set || no ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Undefined}) || #((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Undefined}) > 1) => Undefined else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Undefined}) ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "ada.luckyNumbers->any(i | i <> Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set || no ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Undefined)}) || #((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Undefined)}) > 1) => Undefined else ((((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Undefined)}) ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "ada.luckyNumbers->any(i | Undefined)";
		String expected = "(!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && one ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) && (some i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Undefined = Boolean_True)) => ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) else Undefined ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "Set{1,2,3}->select(i | Set{2,3,4}->any(j | j = 2) <> i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set || no ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = Int[2]}) || #((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = Int[2]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = Int[2]})) = i)} ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "Set{1,2,3}->select(i | Set{2,3,4}->any(j | j = 2) = i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set || no ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = Int[2]}) || #((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = Int[2]}) > 1) => Undefined else ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = Int[2]})) = i} ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "Set{1,2,3}->select(i | Set{2,3,4}->any(j | j = i) <> i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set || no ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = i}) || #((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = i}) > 1) => Undefined else ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = i})) = i)} ";
		test("test53", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "Set{1,2,3}->select(i | Set{2,3,4}->any(j | j = i) = i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set || no ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = i}) || #((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = i}) > 1) => Undefined else ((((Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {j: (Undefined_Set in (none + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[2] + Int[3] + Int[4]) | j = i})) = i} ";
		test("test54", ocl, expected);
	}


	@Test
	public void test53() {
		String ocl = "Set{1,2}->any(i | true)";
		String expected = "(!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) && (some i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Boolean_True = Boolean_True)) => ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) else Undefined ";
		test("test57", ocl, expected);
	}

	@Test
	public void test54() {
		String ocl = "Set{1,2}->any(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[3])}) || #((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[3])}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[3])}) ";
		test("test58", ocl, expected);
	}

	@Test
	public void test55() {
		String ocl = "Set{1,2,3}->any(i | true)";
		String expected = "(!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && one ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) && (some i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Boolean_True = Boolean_True)) => ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) else Undefined ";
		test("test59", ocl, expected);
	}

	@Test
	public void test56() {
		String ocl = "Set{1,2,3}->any(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[1])}) || #((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[1])}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[1])}) ";
		test("test60", ocl, expected);
	}

	@Test
	public void test57() {
		String ocl = "Set{1,2,3}->any(i | i <> 4)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set || no ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[4])}) || #((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[4])}) > 1) => Undefined else ((((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[4])}) ";
		test("test61", ocl, expected);
	}

	@Test
	public void test58() {
		String ocl = "Set{Undefined,1}->any(i | true)";
		String expected = "(!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && one ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) && (some i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | Boolean_True = Boolean_True)) => ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) else Undefined ";
		test("test62", ocl, expected);
	}

	@Test
	public void test59() {
		String ocl = "Set{Undefined,1}->any(i | i <> 2)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[2])}) || #((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[2])}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[2])}) ";
		test("test63", ocl, expected);
	}

	@Test
	public void test60() {
		String ocl = "Set{Undefined,1,2}->any(i | true)";
		String expected = "(!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && one ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) && (some i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Boolean_True = Boolean_True)) => ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) else Undefined ";
		test("test64", ocl, expected);
	}

	@Test
	public void test61() {
		String ocl = "Set{Undefined,1,2}->any(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[1])}) || #((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[1])}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[1])}) ";
		test("test65", ocl, expected);
	}

	@Test
	public void test62() {
		String ocl = "Set{Undefined,1,2}->any(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[3])}) || #((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[3])}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[3])}) ";
		test("test66", ocl, expected);
	}

	@Test
	public void test63() {
		String ocl = "Set{Undefined,1,2}->any(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set || no ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Undefined)}) || #((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Undefined)}) > 1) => Undefined else ((((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Undefined)}) ";
		test("test67", ocl, expected);
	}

}
