package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class ForAll_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->forAll(i | true)";
		String expected = "!(none = Undefined_Set) && (no none || (all i: none | Boolean_True = Boolean_True)) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{}->forAll(i | false)";
		String expected = "!(none = Undefined_Set) && (no none || (all i: none | Boolean_False = Boolean_True)) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{}->forAll(i | i = 1)";
		String expected = "!(none = Undefined_Set) && (all i: none | i = Int[1]) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{}->forAll(i | i <> 1)";
		String expected = "!(none = Undefined_Set) && (all i: none | !(i = Int[1])) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{}->forAll(i | Undefined)";
		String expected = "!(none = Undefined_Set) && (no none || (all i: none | Undefined = Boolean_True)) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{}->forAll(i1,i2 | true)";
		String expected = "!(none = Undefined_Set) && (no none || (all i1: none, i2: none | Boolean_True = Boolean_True)) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{}->forAll(i1,i2 | false)";
		String expected = "!(none = Undefined_Set) && (no none || (all i1: none, i2: none | Boolean_False = Boolean_True)) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{}->forAll(i1,i2 | i1 = i1)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | i1 = i1) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{}->forAll(i1,i2 | i1 = i2)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | i1 = i2) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{}->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | !(i1 = i2)) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{}->forAll(i1,i2 | Undefined)";
		String expected = "!(none = Undefined_Set) && (no none || (all i1: none, i2: none | Undefined = Boolean_True))";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{1}->forAll(i | true)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) || (all i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Boolean_True = Boolean_True)) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{1}->forAll(i | false)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) || (all i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Boolean_False = Boolean_True)) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{1}->forAll(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[1]) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{1}->forAll(i | i = 2)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i = Int[2]) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{1}->forAll(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[1])) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{1}->forAll(i | i <> 2)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i = Int[2])) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{1}->forAll(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) || (all i: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Undefined = Boolean_True)) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{1}->forAll(i1,i2 | true)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) || (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Boolean_True = Boolean_True)) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{1}->forAll(i1,i2 | false)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) || (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Boolean_False = Boolean_True)) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{1}->forAll(i1,i2 | i1 = i1)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i1 = i1) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{1}->forAll(i1,i2 | i1 = i2)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | i1 = i2) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{1}->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i1 = i2)) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{1}->forAll(i1,i2 | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) || (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | Undefined = Boolean_True))";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{1,2}->forAll(i | true)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) || (all i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Boolean_True = Boolean_True)) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{1,2}->forAll(i | false)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) || (all i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Boolean_False = Boolean_True)) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "Set{1,2}->forAll(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[1]) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{1,2}->forAll(i | i = 3)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i = Int[3]) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{1,2}->forAll(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[1])) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{1,2}->forAll(i | i <> 3)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i = Int[3])) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{1,2}->forAll(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) || (all i: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Undefined = Boolean_True)) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "Set{1,2}->forAll(i1,i2 | true)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) || (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Boolean_True = Boolean_True)) ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "Set{1,2}->forAll(i1,i2 | false)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) || (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Boolean_False = Boolean_True)) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "Set{1,2}->forAll(i1,i2 | i1 = i1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i1 = i1) ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "Set{1,2}->forAll(i1,i2 | i1 = i2)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | i1 = i2) ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "Set{1,2}->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i1 = i2)) ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "Set{1,2}->forAll(i1,i2 | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) || (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | Undefined = Boolean_True))";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "Set{1,2,3}->forAll(i | true)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) || (all i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Boolean_True = Boolean_True)) ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "Set{1,2,3}->forAll(i | false)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) || (all i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Boolean_False = Boolean_True)) ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "Set{1,2,3}->forAll(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[1]) ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "Set{1,2,3}->forAll(i | i = 4)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i = Int[4]) ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "Set{1,2,3}->forAll(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[1])) ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "Set{1,2,3}->forAll(i | i <> 4)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i = Int[4])) ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "Set{1,2,3}->forAll(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) || (all i: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Undefined = Boolean_True)) ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | true)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) || (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Boolean_True = Boolean_True)) ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | false)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) || (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Boolean_False = Boolean_True)) ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | i1 = i1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i1 = i1) ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | i1 = i2)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i1 = i2) ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i1 = i2)) ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | i1 = 1 and i2 = 2)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i1 = Int[1] && i2 = Int[2]) ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | i1 = 1 and i2 = 4)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | i1 = Int[1] && i2 = Int[4]) ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "Set{1,2,3}->forAll(i1,i2 | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (no ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) || (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | Undefined = Boolean_True))";
		test("test52", ocl, expected);
	}

	@Test
	public void test53() {
		String ocl = "Set{ada}->forAll(p | p.alive)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (no ((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) || (all p: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | ((p = Undefined) => Undefined else (p . Person_alive)) = Boolean_True))";
		test("test53", ocl, expected);
	}

	@Test
	public void test54() {
		String ocl = "Set{Undefined,1}->forAll(i | true)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) || (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | Boolean_True = Boolean_True)) ";
		test("test54", ocl, expected);
	}

	@Test
	public void test55() {
		String ocl = "Set{Undefined,1}->forAll(i | false)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) || (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | Boolean_False = Boolean_True)) ";
		test("test55", ocl, expected);
	}

	@Test
	public void test56() {
		String ocl = "Set{Undefined,1}->forAll(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[1]) ";
		test("test56", ocl, expected);
	}

	@Test
	public void test57() {
		String ocl = "Set{Undefined,1}->forAll(i | i = 2)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Int[2]) ";
		test("test57", ocl, expected);
	}

	@Test
	public void test58() {
		String ocl = "Set{Undefined,1}->forAll(i | i = Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | i = Undefined) ";
		test("test58", ocl, expected);
	}

	@Test
	public void test59() {
		String ocl = "Set{Undefined,1}->forAll(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[1])) ";
		test("test59", ocl, expected);
	}

	@Test
	public void test60() {
		String ocl = "Set{Undefined,1}->forAll(i | i <> 2)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Int[2])) ";
		test("test60", ocl, expected);
	}

	@Test
	public void test61() {
		String ocl = "Set{Undefined,1}->forAll(i | i <> Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | !(i = Undefined)) ";
		test("test61", ocl, expected);
	}

	@Test
	public void test62() {
		String ocl = "Set{Undefined,1}->forAll(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) || (all i: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) | Undefined = Boolean_True)) ";
		test("test62", ocl, expected);
	}

	@Test
	public void test63() {
		String ocl = "Set{Undefined,1}->forAll(i1,i2 | true)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) || (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1] )) => Undefined_Set else (none + Undefined + Int[1]) | Boolean_True = Boolean_True)) ";
		test("test63", ocl, expected);
	}

	@Test
	public void test64() {
		String ocl = "Set{Undefined,1}->forAll(i1,i2 | false)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) || (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1] )) => Undefined_Set else (none + Undefined + Int[1]) | Boolean_False = Boolean_True)) ";
		test("test64", ocl, expected);
	}

	@Test
	public void test65() {
		String ocl = "Set{Undefined,1}->forAll(i1,i2 | i1 = i1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | i1 = i1) ";
		test("test65", ocl, expected);
	}

	@Test
	public void test66() {
		String ocl = "Set{Undefined,1}->forAll(i1,i2 | i1 = i2)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | i1 = i2) ";
		test("test66", ocl, expected);
	}

	@Test
	public void test67() {
		String ocl = "Set{Undefined,1}->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | !(i1 = i2)) ";
		test("test67", ocl, expected);
	}

	@Test
	public void test68() {
		String ocl = "Set{Undefined,1}->forAll(i1,i2 | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) || (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1] )) => Undefined_Set else (none + Undefined + Int[1]) | Undefined = Boolean_True))";
		test("test68", ocl, expected);
	}

	@Test
	public void test69() {
		String ocl = "Set{Undefined,1,2}->forAll(i | true)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) || (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Boolean_True = Boolean_True)) ";
		test("test69", ocl, expected);
	}

	@Test
	public void test70() {
		String ocl = "Set{Undefined,1,2}->forAll(i | false)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) || (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Boolean_False = Boolean_True)) ";
		test("test70", ocl, expected);
	}

	@Test
	public void test71() {
		String ocl = "Set{Undefined,1,2}->forAll(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[1]) ";
		test("test71", ocl, expected);
	}

	@Test
	public void test72() {
		String ocl = "Set{Undefined,1,2}->forAll(i | i = 3)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Int[3]) ";
		test("test72", ocl, expected);
	}

	@Test
	public void test73() {
		String ocl = "Set{Undefined,1,2}->forAll(i | i = Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i = Undefined) ";
		test("test73", ocl, expected);
	}

	@Test
	public void test74() {
		String ocl = "Set{Undefined,1,2}->forAll(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[1])) ";
		test("test74", ocl, expected);
	}

	@Test
	public void test75() {
		String ocl = "Set{Undefined,1,2}->forAll(i | i <> 3)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Int[3])) ";
		test("test75", ocl, expected);
	}

	@Test
	public void test76() {
		String ocl = "Set{Undefined,1,2}->forAll(i | i <> Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i = Undefined)) ";
		test("test76", ocl, expected);
	}

	@Test
	public void test77() {
		String ocl = "Set{Undefined,1,2}->forAll(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) || (all i: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Undefined = Boolean_True)) ";
		test("test77", ocl, expected);
	}

	@Test
	public void test78() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | true)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) || (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Boolean_True = Boolean_True)) ";
		test("test78", ocl, expected);
	}

	@Test
	public void test79() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | false)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) || (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Boolean_False = Boolean_True)) ";
		test("test79", ocl, expected);
	}

	@Test
	public void test80() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | i1 = i1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i1 = i1) ";
		test("test80", ocl, expected);
	}

	@Test
	public void test81() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | i1 = i2)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i1 = i2) ";
		test("test81", ocl, expected);
	}

	@Test
	public void test82() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i1 = i2)) ";
		test("test82", ocl, expected);
	}

	@Test
	public void test83() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | i1 = 1 and i2 = 2)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i1 = Int[1] && i2 = Int[2]) ";
		test("test83", ocl, expected);
	}

	@Test
	public void test84() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | i1 = 1 and i2 = 3)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i1 = Int[1] && i2 = Int[3]) ";
		test("test84", ocl, expected);
	}

	@Test
	public void test85() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | i1 = 1 and i2 = Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | i1 = Int[1] && i2 = Undefined) ";
		test("test85", ocl, expected);
	}

	@Test
	public void test86() {
		String ocl = "Set{Undefined,1,2}->forAll(i1,i2 | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) || (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | Undefined = Boolean_True))";
		test("test86", ocl, expected);
	}

	@Test
	public void test87() {
		String ocl = "Set{Undefined}->forAll(i | true)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) || (all i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Boolean_True = Boolean_True)) ";
		test("test87", ocl, expected);
	}

	@Test
	public void test88() {
		String ocl = "Set{Undefined}->forAll(i | false)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) || (all i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Boolean_False = Boolean_True)) ";
		test("test88", ocl, expected);
	}

	@Test
	public void test89() {
		String ocl = "Set{Undefined}->forAll(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Int[1]) ";
		test("test89", ocl, expected);
	}

	@Test
	public void test90() {
		String ocl = "Set{Undefined}->forAll(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Int[1])) ";
		test("test90", ocl, expected);
	}

	@Test
	public void test91() {
		String ocl = "Set{Undefined}->forAll(i | i = Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i = Undefined) ";
		test("test91", ocl, expected);
	}

	@Test
	public void test92() {
		String ocl = "Set{Undefined}->forAll(i | i <> Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i = Undefined)) ";
		test("test92", ocl, expected);
	}

	@Test
	public void test93() {
		String ocl = "Set{Undefined}->forAll(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) || (all i: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Undefined = Boolean_True)) ";
		test("test93", ocl, expected);
	}

	@Test
	public void test94() {
		String ocl = "Set{Undefined}->forAll(i1,i2 | true)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) || (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Boolean_True = Boolean_True)) ";
		test("test94", ocl, expected);
	}

	@Test
	public void test95() {
		String ocl = "Set{Undefined}->forAll(i1,i2 | false)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) || (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Boolean_False = Boolean_True)) ";
		test("test95", ocl, expected);
	}

	@Test
	public void test96() {
		String ocl = "Set{Undefined}->forAll(i1,i2 | i1 = i1)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i1 = i1) ";
		test("test96", ocl, expected);
	}

	@Test
	public void test97() {
		String ocl = "Set{Undefined}->forAll(i1,i2 | i1 = i2)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | i1 = i2) ";
		test("test97", ocl, expected);
	}

	@Test
	public void test98() {
		String ocl = "Set{Undefined}->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i1 = i2)) ";
		test("test98", ocl, expected);
	}

	@Test
	public void test99() {
		String ocl = "Set{Undefined}->forAll(i1,i2 | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (no ((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) || (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | Undefined = Boolean_True))";
		test("test99", ocl, expected);
	}

	@Test
	public void test100() {
		String ocl = "ada.luckyNumbers->forAll(i | true)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) || (all i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Boolean_True = Boolean_True)) ";
		test("test100", ocl, expected);
	}

	@Test
	public void test101() {
		String ocl = "ada.luckyNumbers->forAll(i | false)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) || (all i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Boolean_False = Boolean_True)) ";
		test("test101", ocl, expected);
	}

	@Test
	public void test102() {
		String ocl = "ada.luckyNumbers->forAll(i | i = 1)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (all i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Int[1]) ";
		test("test102", ocl, expected);
	}

	@Test
	public void test103() {
		String ocl = "ada.luckyNumbers->forAll(i | i <> 1)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (all i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Int[1])) ";
		test("test103", ocl, expected);
	}

	@Test
	public void test104() {
		String ocl = "ada.luckyNumbers->forAll(i | i = Undefined)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (all i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i = Undefined) ";
		test("test104", ocl, expected);
	}

	@Test
	public void test105() {
		String ocl = "ada.luckyNumbers->forAll(i | i <> Undefined)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (all i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i = Undefined)) ";
		test("test105", ocl, expected);
	}

	@Test
	public void test106() {
		String ocl = "ada.luckyNumbers->forAll(i | Undefined)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) || (all i: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Undefined = Boolean_True)) ";
		test("test106", ocl, expected);
	}

	@Test
	public void test107() {
		String ocl = "ada.luckyNumbers->forAll(i1,i2 | true)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) || (all i1: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), i2: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Boolean_True = Boolean_True)) ";
		test("test107", ocl, expected);
	}

	@Test
	public void test108() {
		String ocl = "ada.luckyNumbers->forAll(i1,i2 | false)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) || (all i1: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), i2: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Boolean_False = Boolean_True)) ";
		test("test108", ocl, expected);
	}

	@Test
	public void test109() {
		String ocl = "ada.luckyNumbers->forAll(i1,i2 | i1 = i1)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), i2: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i1 = i1) ";
		test("test109", ocl, expected);
	}

	@Test
	public void test110() {
		String ocl = "ada.luckyNumbers->forAll(i1,i2 | i1 = i2)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), i2: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | i1 = i2) ";
		test("test110", ocl, expected);
	}

	@Test
	public void test111() {
		String ocl = "ada.luckyNumbers->forAll(i1,i2 | i1 <> i2)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), i2: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | !(i1 = i2)) ";
		test("test111", ocl, expected);
	}

	@Test
	public void test112() {
		String ocl = "ada.luckyNumbers->forAll(i1,i2 | Undefined)";
		String expected = "!(((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) = Undefined_Set) && (no ((Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers)) || (all i1: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers), i2: (Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) | Undefined = Boolean_True))";
		test("test112", ocl, expected);
	}

}
