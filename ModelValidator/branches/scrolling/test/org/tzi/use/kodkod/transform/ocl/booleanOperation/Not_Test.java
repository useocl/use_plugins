package org.tzi.use.kodkod.transform.ocl.booleanOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Not_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "not true";
		String expected = "Boolean_True = Boolean_False  ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "not false";
		String expected = "Boolean_False = Boolean_False   ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "not Undefined";
		String expected = "Undefined = Boolean_False   ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "not ('Ada' = 'Ada')";
		String expected = "!(String_Ada = String_Ada)  ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "not ('Ada' = 'Bob')";
		String expected = "!(String_Ada = String_Bob)   ";
		test("test5", ocl, expected);
	}

}
