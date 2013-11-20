package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Reject_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->reject(i | true)";
		String expected = "(none = Undefined_Set) => Undefined_Set else {i: none | !(Boolean_True = Boolean_True)} ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{}->reject(i | false)";
		String expected = "(none = Undefined_Set) => Undefined_Set else {i: none | !(Boolean_False = Boolean_True)} ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{}->reject(i | i = 1)";
		String expected = "(none = Undefined_Set) => Undefined_Set else {i: none | !(i = Int[1])} ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{}->reject(i | i <> 1)";
		String expected = "(none = Undefined_Set) => Undefined_Set else {i: none | !!(i = Int[1])} ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{}->reject(i | Undefined)";
		String expected = "(none = Undefined_Set) => Undefined_Set else {i: none | !(Undefined = Boolean_True)} ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{1}->reject(i | true)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(Boolean_True = Boolean_True)} ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{1}->reject(i | false)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(Boolean_False = Boolean_True)} ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{1}->reject(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[1])} ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{1}->reject(i | i = 2)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[2])} ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{1}->reject(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !!(i = Int[1])} ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{1}->reject(i | i <> 2)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !!(i = Int[2])} ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{1}->reject(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(Undefined = Boolean_True)} ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{1,2}->reject(i | true)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(Boolean_True = Boolean_True)} ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{1,2}->reject(i | false)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(Boolean_False = Boolean_True)} ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{1,2}->reject(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[1])} ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{1,2}->reject(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[3])} ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{1,2}->reject(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !!(i = Int[1])} ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{1,2}->reject(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !!(i = Int[3])} ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{1,2}->reject(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(Undefined = Boolean_True)} ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{1,2,3}->reject(i | true)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(Boolean_True = Boolean_True)} ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{1,2,3}->reject(i | false)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(Boolean_False = Boolean_True)} ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{1,2,3}->reject(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[1])} ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{1,2,3}->reject(i | i = 4)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[4])} ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{1,2,3}->reject(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !!(i = Int[1])} ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{1,2,3}->reject(i | i <> 4)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !!(i = Int[4])} ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{1,2,3}->reject(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(Undefined = Boolean_True)}";
		test("test26", ocl, expected);
	}

	 
	@Test
	public void test27() {
		String ocl = "Set{ada}->select(p | p.alive)";
		String expected = "(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | ((p = Undefined) => Undefined else (p . Person_alive)) = Boolean_True}";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{Undefined,1}->reject(i | true)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(Boolean_True = Boolean_True)} ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{Undefined,1}->reject(i | false)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(Boolean_False = Boolean_True)} ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{Undefined,1}->reject(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[1])} ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{Undefined,1}->reject(i | i = 2)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[2])} ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "Set{Undefined,1}->reject(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Undefined)} ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "Set{Undefined,1}->reject(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !!(i = Int[1])} ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "Set{Undefined,1}->reject(i | i <> 2)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !!(i = Int[2])} ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "Set{Undefined,1}->reject(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !!(i = Undefined)} ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "Set{Undefined,1}->reject(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(Undefined = Boolean_True)} ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "Set{Undefined,1,2}->reject(i | true)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(Boolean_True = Boolean_True)} ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "Set{Undefined,1,2}->reject(i | false)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(Boolean_False = Boolean_True)} ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "Set{Undefined,1,2}->reject(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[1])} ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "Set{Undefined,1,2}->reject(i | i = 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[3])} ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "Set{Undefined,1,2}->reject(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Undefined)} ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "Set{Undefined,1,2}->reject(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !!(i = Int[1])} ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "Set{Undefined,1,2}->reject(i | i <> 3)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !!(i = Int[3])} ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "Set{Undefined,1,2}->reject(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !!(i = Undefined)} ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "Set{Undefined,1,2}->reject(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(Undefined = Boolean_True)} ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "Set{Undefined}->reject(i | true)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(Boolean_True = Boolean_True)} ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "Set{Undefined}->reject(i | false)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(Boolean_False = Boolean_True)} ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "Set{Undefined}->reject(i | i = 1)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Int[1])} ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "Set{Undefined}->reject(i | i <> 1)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !!(i = Int[1])} ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "Set{Undefined}->reject(i | i = Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Undefined)} ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "Set{Undefined}->reject(i | i <> Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !!(i = Undefined)} ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "Set{Undefined}->reject(i | Undefined)";
		String expected = "(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) => Undefined_Set else {i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(Undefined = Boolean_True)}";
		test("test52", ocl, expected);
	}

	 
	@Test
	public void test53() {
		String ocl = "ada.luckyNumbers->reject(i | true)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(Boolean_True = Boolean_True)} ";
		test("test53", ocl, expected);
	}

	 
	@Test
	public void test54() {
		String ocl = "ada.luckyNumbers->reject(i | false)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(Boolean_False = Boolean_True)} ";
		test("test54", ocl, expected);
	}

	 
	@Test
	public void test55() {
		String ocl = "ada.luckyNumbers->reject(i | i = 1)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Int[1])} ";
		test("test55", ocl, expected);
	}

	 
	@Test
	public void test56() {
		String ocl = "ada.luckyNumbers->reject(i | i <> 1)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !!(i = Int[1])} ";
		test("test56", ocl, expected);
	}

	 
	@Test
	public void test57() {
		String ocl = "ada.luckyNumbers->reject(i | i = Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Undefined)} ";
		test("test57", ocl, expected);
	}

	 
	@Test
	public void test58() {
		String ocl = "ada.luckyNumbers->reject(i | i <> Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !!(i = Undefined)} ";
		test("test58", ocl, expected);
	}


	@Test
	public void test59() {
		String ocl = "ada.luckyNumbers->reject(i | Undefined)";
		String expected = "(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) => Undefined_Set else {i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(Undefined = Boolean_True)} ";
		test("test59", ocl, expected);
	}

}
