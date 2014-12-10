package org.tzi.use.kodkod.transform.ocl.integerOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Abs_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "(-2).abs()";
		String expected = "(Int[-2] = Undefined) => Undefined else Int[abs(int[Int[-2]])] ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "(-1).abs()";
		String expected = "(Int[-1] = Undefined) => Undefined else Int[abs(int[Int[-1]])] ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "0.abs()";
		String expected = "(Int[0] = Undefined) => Undefined else Int[abs(int[Int[0]])] ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "1.abs()";
		String expected = "(Int[1] = Undefined) => Undefined else Int[abs(int[Int[1]])] ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "2.abs()";
		String expected = "(Int[2] = Undefined) => Undefined else Int[abs(int[Int[2]])]  ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Undefined.abs()";
		String expected = "(Undefined = Undefined) => Undefined else Int[abs(int[Undefined])]  ";
		test("test6", ocl, expected);
	}

}
