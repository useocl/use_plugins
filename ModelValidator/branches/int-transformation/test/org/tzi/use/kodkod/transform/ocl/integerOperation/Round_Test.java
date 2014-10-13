package org.tzi.use.kodkod.transform.ocl.integerOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Round_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "(-2).round()";
		String expected = "Int[-2] ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "(-1).round()";
		String expected = "Int[-1] ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "0.round()";
		String expected = "Int[0] ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "1.round()";
		String expected = "Int[1] ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "2.round()";
		String expected = "Int[2]  ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Undefined.round()";
		String expected = "Undefined  ";
		test("test6", ocl, expected);
	}

}
