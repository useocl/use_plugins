package org.tzi.use.kodkod.transform.ocl.ifThenElse;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class IfThenElse_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "if true then 1 else 2 endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Int[1] else Int[2]) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "if false then 1 else 2 endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Int[1] else Int[2]) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "if true then 1+2 else 2+2 endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) else ((Int[2] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[2]] + int[Int[2]]])) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "if false then 1+2 else 2+2 endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) else ((Int[2] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[2]] + int[Int[2]]])) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "if (2<3) then 1 else 2 endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Int[1] else Int[2] ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "if (2>3) then 1 else 2 endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Int[1] else Int[2] ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "if (2<3) then 1+2 else 2+2 endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) else ((Int[2] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[2]] + int[Int[2]]]) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "if (2>3) then 1+2 else 2+2 endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) else ((Int[2] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[2]] + int[Int[2]]]) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "if true then true else false endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Boolean_True else Boolean_False) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "if false then true else false endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Boolean_True else Boolean_False) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "if true then (2<3) else false endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else Boolean_False) else Boolean_False) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "if false then (2<3) else false endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else Boolean_False) else Boolean_False) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "if true then true else (2>3) endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Boolean_True else ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else Boolean_False)) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "if false then true else (2>3) endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Boolean_True else ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else Boolean_False)) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "if true then (2<3) else (2>3) endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else Boolean_False) else ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else Boolean_False)) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "if false then (2<3) else (2>3) endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else Boolean_False) else ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else Boolean_False)) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "if (2<3) then true else false endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else Boolean_False ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "if (2>3) then true else false endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else Boolean_False ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "if (2<3) then (2<3) else false endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else Boolean_False) else Boolean_False ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "if (2>3) then (2<3) else false endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else Boolean_False) else Boolean_False ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "if (2<3) then true else (2>3) endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => Boolean_True else ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else Boolean_False) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "if (2>3) then true else (2>3) endif";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else ((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => Boolean_True else Boolean_False) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "if (2<3) then (2<3) else (2>3) endif";
		String expected = "((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => (!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]])) && (!(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) => (!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]])) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "if (2>3) then (2<3) else (2>3) endif";
		String expected = "((!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => (!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]])) && (!(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]]) => (!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] > int[Int[3]])) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "if true then Undefined else 2 endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Undefined else Int[2]) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "if false then Undefined else 2 endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Undefined else Int[2]) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "if true then 1 else Undefined endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Int[1] else Undefined) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "if false then 1 else Undefined endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Int[1] else Undefined) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "if Undefined then Undefined else 2 endif";
		String expected = "(Undefined = Undefined) => Undefined else ((Undefined = Boolean_True) => Undefined else Int[2]) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "if Undefined then 2 else Undefined endif";
		String expected = "(Undefined = Undefined) => Undefined else ((Undefined = Boolean_True) => Int[2] else Undefined) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "if true then Undefined else false endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Undefined else Boolean_False) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "if false then Undefined else false endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Undefined else Boolean_False) ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "if true then true else Undefined endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Boolean_True else Undefined) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "if false then true else Undefined endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Boolean_True else Undefined) ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "if true then Undefined else Undefined endif";
		String expected = "(Boolean_True = Undefined) => Undefined else ((Boolean_True = Boolean_True) => Undefined else Undefined) ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "if false then Undefined else Undefined endif";
		String expected = "(Boolean_False = Undefined) => Undefined else ((Boolean_False = Boolean_True) => Undefined else Undefined) ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "if Undefined then Undefined else false endif";
		String expected = "(Undefined = Undefined) => Undefined else ((Undefined = Boolean_True) => Undefined else Boolean_False) ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "if Undefined then true else Undefined endif";
		String expected = "(Undefined = Undefined) => Undefined else ((Undefined = Boolean_True) => Boolean_True else Undefined) ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "if Undefined then Undefined else Undefined endif";
		String expected = "(Undefined = Undefined) => Undefined else ((Undefined = Boolean_True) => Undefined else Undefined) ";
		test("test39", ocl, expected);
	}

}
