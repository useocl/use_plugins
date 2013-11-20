package org.tzi.use.kodkod.transform.ocl.anyOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class IsDefined_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Undefined.isDefined()";
		String expected = "!(Undefined = Undefined) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "ada.alive.isDefined()";
		String expected = "!(((Person_ada = Undefined) => Undefined else (Person_ada . Person_alive)) = Undefined) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "ada.employerA.isDefined()";
		String expected = "!(((Person_ada = Undefined) => Undefined else (Person_ada . Job_A)) = Undefined)  ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Color::red.isDefined()";
		String expected = "!(Color_red = Undefined) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "ada.isDefined";
		String expected = "!(Person_ada = Undefined) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "true.isDefined()";
		String expected = "!(Boolean_True = Undefined) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "false.isDefined()";
		String expected = "!(Boolean_False = Undefined) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "(1 = 1).isDefined()";
		String expected = "true ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "(1 <> 1).isDefined()";
		String expected = "true ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "'Ada'.isDefined";
		String expected = "!(String_Ada = Undefined) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "''.isDefined";
		String expected = "!(String_ = Undefined) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "(-1).isDefined()";
		String expected = "!(Int[-1] = Undefined) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "0.isDefined()";
		String expected = "!(Int[0] = Undefined) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "1.isDefined()";
		String expected = "!(Int[1] = Undefined) ";
		test("test14", ocl, expected);
	}

}
