package org.tzi.use.kodkod.transform.ocl.booleanOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Or_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "true or true";
		String expected = "Boolean_True = Boolean_True || Boolean_True = Boolean_True  ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "true or false";
		String expected = "Boolean_True = Boolean_True || Boolean_False = Boolean_True  ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "false or true";
		String expected = "Boolean_False = Boolean_True || Boolean_True = Boolean_True  ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "false or false";
		String expected = "Boolean_False = Boolean_True || Boolean_False = Boolean_True   ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "true or Undefined";
		String expected = "Boolean_True = Boolean_True || Undefined = Boolean_True  ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Undefined or true";
		String expected = "Undefined = Boolean_True || Boolean_True = Boolean_True  ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "false or Undefined";
		String expected = "Boolean_False = Boolean_True || Undefined = Boolean_True  ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Undefined or false";
		String expected = "Undefined = Boolean_True || Boolean_False = Boolean_True  ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Undefined or Undefined";
		String expected = "Undefined = Boolean_True || Undefined = Boolean_True   ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "('Ada' = 'Ada') or true";
		String expected = "String_Ada = String_Ada || Boolean_True = Boolean_True  ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "true or ('Ada' = 'Ada')";
		String expected = "Boolean_True = Boolean_True || String_Ada = String_Ada  ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "('Ada' = 'Bob') or true";
		String expected = "String_Ada = String_Bob || Boolean_True = Boolean_True  ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "true or ('Ada' = 'Bob')";
		String expected = "Boolean_True = Boolean_True || String_Ada = String_Bob   ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "('Ada' = 'Ada') or false";
		String expected = "String_Ada = String_Ada || Boolean_False = Boolean_True  ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "false or ('Ada' = 'Ada')";
		String expected = "Boolean_False = Boolean_True || String_Ada = String_Ada  ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "('Ada' = 'Bob') or false";
		String expected = "String_Ada = String_Bob || Boolean_False = Boolean_True  ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "false or ('Ada' = 'Bob')";
		String expected = "Boolean_False = Boolean_True || String_Ada = String_Bob   ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "('Ada' = 'Ada') or ('Ada' = 'Ada')";
		String expected = "String_Ada = String_Ada || String_Ada = String_Ada  ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "('Ada' = 'Ada') or ('Ada' = 'Bob')";
		String expected = "String_Ada = String_Ada || String_Ada = String_Bob  ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "('Ada' = 'Bob') or ('Ada' = 'Ada')";
		String expected = "String_Ada = String_Bob || String_Ada = String_Ada  ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "('Ada' = 'Bob') or ('Ada' = 'Bob')";
		String expected = "String_Ada = String_Bob || String_Ada = String_Bob   ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "('Ada' = 'Ada') or Undefined";
		String expected = "String_Ada = String_Ada || Undefined = Boolean_True  ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Undefined or ('Ada' = 'Ada')";
		String expected = "Undefined = Boolean_True || String_Ada = String_Ada  ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "('Ada' = 'Bob') or Undefined";
		String expected = "String_Ada = String_Bob || Undefined = Boolean_True  ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Undefined or ('Ada' = 'Bob')";
		String expected = "Undefined = Boolean_True || String_Ada = String_Bob   ";
		test("test25", ocl, expected);
	}

}
