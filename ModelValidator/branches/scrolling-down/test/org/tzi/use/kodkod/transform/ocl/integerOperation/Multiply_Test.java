package org.tzi.use.kodkod.transform.ocl.integerOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Multiply_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "0 * 0";
		String expected = "(Int[0] = Undefined || Int[0] = Undefined) => Undefined else Int[int[Int[0]] * int[Int[0]]] ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "0 * 1";
		String expected = "(Int[0] = Undefined || Int[1] = Undefined) => Undefined else Int[int[Int[0]] * int[Int[1]]]  ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "1 * 0";
		String expected = "(Int[1] = Undefined || Int[0] = Undefined) => Undefined else Int[int[Int[1]] * int[Int[0]]] ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "1 * 1";
		String expected = "(Int[1] = Undefined || Int[1] = Undefined) => Undefined else Int[int[Int[1]] * int[Int[1]]] ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "1 * 2";
		String expected = "(Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] * int[Int[2]]]  ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "3 * 0";
		String expected = "(Int[3] = Undefined || Int[0] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[0]]] ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "3 * 1";
		String expected = "(Int[3] = Undefined || Int[1] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[1]]] ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "3 * 2";
		String expected = "(Int[3] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[2]]] ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "3 * 3";
		String expected = "(Int[3] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[3]]] ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "3 * 4";
		String expected = "(Int[3] = Undefined || Int[4] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[4]]] ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "3 * 5";
		String expected = "(Int[3] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[5]]] ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "3 * 6";
		String expected = "(Int[3] = Undefined || Int[6] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[6]]] ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "3 * 7";
		String expected = "(Int[3] = Undefined || Int[7] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[7]]] ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "3 * 8";
		String expected = "(Int[3] = Undefined || Int[8] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[8]]] ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "3 * 9";
		String expected = "(Int[3] = Undefined || Int[9] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[9]]] ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "3 * 10";
		String expected = "(Int[3] = Undefined || Int[10] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[10]]]  ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "0 * 3";
		String expected = "(Int[0] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[0]] * int[Int[3]]] ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "1 * 3";
		String expected = "(Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] * int[Int[3]]] ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "2 * 3";
		String expected = "(Int[2] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[2]] * int[Int[3]]] ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "4 * 3";
		String expected = "(Int[4] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[4]] * int[Int[3]]] ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "5 * 3";
		String expected = "(Int[5] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[5]] * int[Int[3]]] ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "6 * 3";
		String expected = "(Int[6] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[6]] * int[Int[3]]] ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "7 * 3";
		String expected = "(Int[7] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[7]] * int[Int[3]]] ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "8 * 3";
		String expected = "(Int[8] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[8]] * int[Int[3]]] ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "9 * 3";
		String expected = "(Int[9] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[9]] * int[Int[3]]] ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "10 * 3";
		String expected = "(Int[10] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[10]] * int[Int[3]]]  ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "0 * -1";
		String expected = "(Int[0] = Undefined || Int[-1] = Undefined) => Undefined else Int[int[Int[0]] * int[Int[-1]]]  ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "1 * -1";
		String expected = "(Int[1] = Undefined || Int[-1] = Undefined) => Undefined else Int[int[Int[1]] * int[Int[-1]]] ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "1 * -2";
		String expected = "(Int[1] = Undefined || Int[-2] = Undefined) => Undefined else Int[int[Int[1]] * int[Int[-2]]]  ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "3 * -1";
		String expected = "(Int[3] = Undefined || Int[-1] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-1]]] ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "3 * -2";
		String expected = "(Int[3] = Undefined || Int[-2] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-2]]] ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "3 * -3";
		String expected = "(Int[3] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-3]]] ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "3 * -4";
		String expected = "(Int[3] = Undefined || Int[-4] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-4]]] ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "3 * -5";
		String expected = "(Int[3] = Undefined || Int[-5] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-5]]] ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "3 * -6";
		String expected = "(Int[3] = Undefined || Int[-6] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-6]]] ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "3 * -7";
		String expected = "(Int[3] = Undefined || Int[-7] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-7]]] ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "3 * -8";
		String expected = "(Int[3] = Undefined || Int[-8] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-8]]] ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "3 * -9";
		String expected = "(Int[3] = Undefined || Int[-9] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-9]]] ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "3 * -10";
		String expected = "(Int[3] = Undefined || Int[-10] = Undefined) => Undefined else Int[int[Int[3]] * int[Int[-10]]]  ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "0 * -3";
		String expected = "(Int[0] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[0]] * int[Int[-3]]] ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "1 * -3";
		String expected = "(Int[1] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[1]] * int[Int[-3]]] ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "2 * -3";
		String expected = "(Int[2] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[2]] * int[Int[-3]]] ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "4 * -3";
		String expected = "(Int[4] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[4]] * int[Int[-3]]] ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "5 * -3";
		String expected = "(Int[5] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[5]] * int[Int[-3]]] ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "6 * -3";
		String expected = "(Int[6] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[6]] * int[Int[-3]]] ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "7 * -3";
		String expected = "(Int[7] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[7]] * int[Int[-3]]] ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "8 * -3";
		String expected = "(Int[8] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[8]] * int[Int[-3]]] ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "9 * -3";
		String expected = "(Int[9] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[9]] * int[Int[-3]]] ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "10 * -3";
		String expected = "(Int[10] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[10]] * int[Int[-3]]]  ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "-1 * 0";
		String expected = "(Int[-1] = Undefined || Int[0] = Undefined) => Undefined else Int[int[Int[-1]] * int[Int[0]]] ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "-1 * 1";
		String expected = "(Int[-1] = Undefined || Int[1] = Undefined) => Undefined else Int[int[Int[-1]] * int[Int[1]]] ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "-1 * 2";
		String expected = "(Int[-1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[-1]] * int[Int[2]]]  ";
		test("test52", ocl, expected);
	}

	@Test
	public void test53() {
		String ocl = "-3 * 0";
		String expected = "(Int[-3] = Undefined || Int[0] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[0]]] ";
		test("test53", ocl, expected);
	}

	@Test
	public void test54() {
		String ocl = "-3 * 1";
		String expected = "(Int[-3] = Undefined || Int[1] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[1]]] ";
		test("test54", ocl, expected);
	}

	@Test
	public void test55() {
		String ocl = "-3 * 2";
		String expected = "(Int[-3] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[2]]] ";
		test("test55", ocl, expected);
	}

	@Test
	public void test56() {
		String ocl = "-3 * 3";
		String expected = "(Int[-3] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[3]]] ";
		test("test56", ocl, expected);
	}

	@Test
	public void test57() {
		String ocl = "-3 * 4";
		String expected = "(Int[-3] = Undefined || Int[4] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[4]]] ";
		test("test57", ocl, expected);
	}

	@Test
	public void test58() {
		String ocl = "-3 * 5";
		String expected = "(Int[-3] = Undefined || Int[5] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[5]]] ";
		test("test58", ocl, expected);
	}

	@Test
	public void test59() {
		String ocl = "-3 * 6";
		String expected = "(Int[-3] = Undefined || Int[6] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[6]]] ";
		test("test59", ocl, expected);
	}

	@Test
	public void test60() {
		String ocl = "-3 * 7";
		String expected = "(Int[-3] = Undefined || Int[7] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[7]]] ";
		test("test60", ocl, expected);
	}

	@Test
	public void test61() {
		String ocl = "-3 * 8";
		String expected = "(Int[-3] = Undefined || Int[8] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[8]]] ";
		test("test61", ocl, expected);
	}

	@Test
	public void test62() {
		String ocl = "-3 * 9";
		String expected = "(Int[-3] = Undefined || Int[9] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[9]]] ";
		test("test62", ocl, expected);
	}

	@Test
	public void test63() {
		String ocl = "-3 * 10";
		String expected = "(Int[-3] = Undefined || Int[10] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[10]]]  ";
		test("test63", ocl, expected);
	}

	@Test
	public void test64() {
		String ocl = "-1 * 3";
		String expected = "(Int[-1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-1]] * int[Int[3]]] ";
		test("test64", ocl, expected);
	}

	@Test
	public void test65() {
		String ocl = "-2 * 3";
		String expected = "(Int[-2] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-2]] * int[Int[3]]] ";
		test("test65", ocl, expected);
	}

	@Test
	public void test66() {
		String ocl = "-4 * 3";
		String expected = "(Int[-4] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-4]] * int[Int[3]]] ";
		test("test66", ocl, expected);
	}

	@Test
	public void test67() {
		String ocl = "-5 * 3";
		String expected = "(Int[-5] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-5]] * int[Int[3]]] ";
		test("test67", ocl, expected);
	}

	@Test
	public void test68() {
		String ocl = "-6 * 3";
		String expected = "(Int[-6] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-6]] * int[Int[3]]] ";
		test("test68", ocl, expected);
	}

	@Test
	public void test69() {
		String ocl = "-7 * 3";
		String expected = "(Int[-7] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-7]] * int[Int[3]]] ";
		test("test69", ocl, expected);
	}

	@Test
	public void test70() {
		String ocl = "-8 * 3";
		String expected = "(Int[-8] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-8]] * int[Int[3]]] ";
		test("test70", ocl, expected);
	}

	@Test
	public void test71() {
		String ocl = "-9 * 3";
		String expected = "(Int[-9] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-9]] * int[Int[3]]] ";
		test("test71", ocl, expected);
	}

	@Test
	public void test72() {
		String ocl = "-10 * 3";
		String expected = "(Int[-10] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[-10]] * int[Int[3]]]  ";
		test("test72", ocl, expected);
	}

	@Test
	public void test73() {
		String ocl = "-1 * -1";
		String expected = "(Int[-1] = Undefined || Int[-1] = Undefined) => Undefined else Int[int[Int[-1]] * int[Int[-1]]] ";
		test("test73", ocl, expected);
	}

	@Test
	public void test74() {
		String ocl = "-1 * -2";
		String expected = "(Int[-1] = Undefined || Int[-2] = Undefined) => Undefined else Int[int[Int[-1]] * int[Int[-2]]]  ";
		test("test74", ocl, expected);
	}

	@Test
	public void test75() {
		String ocl = "-3 * -1";
		String expected = "(Int[-3] = Undefined || Int[-1] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-1]]] ";
		test("test75", ocl, expected);
	}

	@Test
	public void test76() {
		String ocl = "-3 * -2";
		String expected = "(Int[-3] = Undefined || Int[-2] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-2]]] ";
		test("test76", ocl, expected);
	}

	@Test
	public void test77() {
		String ocl = "-3 * -3";
		String expected = "(Int[-3] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-3]]] ";
		test("test77", ocl, expected);
	}

	@Test
	public void test78() {
		String ocl = "-3 * -4";
		String expected = "(Int[-3] = Undefined || Int[-4] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-4]]] ";
		test("test78", ocl, expected);
	}

	@Test
	public void test79() {
		String ocl = "-3 * -5";
		String expected = "(Int[-3] = Undefined || Int[-5] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-5]]] ";
		test("test79", ocl, expected);
	}

	@Test
	public void test80() {
		String ocl = "-3 * -6";
		String expected = "(Int[-3] = Undefined || Int[-6] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-6]]] ";
		test("test80", ocl, expected);
	}

	@Test
	public void test81() {
		String ocl = "-3 * -7";
		String expected = "(Int[-3] = Undefined || Int[-7] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-7]]] ";
		test("test81", ocl, expected);
	}

	@Test
	public void test82() {
		String ocl = "-3 * -8";
		String expected = "(Int[-3] = Undefined || Int[-8] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-8]]] ";
		test("test82", ocl, expected);
	}

	@Test
	public void test83() {
		String ocl = "-3 * -9";
		String expected = "(Int[-3] = Undefined || Int[-9] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-9]]] ";
		test("test83", ocl, expected);
	}

	@Test
	public void test84() {
		String ocl = "-3 * -10";
		String expected = "(Int[-3] = Undefined || Int[-10] = Undefined) => Undefined else Int[int[Int[-3]] * int[Int[-10]]]  ";
		test("test84", ocl, expected);
	}

	@Test
	public void test85() {
		String ocl = "-1 * -3";
		String expected = "(Int[-1] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-1]] * int[Int[-3]]] ";
		test("test85", ocl, expected);
	}

	@Test
	public void test86() {
		String ocl = "-2 * -3";
		String expected = "(Int[-2] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-2]] * int[Int[-3]]] ";
		test("test86", ocl, expected);
	}

	@Test
	public void test87() {
		String ocl = "-4 * -3";
		String expected = "(Int[-4] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-4]] * int[Int[-3]]] ";
		test("test87", ocl, expected);
	}

	@Test
	public void test88() {
		String ocl = "-5 * -3";
		String expected = "(Int[-5] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-5]] * int[Int[-3]]] ";
		test("test88", ocl, expected);
	}

	@Test
	public void test89() {
		String ocl = "-6 * -3";
		String expected = "(Int[-6] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-6]] * int[Int[-3]]] ";
		test("test89", ocl, expected);
	}

	@Test
	public void test90() {
		String ocl = "-7 * -3";
		String expected = "(Int[-7] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-7]] * int[Int[-3]]] ";
		test("test90", ocl, expected);
	}

	@Test
	public void test91() {
		String ocl = "-8 * -3";
		String expected = "(Int[-8] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-8]] * int[Int[-3]]] ";
		test("test91", ocl, expected);
	}

	@Test
	public void test92() {
		String ocl = "-9 * -3";
		String expected = "(Int[-9] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-9]] * int[Int[-3]]] ";
		test("test92", ocl, expected);
	}

	@Test
	public void test93() {
		String ocl = "-10 * -3";
		String expected = "(Int[-10] = Undefined || Int[-3] = Undefined) => Undefined else Int[int[Int[-10]] * int[Int[-3]]]  ";
		test("test93", ocl, expected);
	}

	@Test
	public void test94() {
		String ocl = "0 * Undefined";
		String expected = "(Int[0] = Undefined || Undefined = Undefined) => Undefined else Int[int[Int[0]] * int[Undefined]] ";
		test("test94", ocl, expected);
	}

	@Test
	public void test95() {
		String ocl = "Undefined * 0";
		String expected = "(Undefined = Undefined || Int[0] = Undefined) => Undefined else Int[int[Undefined] * int[Int[0]]] ";
		test("test95", ocl, expected);
	}

	@Test
	public void test96() {
		String ocl = "1 * Undefined";
		String expected = "(Int[1] = Undefined || Undefined = Undefined) => Undefined else Int[int[Int[1]] * int[Undefined]] ";
		test("test96", ocl, expected);
	}

	@Test
	public void test97() {
		String ocl = "Undefined * 1";
		String expected = "(Undefined = Undefined || Int[1] = Undefined) => Undefined else Int[int[Undefined] * int[Int[1]]] ";
		test("test97", ocl, expected);
	}

	@Test
	public void test98() {
		String ocl = "-1 * Undefined";
		String expected = "(Int[-1] = Undefined || Undefined = Undefined) => Undefined else Int[int[Int[-1]] * int[Undefined]] ";
		test("test98", ocl, expected);
	}

	@Test
	public void test99() {
		String ocl = "Undefined * -1";
		String expected = "(Undefined = Undefined || Int[-1] = Undefined) => Undefined else Int[int[Undefined] * int[Int[-1]]]  ";
		test("test99", ocl, expected);
	}

	@Test
	public void test100() {
		String ocl = "Undefined * Undefined";
		String expected = "(Undefined = Undefined || Undefined = Undefined) => Undefined else Int[int[Undefined] * int[Undefined]]  ";
		test("test100", ocl, expected);
	}

}
