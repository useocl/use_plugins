package org.tzi.use.kodkod.transform.ocl.anyOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class OclIsKindOf_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Undefined.oclIsKindOf(Boolean)";
		String expected = "Undefined = Undefined || Undefined in Boolean  ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Undefined.oclIsKindOf(Integer)";
		String expected = "Undefined = Undefined || Undefined in ints  ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Undefined.oclIsKindOf(String)";
		String expected = "Undefined = Undefined || Undefined in String  ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Undefined.oclIsKindOf(Color)";
		String expected = "Undefined = Undefined || Undefined in Color  ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Undefined.oclIsKindOf(Person)";
		String expected = "Undefined = Undefined || Undefined in Person  ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Undefined.oclIsKindOf(OclAny)";
		String expected = "Undefined = Undefined || Undefined in univ  ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Undefined.oclIsKindOf(OclVoid)";
		String expected = "Undefined = Undefined || Undefined in Undefined   ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Color::red.oclIsKindOf(Boolean)";
		String expected = "Color_red = Undefined || Color_red in Boolean  ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Color::red.oclIsKindOf(Integer)";
		String expected = "Color_red = Undefined || Color_red in ints  ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Color::red.oclIsKindOf(String)";
		String expected = "Color_red = Undefined || Color_red in String  ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Color::red.oclIsKindOf(Color)";
		String expected = "Color_red = Undefined || Color_red in Color  ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Color::red.oclIsKindOf(Person)";
		String expected = "Color_red = Undefined || Color_red in Person  ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Color::red.oclIsKindOf(OclAny)";
		String expected = "Color_red = Undefined || Color_red in univ  ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Color::red.oclIsKindOf(OclVoid)";
		String expected = "Color_red = Undefined || Color_red in Undefined   ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "ada.oclIsKindOf(Boolean)";
		String expected = "Person_ada = Undefined || Person_ada in Boolean  ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "ada.oclIsKindOf(Integer)";
		String expected = "Person_ada = Undefined || Person_ada in ints  ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "ada.oclIsKindOf(String)";
		String expected = "Person_ada = Undefined || Person_ada in String  ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "ada.oclIsKindOf(Color)";
		String expected = "Person_ada = Undefined || Person_ada in Color  ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "ada.oclIsKindOf(Person)";
		String expected = "Person_ada = Undefined || Person_ada in Person  ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "ada.oclIsKindOf(OclAny)";
		String expected = "Person_ada = Undefined || Person_ada in univ  ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "ada.oclIsKindOf(OclVoid)";
		String expected = "Person_ada = Undefined || Person_ada in Undefined   ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "true.oclIsKindOf(Boolean)";
		String expected = "Boolean_True = Undefined || Boolean_True in Boolean  ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "true.oclIsKindOf(Integer)";
		String expected = "Boolean_True = Undefined || Boolean_True in ints  ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "true.oclIsKindOf(String)";
		String expected = "Boolean_True = Undefined || Boolean_True in String  ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "true.oclIsKindOf(Color)";
		String expected = "Boolean_True = Undefined || Boolean_True in Color  ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "true.oclIsKindOf(Person)";
		String expected = "Boolean_True = Undefined || Boolean_True in Person  ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "true.oclIsKindOf(OclAny)";
		String expected = "Boolean_True = Undefined || Boolean_True in univ  ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "true.oclIsKindOf(OclVoid)";
		String expected = "Boolean_True = Undefined || Boolean_True in Undefined   ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "(1 = 1).oclIsKindOf(Boolean)";
		String expected = "Boolean = Boolean || Boolean = univ  ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "(1 = 1).oclIsKindOf(Integer)";
		String expected = "ints = Boolean || ints = univ  ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "(1 = 1).oclIsKindOf(String)";
		String expected = "String = Boolean || String = univ  ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "(1 = 1).oclIsKindOf(Color)";
		String expected = "Color = Boolean || Color = univ  ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "(1 = 1).oclIsKindOf(Person)";
		String expected = "Person = Boolean || Person = univ  ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "(1 = 1).oclIsKindOf(OclAny)";
		String expected = "univ = Boolean || univ = univ  ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "(1 = 1).oclIsKindOf(OclVoid)";
		String expected = "Undefined = Boolean || Undefined = univ   ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "'Ada'.oclIsKindOf(Boolean)";
		String expected = "String_Ada = Undefined || String_Ada in Boolean  ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "'Ada'.oclIsKindOf(Integer)";
		String expected = "String_Ada = Undefined || String_Ada in ints  ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "'Ada'.oclIsKindOf(String)";
		String expected = "String_Ada = Undefined || String_Ada in String  ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "'Ada'.oclIsKindOf(Color)";
		String expected = "String_Ada = Undefined || String_Ada in Color  ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "'Ada'.oclIsKindOf(Person)";
		String expected = "String_Ada = Undefined || String_Ada in Person  ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "'Ada'.oclIsKindOf(OclAny)";
		String expected = "String_Ada = Undefined || String_Ada in univ  ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "'Ada'.oclIsKindOf(OclVoid)";
		String expected = "String_Ada = Undefined || String_Ada in Undefined   ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "1.oclIsKindOf(Boolean)";
		String expected = "Int[1] = Undefined || Int[1] in Boolean  ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "1.oclIsKindOf(Integer)";
		String expected = "Int[1] = Undefined || Int[1] in ints  ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "1.oclIsKindOf(String)";
		String expected = "Int[1] = Undefined || Int[1] in String  ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "1.oclIsKindOf(Color)";
		String expected = "Int[1] = Undefined || Int[1] in Color  ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "1.oclIsKindOf(Person)";
		String expected = "Int[1] = Undefined || Int[1] in Person  ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "1.oclIsKindOf(OclAny)";
		String expected = "Int[1] = Undefined || Int[1] in univ  ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "1.oclIsKindOf(OclVoid)";
		String expected = "Int[1] = Undefined || Int[1] in Undefined   ";
		test("test49", ocl, expected);
	}

}
