package org.tzi.use.kodkod.transform.ocl.booleanOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Xor_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "true xor true";
		String expected = "(Boolean_True = Boolean_True || Boolean_True = Boolean_True) && (Boolean_True = Boolean_False || Boolean_True = Boolean_False) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "true xor false";
		String expected = "(Boolean_True = Boolean_True || Boolean_False = Boolean_True) && (Boolean_True = Boolean_False || Boolean_False = Boolean_False) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "false xor true";
		String expected = "(Boolean_False = Boolean_True || Boolean_True = Boolean_True) && (Boolean_False = Boolean_False || Boolean_True = Boolean_False) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "false xor false";
		String expected = "(Boolean_False = Boolean_True || Boolean_False = Boolean_True) && (Boolean_False = Boolean_False || Boolean_False = Boolean_False)  ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "true xor Undefined";
		String expected = "(Boolean_True = Boolean_True || Undefined = Boolean_True) && (Boolean_True = Boolean_False || Undefined = Boolean_False) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Undefined xor true";
		String expected = "(Undefined = Boolean_True || Boolean_True = Boolean_True) && (Undefined = Boolean_False || Boolean_True = Boolean_False) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "false xor Undefined";
		String expected = "(Boolean_False = Boolean_True || Undefined = Boolean_True) && (Boolean_False = Boolean_False || Undefined = Boolean_False) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Undefined xor false";
		String expected = "(Undefined = Boolean_True || Boolean_False = Boolean_True) && (Undefined = Boolean_False || Boolean_False = Boolean_False) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Undefined xor Undefined";
		String expected = "(Undefined = Boolean_True || Undefined = Boolean_True) && (Undefined = Boolean_False || Undefined = Boolean_False)  ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "('Ada' = 'Ada') xor true";
		String expected = "(String_Ada = String_Ada || Boolean_True = Boolean_True) && (!(String_Ada = String_Ada) || Boolean_True = Boolean_False) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "true xor ('Ada' = 'Ada')";
		String expected = "(Boolean_True = Boolean_True || String_Ada = String_Ada) && (Boolean_True = Boolean_False || !(String_Ada = String_Ada)) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "('Ada' = 'Bob') xor true";
		String expected = "(String_Ada = String_Bob || Boolean_True = Boolean_True) && (!(String_Ada = String_Bob) || Boolean_True = Boolean_False) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "true xor ('Ada' = 'Bob')";
		String expected = "(Boolean_True = Boolean_True || String_Ada = String_Bob) && (Boolean_True = Boolean_False || !(String_Ada = String_Bob))  ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "('Ada' = 'Ada') xor false";
		String expected = "(String_Ada = String_Ada || Boolean_False = Boolean_True) && (!(String_Ada = String_Ada) || Boolean_False = Boolean_False) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "false xor ('Ada' = 'Ada')";
		String expected = "(Boolean_False = Boolean_True || String_Ada = String_Ada) && (Boolean_False = Boolean_False || !(String_Ada = String_Ada)) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "('Ada' = 'Bob') xor false";
		String expected = "(String_Ada = String_Bob || Boolean_False = Boolean_True) && (!(String_Ada = String_Bob) || Boolean_False = Boolean_False) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "false xor ('Ada' = 'Bob')";
		String expected = "(Boolean_False = Boolean_True || String_Ada = String_Bob) && (Boolean_False = Boolean_False || !(String_Ada = String_Bob))  ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "('Ada' = 'Ada') xor ('Ada' = 'Ada')";
		String expected = "(String_Ada = String_Ada && !(String_Ada = String_Ada)) || (!(String_Ada = String_Ada) && String_Ada = String_Ada)";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "('Ada' = 'Ada') xor ('Ada' = 'Bob')";
		String expected = "(String_Ada = String_Ada && !(String_Ada = String_Bob)) || (!(String_Ada = String_Ada) && String_Ada = String_Bob)";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "('Ada' = 'Bob') xor ('Ada' = 'Ada')";
		String expected = "(String_Ada = String_Bob && !(String_Ada = String_Ada)) || (!(String_Ada = String_Bob) && String_Ada = String_Ada)";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "('Ada' = 'Bob') xor ('Ada' = 'Bob')";
		String expected = "(String_Ada = String_Bob && !(String_Ada = String_Bob)) || (!(String_Ada = String_Bob) && String_Ada = String_Bob)";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "('Ada' = 'Ada') xor Undefined";
		String expected = "(String_Ada = String_Ada || Undefined = Boolean_True) && (!(String_Ada = String_Ada) || Undefined = Boolean_False) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Undefined xor ('Ada' = 'Ada')";
		String expected = "(Undefined = Boolean_True || String_Ada = String_Ada) && (Undefined = Boolean_False || !(String_Ada = String_Ada)) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "('Ada' = 'Bob') xor Undefined";
		String expected = "(String_Ada = String_Bob || Undefined = Boolean_True) && (!(String_Ada = String_Bob) || Undefined = Boolean_False) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Undefined xor ('Ada' = 'Bob')";
		String expected = "(Undefined = Boolean_True || String_Ada = String_Bob) && (Undefined = Boolean_False || !(String_Ada = String_Bob))  ";
		test("test25", ocl, expected);
	}

}
