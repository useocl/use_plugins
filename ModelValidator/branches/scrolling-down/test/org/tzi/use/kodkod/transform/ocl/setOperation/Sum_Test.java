package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Sum_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->sum()";
		String expected = "(none = Undefined_Set || Undefined in none) => Undefined else Int[int[none]] ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{-2}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2])) => Undefined_Set else (none + Int[-2])]] ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{-2,-1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-2] + Int[-1])]] ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{-3,-2,-1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-3] + Int[-2] + Int[-1])) => Undefined_Set else (none + Int[-3] + Int[-2] + Int[-1])]] ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{-2,0}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[0]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[0])]] ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{-2,1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[1])]] ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{-2,0,1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1])]] ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{-2,1,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[1] + Int[4])]] ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{-2,0,1,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[0] + Int[1] + Int[4])]] ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{-2,-1,0}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0])]] ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{-2,-1,1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1])]] ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{-2,-1,0,1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1])]] ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{-2,-1,1,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[1] + Int[4])]] ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{-2,-1,0,1,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]) ) => Undefined_Set else (none + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])]] ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{-5,-2,-1,0}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0])]] ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{-5,-2,-1,1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1])]] ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{-5,-2,-1,0,1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] )) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1])]] ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{-5,-2,-1,1,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4] )) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[1] + Int[4])]] ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{-5,-2,-1,0,1,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])) => Undefined_Set else (none + Int[-5] + Int[-2] + Int[-1] + Int[0] + Int[1] + Int[4])]] ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{0}->sum()";
		String expected = "(((Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0]))) => Undefined else Int[int[(Undefined_Set in (none + Int[0])) => Undefined_Set else (none + Int[0])]] ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])]] ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{0,1}->sum()";
		String expected = "(((Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1]))) => Undefined else Int[int[(Undefined_Set in (none + Int[0] + Int[1])) => Undefined_Set else (none + Int[0] + Int[1])]] ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{1,2}->sum()";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]))) => Undefined else Int[int[(Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])]] ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{0,1,2}->sum()";
		String expected = "(((Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2]))) => Undefined else Int[int[(Undefined_Set in (none + Int[0] + Int[1] + Int[2])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2])]] ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{1,2,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[4])]] ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{0,1,2,4}->sum()";
		String expected = "(((Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Int[0] + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Int[0] + Int[1] + Int[2] + Int[4])]] ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "Set{Undefined,-2,-1}->sum()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1]))) => Undefined else Int[int[(Undefined_Set in (none + Undefined + Int[-2] + Int[-1])) => Undefined_Set else (none + Undefined + Int[-2] + Int[-1])]] ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{Undefined,-2,1,4}->sum()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Undefined + Int[-2] + Int[1] + Int[4])) => Undefined_Set else (none + Undefined + Int[-2] + Int[1] + Int[4])]] ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{Undefined,0}->sum()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0]))) => Undefined else Int[int[(Undefined_Set in (none + Undefined + Int[0])) => Undefined_Set else (none + Undefined + Int[0])]] ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{Undefined,1,2,4}->sum()";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4])) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4]))) => Undefined else Int[int[(Undefined_Set in (none + Undefined + Int[1] + Int[2] + Int[4])) => Undefined_Set else (none + Undefined + Int[1] + Int[2] + Int[4])]] ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{Undefined}->sum()";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set || Undefined in ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined))) => Undefined else Int[int[(Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)]]";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "ada.luckyNumbers->sum()";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set || Undefined in ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers))) => Undefined else Int[int[(Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)]] ";
		test("test32", ocl, expected);
	}

}
