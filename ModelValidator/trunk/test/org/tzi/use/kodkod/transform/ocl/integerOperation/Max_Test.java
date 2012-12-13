package org.tzi.use.kodkod.transform.ocl.integerOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Max_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "0.max(0)";
		String expected = "(Int[0] = Undefined || Int[0] = Undefined) => Undefined else ((int[Int[0]] >= int[Int[0]]) => Int[0] else Int[0]) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "1.max(0)";
		String expected = "(Int[1] = Undefined || Int[0] = Undefined) => Undefined else ((int[Int[1]] >= int[Int[0]]) => Int[1] else Int[0]) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "2.max(0)";
		String expected = "(Int[2] = Undefined || Int[0] = Undefined) => Undefined else ((int[Int[2]] >= int[Int[0]]) => Int[2] else Int[0]) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "0.max(1)";
		String expected = "(Int[0] = Undefined || Int[1] = Undefined) => Undefined else ((int[Int[0]] >= int[Int[1]]) => Int[0] else Int[1]) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "1.max(1)";
		String expected = "(Int[1] = Undefined || Int[1] = Undefined) => Undefined else ((int[Int[1]] >= int[Int[1]]) => Int[1] else Int[1]) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "2.max(1)";
		String expected = "(Int[2] = Undefined || Int[1] = Undefined) => Undefined else ((int[Int[2]] >= int[Int[1]]) => Int[2] else Int[1]) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "0.max(2)";
		String expected = "(Int[0] = Undefined || Int[2] = Undefined) => Undefined else ((int[Int[0]] >= int[Int[2]]) => Int[0] else Int[2]) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "1.max(2)";
		String expected = "(Int[1] = Undefined || Int[2] = Undefined) => Undefined else ((int[Int[1]] >= int[Int[2]]) => Int[1] else Int[2]) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "2.max(2)";
		String expected = "(Int[2] = Undefined || Int[2] = Undefined) => Undefined else ((int[Int[2]] >= int[Int[2]]) => Int[2] else Int[2]) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "3.max(2)";
		String expected = "(Int[3] = Undefined || Int[2] = Undefined) => Undefined else ((int[Int[3]] >= int[Int[2]]) => Int[3] else Int[2]) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "(-1).max(0)";
		String expected = "(Int[-1] = Undefined || Int[0] = Undefined) => Undefined else ((int[Int[-1]] >= int[Int[0]]) => Int[-1] else Int[0]) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "(-2).max(0)";
		String expected = "(Int[-2] = Undefined || Int[0] = Undefined) => Undefined else ((int[Int[-2]] >= int[Int[0]]) => Int[-2] else Int[0]) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "(-1).max(1)";
		String expected = "(Int[-1] = Undefined || Int[1] = Undefined) => Undefined else ((int[Int[-1]] >= int[Int[1]]) => Int[-1] else Int[1]) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "(-2).max(1)";
		String expected = "(Int[-2] = Undefined || Int[1] = Undefined) => Undefined else ((int[Int[-2]] >= int[Int[1]]) => Int[-2] else Int[1]) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "(-1).max(2)";
		String expected = "(Int[-1] = Undefined || Int[2] = Undefined) => Undefined else ((int[Int[-1]] >= int[Int[2]]) => Int[-1] else Int[2]) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "(-2).max(2)";
		String expected = "(Int[-2] = Undefined || Int[2] = Undefined) => Undefined else ((int[Int[-2]] >= int[Int[2]]) => Int[-2] else Int[2]) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "(-3).max(2)";
		String expected = "(Int[-3] = Undefined || Int[2] = Undefined) => Undefined else ((int[Int[-3]] >= int[Int[2]]) => Int[-3] else Int[2]) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "0.max(-1)";
		String expected = "(Int[0] = Undefined || Int[-1] = Undefined) => Undefined else ((int[Int[0]] >= int[Int[-1]]) => Int[0] else Int[-1]) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "1.max(-1)";
		String expected = "(Int[1] = Undefined || Int[-1] = Undefined) => Undefined else ((int[Int[1]] >= int[Int[-1]]) => Int[1] else Int[-1]) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "2.max(-1)";
		String expected = "(Int[2] = Undefined || Int[-1] = Undefined) => Undefined else ((int[Int[2]] >= int[Int[-1]]) => Int[2] else Int[-1]) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "0.max(-2)";
		String expected = "(Int[0] = Undefined || Int[-2] = Undefined) => Undefined else ((int[Int[0]] >= int[Int[-2]]) => Int[0] else Int[-2]) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "1.max(-2)";
		String expected = "(Int[1] = Undefined || Int[-2] = Undefined) => Undefined else ((int[Int[1]] >= int[Int[-2]]) => Int[1] else Int[-2]) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "2.max(-2)";
		String expected = "(Int[2] = Undefined || Int[-2] = Undefined) => Undefined else ((int[Int[2]] >= int[Int[-2]]) => Int[2] else Int[-2]) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "3.max(-2)";
		String expected = "(Int[3] = Undefined || Int[-2] = Undefined) => Undefined else ((int[Int[3]] >= int[Int[-2]]) => Int[3] else Int[-2]) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "(-1).max(-1)";
		String expected = "(Int[-1] = Undefined || Int[-1] = Undefined) => Undefined else ((int[Int[-1]] >= int[Int[-1]]) => Int[-1] else Int[-1]) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "(-2).max(-1)";
		String expected = "(Int[-2] = Undefined || Int[-1] = Undefined) => Undefined else ((int[Int[-2]] >= int[Int[-1]]) => Int[-2] else Int[-1]) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "(-1).max(-2)";
		String expected = "(Int[-1] = Undefined || Int[-2] = Undefined) => Undefined else ((int[Int[-1]] >= int[Int[-2]]) => Int[-1] else Int[-2]) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "(-2).max(-2)";
		String expected = "(Int[-2] = Undefined || Int[-2] = Undefined) => Undefined else ((int[Int[-2]] >= int[Int[-2]]) => Int[-2] else Int[-2]) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "(-3).max(-2)";
		String expected = "(Int[-3] = Undefined || Int[-2] = Undefined) => Undefined else ((int[Int[-3]] >= int[Int[-2]]) => Int[-3] else Int[-2]) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Undefined.max(-1)";
		String expected = "(Undefined = Undefined || Int[-1] = Undefined) => Undefined else ((int[Undefined] >= int[Int[-1]]) => Undefined else Int[-1]) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Undefined.max(0)";
		String expected = "(Undefined = Undefined || Int[0] = Undefined) => Undefined else ((int[Undefined] >= int[Int[0]]) => Undefined else Int[0]) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "Undefined.max(1)";
		String expected = "(Undefined = Undefined || Int[1] = Undefined) => Undefined else ((int[Undefined] >= int[Int[1]]) => Undefined else Int[1]) ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "(-1).max(Undefined)";
		String expected = "(Int[-1] = Undefined || Undefined = Undefined) => Undefined else ((int[Int[-1]] >= int[Undefined]) => Int[-1] else Undefined) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "0.max(Undefined)";
		String expected = "(Int[0] = Undefined || Undefined = Undefined) => Undefined else ((int[Int[0]] >= int[Undefined]) => Int[0] else Undefined) ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "1.max(Undefined)";
		String expected = "(Int[1] = Undefined || Undefined = Undefined) => Undefined else ((int[Int[1]] >= int[Undefined]) => Int[1] else Undefined) ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "Undefined.max(Undefined)";
		String expected = "(Undefined = Undefined || Undefined = Undefined) => Undefined else ((int[Undefined] >= int[Undefined]) => Undefined else Undefined) ";
		test("test36", ocl, expected);
	}

}
