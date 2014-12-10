package org.tzi.use.kodkod.transform.ocl.integerOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class ToString_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "0.toString()";
		String expected = "(Int[0] = Undefined) => Undefined else (Int[0] . ToStringMap) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "1.toString()";
		String expected = "(Int[1] = Undefined) => Undefined else (Int[1] . ToStringMap) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "2.toString()";
		String expected = "(Int[2] = Undefined) => Undefined else (Int[2] . ToStringMap) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "0.toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Int[0] = Undefined) => Undefined else (Int[0] . ToStringMap)) in String ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "1.toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Int[1] = Undefined) => Undefined else (Int[1] . ToStringMap)) in String ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "2.toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Int[2] = Undefined) => Undefined else (Int[2] . ToStringMap)) in String ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "(-1).toString()";
		String expected = "(Int[-1] = Undefined) => Undefined else (Int[-1] . ToStringMap) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "(-2).toString()";
		String expected = "(Int[-2] = Undefined) => Undefined else (Int[-2] . ToStringMap) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "(-1).toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Int[-1] = Undefined) => Undefined else (Int[-1] . ToStringMap)) in String ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "(-2).toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Int[-2] = Undefined) => Undefined else (Int[-2] . ToStringMap)) in String ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Undefined.toString()";
		String expected = "(Undefined = Undefined) => Undefined else (Undefined . ToStringMap) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Undefined.toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Undefined = Undefined) => Undefined else (Undefined . ToStringMap)) in String ";
		test("test12", ocl, expected);
	}

}
