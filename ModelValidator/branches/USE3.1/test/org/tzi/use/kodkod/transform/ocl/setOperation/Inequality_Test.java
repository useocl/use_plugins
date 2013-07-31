package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Inequality_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{} <> Set{}";
		String expected = "!(none = none) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{} <> Set{1}";
		String expected = "!(none = ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{} <> Set{1,2}";
		String expected = "!(none = ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{1} <> Set{}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = none) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{1,2} <> Set{}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = none) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{1} <> Set{1}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{1} <> Set{2}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Int[2])) => Undefined_Set else (none + Int[2])))";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{1} <> Set{1,2}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{1} <> Set{2,3}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Int[2] + Int[3])) => Undefined_Set else (none + Int[2] + Int[3]))) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{1,2} <> Set{1}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{1,2} <> Set{3}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[3])) => Undefined_Set else (none + Int[3]))) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{1,2} <> Set{1,2}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{1,2} <> Set{1,3}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[1] + Int[3])) => Undefined_Set else (none + Int[1] + Int[3]))) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{1,2} <> Set{3,4}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[3] + Int[4])) => Undefined_Set else (none + Int[3] + Int[4]))) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{1,2} <> Set{1,2,3}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{1,2} <> Set{1,3,4}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[1] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[3] + Int[4]))) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{1,2} <> Set{3,4,5}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Int[3] + Int[4] + Int[5])) => Undefined_Set else (none + Int[3] + Int[4] + Int[5]))) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{} <> Set{Undefined}";
		String expected = "!(none = ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{} <> Set{Undefined,1}";
		String expected = "!(none = ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{} <> Set{Undefined,1,2}";
		String expected = "!(none = ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{Undefined} <> Set{}";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = none) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{Undefined,1} <> Set{}";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = none) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{Undefined,1,2} <> Set{}";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = none) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{1} <> Set{Undefined}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{1} <> Set{Undefined,1}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{1} <> Set{Undefined,2}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Undefined + Int[2])) => Undefined_Set else (none + Undefined + Int[2]))) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "Set{1} <> Set{Undefined,1,2}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{1} <> Set{Undefined,2,3}";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Undefined_Set in (none + Undefined + Int[2] + Int[3])) => Undefined_Set else (none + Undefined + Int[2] + Int[3]))) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{1,2} <> Set{Undefined}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{1,2} <> Set{Undefined,1}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{1,2} <> Set{Undefined,3}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Undefined + Int[3])) => Undefined_Set else (none + Undefined + Int[3]))) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "Set{1,2} <> Set{Undefined,1,2}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "Set{1,2} <> Set{Undefined,1,3}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Undefined + Int[1] + Int[3])) => Undefined_Set else (none + Undefined + Int[1] + Int[3]))) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "Set{1,2} <> Set{Undefined,3,4}";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Undefined_Set in (none + Undefined + Int[3] + Int[4])) => Undefined_Set else (none + Undefined + Int[3] + Int[4]))) ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "Set{Undefined} <> Set{Undefined}";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "Set{Undefined} <> Set{Undefined,1}";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "Set{Undefined} <> Set{Undefined,1,2}";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((Undefined_Set in (none + Undefined + Int[1] + Int[2]) ) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "Set{Undefined,1} <> Set{Undefined}";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "Set{Undefined,1,2} <> Set{Undefined}";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = ((Undefined_Set in (none + Undefined) ) => Undefined_Set else (none + Undefined)))";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "ada.luckyNumbers <> ada.luckyNumbers ";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "ada.luckyNumbers <> Set{}";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = none) ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "ada.luckyNumbers <> Set{1}";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "ada.luckyNumbers <> Set{1,2}";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "ada.luckyNumbers <> Set{Undefined}";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "ada.luckyNumbers <> Set{Undefined,1}";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "ada.luckyNumbers <> Set{Undefined,1,2}";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "Set{} <> ada.luckyNumbers";
		String expected = "!(none = ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "Set{1} <> ada.luckyNumbers";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "Set{1,2} <> ada.luckyNumbers";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "Set{Undefined} <> ada.luckyNumbers";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "Set{Undefined,1} <> ada.luckyNumbers";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "Set{Undefined,1,2} <> ada.luckyNumbers";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) ";
		test("test52", ocl, expected);
	}

}
