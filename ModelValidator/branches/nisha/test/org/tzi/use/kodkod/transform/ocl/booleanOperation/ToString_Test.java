package org.tzi.use.kodkod.transform.ocl.booleanOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class ToString_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "true.toString()";
		String expected = "(Boolean_True = Undefined) => Undefined else (Boolean_True . ToStringMap) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "false.toString()";
		String expected = "(Boolean_False = Undefined) => Undefined else (Boolean_False . ToStringMap) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "true.toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Boolean_True = Undefined) => Undefined else (Boolean_True . ToStringMap)) in String ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "false.toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Boolean_False = Undefined) => Undefined else (Boolean_False . ToStringMap)) in String ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "(1 = 1).toString()";
		String expected = "(Int[1] = Int[1]) => String_True else String_False ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "(1 <> 1).toString()";
		String expected = "!(Int[1] = Int[1]) => String_True else String_False ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "(1 = 1).toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Int[1] = Int[1]) => String_True else String_False) in String ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "(1 <> 1).toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && (!(Int[1] = Int[1]) => String_True else String_False) in String ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Undefined.toString()";
		String expected = "(Undefined = Undefined) => Undefined else (Undefined . ToStringMap) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Undefined.toString().oclIsTypeOf(String)";
		String expected = "!(String = univ) && ((Undefined = Undefined) => Undefined else (Undefined . ToStringMap)) in String ";
		test("test10", ocl, expected);
	}

}
