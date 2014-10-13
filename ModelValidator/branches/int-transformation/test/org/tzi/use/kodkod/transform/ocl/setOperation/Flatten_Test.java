package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Flatten_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Set{}->flatten()";
		String expected = "none ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{1}->flatten()";
		String expected = "(Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{1,2}->flatten()";
		String expected = "(Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{1,2,3}->flatten()";
		String expected = "(Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{Undefined,1}->flatten()";
		String expected = "(Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{Undefined,1,2}->flatten()";
		String expected = "(Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{Undefined}->flatten()";
		String expected = "(Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) ";
		test("test7", ocl, expected);
	}

}
