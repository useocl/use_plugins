package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Includes_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->includes(1)";
		String expected = "Int[1] in none ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{}->includes(Undefined)";
		String expected = "Undefined in none ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{1}->includes(1)";
		String expected = "Int[1] in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{1}->includes(2)";
		String expected = "Int[2] in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{1}->includes(Undefined)";
		String expected = "Undefined in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{1,2}->includes(1)";
		String expected = "Int[1] in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{1,2}->includes(3)";
		String expected = "Int[3] in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{1,2}->includes(Undefined)";
		String expected = "Undefined in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{1,2,3}->includes(1)";
		String expected = "Int[1] in ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{1,2,3}->includes(4)";
		String expected = "Int[4] in ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{1,2,3}->includes(Undefined)";
		String expected = "Undefined in ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{Undefined,1}->includes(1)";
		String expected = "Int[1] in ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{Undefined,1}->includes(2)";
		String expected = "Int[2] in ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{Undefined,1}->includes(Undefined)";
		String expected = "Undefined in ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{Undefined,1,2}->includes(1)";
		String expected = "Int[1] in ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{Undefined,1,2}->includes(3)";
		String expected = "Int[3] in ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{Undefined,1,2}->includes(Undefined)";
		String expected = "Undefined in ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{Undefined}->includes(1)";
		String expected = "Int[1] in ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{Undefined}->includes(Undefined)";
		String expected = "Undefined in ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))";
		test("test19", ocl, expected);
	}

	
	@Test
	public void test20() {
		String ocl = "ada.luckyNumbers->includes(1)";
		String expected = "Int[1] in ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) ";
		test("test20", ocl, expected);
	}

	
	@Test
	public void test21() {
		String ocl = "ada.luckyNumbers->includes(Undefined)";
		String expected = "Undefined in ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) ";
		test("test21", ocl, expected);
	}

}
