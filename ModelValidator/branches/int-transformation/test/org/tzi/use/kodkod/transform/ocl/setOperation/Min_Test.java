package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Min_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->min()";
		String expected = "(none = Undefined_Set || Undefined in none || no none) => Undefined else {i: none | all j: none | int[i] <= int[j]} ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{-2}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2])) || no ((Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2]))) => Undefined else {i: (Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2]) | all j: (Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2]) | int[i] <= int[j]} ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{-2,-1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1])) || no ((Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1]) | all j: (Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1]) | int[i] <= int[j]} ";
		test("test3", ocl, expected);
	}
	
	@Test
	public void test4() {
		String ocl = "Set{-3,-2,-1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1])) || no ((Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1]))) => Undefined else {i: (Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1]) | all j: (Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1]) | int[i] <= int[j]} ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{-2,0}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[0])) || no ((Undefined_Set in (none + Int[-2] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[0]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[0]) | all j: (Undefined_Set in (none + Int[-2] + Int[0]) ) => Undefined_Set else (none + Int[-2] + Int[0]) | int[i] <= int[j]} ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{-2,1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[1])) || no ((Undefined_Set in (none + Int[-2] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[1]) | all j: (Undefined_Set in (none + Int[-2] + Int[1]) ) => Undefined_Set else (none + Int[-2] + Int[1]) | int[i] <= int[j]} ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{-2,0,1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1])) || no ((Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1]) | all j: (Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1]) | int[i] <= int[j]} ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{-2,1,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4])) || no ((Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4]) | all j: (Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4]) | int[i] <= int[j]} ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{-2,0,1,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4])) || no ((Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4]) | all j: (Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4]) | int[i] <= int[j]} ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{-2,-1,0}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0])) || no ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0]) | all j: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0]) | int[i] <= int[j]} ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{-2,-1,1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1])) || no ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1]) | all j: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1]) | int[i] <= int[j]} ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{-2,-1,0,1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1])) || no ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1]) | all j: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1]) | int[i] <= int[j]} ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{-2,-1,1,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4])) || no ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4]) | all j: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4]) | int[i] <= int[j]} ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{-2,-1,0,1,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) || no ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]) | all j: (Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]) | int[i] <= int[j]} ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{-5,-2,-1,0}->min()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) || no ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0]))) => Undefined else {i: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0]) | all j: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0]) | int[i] <= int[j]} ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{-5,-2,-1,1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) || no ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1]) | all j: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1]) | int[i] <= int[j]} ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{-5,-2,-1,0,1}->min()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) || no ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1]) | all j: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1]) | int[i] <= int[j]} ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{-5,-2,-1,1,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) || no ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4]) | all j: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4]) | int[i] <= int[j]} ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{-5,-2,-1,0,1,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) || no ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]) | all j: ( Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4] )) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]) | int[i] <= int[j]} ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{0}->min()";
		String expected = "(((Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0])) || no ((Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0]))) => Undefined else {i: (Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0]) | all j: (Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0]) | int[i] <= int[j]} ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{1}->min()";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) || no ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | all j: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | int[i] <= int[j]} ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{0,1}->min()";
		String expected = "(((Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1])) || no ((Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1]))) => Undefined else {i: (Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1]) | all j: (Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1]) | int[i] <= int[j]} ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{1,2}->min()";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) || no ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) => Undefined else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | all j: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | int[i] <= int[j]} ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{0,1,2}->min()";
		String expected = "(((Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2])) || no ((Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2]))) => Undefined else {i: (Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2]) | all j: (Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2]) | int[i] <= int[j]} ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{1,2,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4])) || no ((Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4]) | all j: (Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4]) | int[i] <= int[j]} ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{0,1,2,4}->min()";
		String expected = "(((Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4])) || no ((Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4]) | all j: (Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4]) | int[i] <= int[j]} ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "Set{Undefined,-2,-1}->min()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1])) || no ((Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1]))) => Undefined else {i: (Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1]) | all j: (Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1]) | int[i] <= int[j]} ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{Undefined,-2,1,4}->min()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4])) || no ((Undefined_Set in (none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4]) | all j: (Undefined_Set in ( none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4]) | int[i] <= int[j]} ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{Undefined,0}->min()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0])) || no ((Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0]))) => Undefined else {i: (Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0]) | all j: (Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0]) | int[i] <= int[j]} ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{Undefined,1,2,4}->min()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4])) || no ((Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4]))) => Undefined else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4]) | all j: (Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4]) | int[i] <= int[j]} ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{Undefined}->min()";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) || no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => Undefined else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | all j: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | int[i] <= int[j]}";
		test("test31", ocl, expected);
	}

	
	@Test
	public void test32() {
		String ocl = "ada.luckyNumbers->min()";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set || Undefined in ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) || no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => Undefined else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | all j: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | int[i] <= int[j]} ";
		test("test32", ocl, expected);
	}

}
