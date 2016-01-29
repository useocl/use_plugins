package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Count_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->count(1)";
		String expected = "Int[(Int[1] in none) => 1 else 0] ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{}->count(Undefined)";
		String expected = "Int[(Undefined in none) => 1 else 0] ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{1}->count(1)";
		String expected = "Int[(Int[1] in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) => 1 else 0] ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{1}->count(2)";
		String expected = "Int[(Int[2] in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) => 1 else 0] ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{1}->count(Undefined)";
		String expected = "Int[(Undefined in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) => 1 else 0] ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{1,2}->count(1)";
		String expected = "Int[(Int[1] in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) => 1 else 0] ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{1,2}->count(3)";
		String expected = "Int[(Int[3] in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) => 1 else 0] ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{1,2}->count(Undefined)";
		String expected = "Int[(Undefined in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) => 1 else 0] ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{1,2,3}->count(1)";
		String expected = "Int[(Int[1] in ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) => 1 else 0] ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{1,2,3}->count(4)";
		String expected = "Int[(Int[4] in ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) => 1 else 0] ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{1,2,3}->count(Undefined)";
		String expected = "Int[(Undefined in ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))) => 1 else 0] ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{Undefined,1}->count(1)";
		String expected = "Int[(Int[1] in ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) => 1 else 0] ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{Undefined,1}->count(2)";
		String expected = "Int[(Int[2] in ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) => 1 else 0] ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{Undefined,1}->count(Undefined)";
		String expected = "Int[(Undefined in ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]))) => 1 else 0] ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{Undefined,1,2}->count(1)";
		String expected = "Int[(Int[1] in ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) => 1 else 0] ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{Undefined,1,2}->count(3)";
		String expected = "Int[(Int[3] in ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) => 1 else 0] ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{Undefined,1,2}->count(Undefined)";
		String expected = "Int[(Undefined in ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]))) => 1 else 0] ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{Undefined}->count(1)";
		String expected = "Int[(Int[1] in ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => 1 else 0] ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{Undefined}->count(Undefined)";
		String expected = "Int[(Undefined in ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => 1 else 0]";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "ada.luckyNumbers->count(1)";
		String expected = "Int[(Int[1] in ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => 1 else 0] ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "ada.luckyNumbers->count(Undefined)";
		String expected = "Int[(Undefined in ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => 1 else 0] ";
		test("test21", ocl, expected);
	}

}
