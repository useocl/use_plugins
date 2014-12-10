package org.tzi.use.kodkod.transform.ocl.setConstructor;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class SetConstructor_Test extends OCLTest {

	@Test
	public void test1() {
		String ocl = "Set{}";
		String expected = "none ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{1}";
		String expected = "(Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{1,2,3}";
		String expected = "(Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{1,true}";
		String expected = "(Undefined_Set in (none + Int[1] + Boolean_True)) => Undefined_Set else (none + Int[1] + Boolean_True) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{1,2,true,'Ada'}";
		String expected = "(Undefined_Set in (none + Int[1] + Int[2] + Boolean_True + String_Ada)) => Undefined_Set else (none + Int[1] + Int[2] + Boolean_True + String_Ada) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{1..9}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[9]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[9]]})) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{9..1}";
		String expected = "(Undefined_Set in (none + ((Int[9] = Undefined || Int[1] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[9]] && int[i] <= int[Int[1]]}))) => Undefined_Set else (none + ((Int[9] = Undefined || Int[1] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[9]] && int[i] <= int[Int[1]]})) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{-3..3}";
		String expected = "(Undefined_Set in (none + ((Int[-3] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[-3]] && int[i] <= int[Int[3]]}))) => Undefined_Set else (none + ((Int[-3] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[-3]] && int[i] <= int[Int[3]]})) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{3..-3}";
		String expected = "(Undefined_Set in (none + ((Int[3] = Undefined || Int[-3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[3]] && int[i] <= int[Int[-3]]}))) => Undefined_Set else (none + ((Int[3] = Undefined || Int[-3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[3]] && int[i] <= int[Int[-3]]})) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{1..1}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[1] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[1]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[1] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[1]]})) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{1..3, 7..9}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[7] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[7]] && int[i] <= int[Int[9]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[7] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[7]] && int[i] <= int[Int[9]]})) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{1..3, 9..4, 7..9}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[9] = Undefined || Int[4] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[9]] && int[i] <= int[Int[4]]}) + ((Int[7] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[7]] && int[i] <= int[Int[9]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[9] = Undefined || Int[4] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[9]] && int[i] <= int[Int[4]]}) + ((Int[7] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[7]] && int[i] <= int[Int[9]]})) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{1..3, 5}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[5] = Undefined || Int[5] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[5]] && int[i] <= int[Int[5]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[5] = Undefined || Int[5] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[5]] && int[i] <= int[Int[5]]}))";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{1..3, 5, 7..9}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[5] = Undefined || Int[5] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[5]] && int[i] <= int[Int[5]]}) + ((Int[7] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[7]] && int[i] <= int[Int[9]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((Int[5] = Undefined || Int[5] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[5]] && int[i] <= int[Int[5]]}) + ((Int[7] = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[7]] && int[i] <= int[Int[9]]}))";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{1+2}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]])) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{1+2, 3+4}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) + ((Int[3] = Undefined || Int[4] = Undefined) => Undefined else Int[int[Int[3]] + int[Int[4]]]))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) + ((Int[3] = Undefined || Int[4] = Undefined) => Undefined else Int[int[Int[3]] + int[Int[4]]])) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{1+2, ada.name}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) + ((Person_ada = Undefined) => Undefined else (Person_ada . Person_name)))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) + ((Person_ada = Undefined) => Undefined else (Person_ada . Person_name))) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{1..4+5}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || ((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || ((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]]})) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{1..4+2, 4+5..4+7}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || ((Int[4] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[2]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[(Int[4] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[2]]]]}) + ((((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined || ((Int[4] = Undefined || Int[7] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[7]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]] && int[i] <= int[(Int[4] = Undefined || Int[7] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[7]]]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || ((Int[4] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[2]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[(Int[4] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[2]]]]}) + ((((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined || ((Int[4] = Undefined || Int[7] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[7]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]] && int[i] <= int[(Int[4] = Undefined || Int[7] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[7]]]]})) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{1..3, 4+5, 12..10, 11..13}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined || ((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]] && int[i] <= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]]}) + ((Int[12] = Undefined || Int[10] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[12]] && int[i] <= int[Int[10]]}) + ((Int[11] = Undefined || Int[13] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[11]] && int[i] <= int[Int[13]]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Int[3]]}) + ((((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined || ((Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]) = Undefined) => Undefined_Set else {i: ints | int[i] >= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]] && int[i] <= int[(Int[4] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[4]] + int[Int[5]]]]}) + ((Int[12] = Undefined || Int[10] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[12]] && int[i] <= int[Int[10]]}) + ((Int[11] = Undefined || Int[13] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[11]] && int[i] <= int[Int[13]]}))";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{Undefined}";
		String expected = "(Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{1,2,'Ada',Undefined}";
		String expected = "(Undefined_Set in (none + Int[1] + Int[2] + String_Ada + Undefined)) => Undefined_Set else (none + Int[1] + Int[2] + String_Ada + Undefined) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{1+3,Undefined}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]) + Undefined)) => Undefined_Set else (none + ((Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]) + Undefined) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{Undefined..Undefined}";
		String expected = "(Undefined_Set in (none + ((Undefined = Undefined || Undefined = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Undefined] && int[i] <= int[Undefined]}))) => Undefined_Set else (none + ((Undefined = Undefined || Undefined = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Undefined] && int[i] <= int[Undefined]})) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{Undefined..9}";
		String expected = "(Undefined_Set in (none + ((Undefined = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Undefined] && int[i] <= int[Int[9]]}))) => Undefined_Set else (none + ((Undefined = Undefined || Int[9] = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Undefined] && int[i] <= int[Int[9]]})) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{1..Undefined}";
		String expected = "(Undefined_Set in (none + ((Int[1] = Undefined || Undefined = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Undefined]}))) => Undefined_Set else (none + ((Int[1] = Undefined || Undefined = Undefined) => Undefined_Set else {i: ints | int[i] >= int[Int[1]] && int[i] <= int[Undefined]})) ";
		test("test26", ocl, expected);
	}

}
