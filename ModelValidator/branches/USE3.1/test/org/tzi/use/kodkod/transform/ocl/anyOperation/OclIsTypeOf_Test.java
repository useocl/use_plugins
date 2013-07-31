package org.tzi.use.kodkod.transform.ocl.anyOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class OclIsTypeOf_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Undefined.oclIsTypeOf(Boolean)";
		String expected = "!(Boolean = univ) && Undefined in Boolean ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Undefined.oclIsTypeOf(Integer)";
		String expected = "!(ints = univ) && Undefined in ints ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Undefined.oclIsTypeOf(String)";
		String expected = "!(String = univ) && Undefined in String ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Undefined.oclIsTypeOf(Color)";
		String expected = "!(Color = univ) && Undefined in Color ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Undefined.oclIsTypeOf(Person)";
		String expected = "!(Person = univ) && Undefined in Person ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Undefined.oclIsTypeOf(OclAny)";
		String expected = "!(univ = univ) && Undefined in univ ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Undefined.oclIsTypeOf(OclVoid)";
		String expected = "!(Undefined = univ) && Undefined in Undefined  ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Color::red.oclIsTypeOf(Boolean)";
		String expected = "!(Boolean = univ) && Color_red in Boolean ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Color::red.oclIsTypeOf(Integer)";
		String expected = "!(ints = univ) && Color_red in ints ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Color::red.oclIsTypeOf(String)";
		String expected = "!(String = univ) && Color_red in String ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Color::red.oclIsTypeOf(Color)";
		String expected = "!(Color = univ) && Color_red in Color ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Color::red.oclIsTypeOf(Person)";
		String expected = "!(Person = univ) && Color_red in Person ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Color::red.oclIsTypeOf(OclAny)";
		String expected = "!(univ = univ) && Color_red in univ ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Color::red.oclIsTypeOf(OclVoid)";
		String expected = "!(Undefined = univ) && Color_red in Undefined  ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "ada.oclIsTypeOf(Boolean)";
		String expected = "!(Boolean = univ) && Person_ada in Boolean ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "ada.oclIsTypeOf(Integer)";
		String expected = "!(ints = univ) && Person_ada in ints ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "ada.oclIsTypeOf(String)";
		String expected = "!(String = univ) && Person_ada in String ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "ada.oclIsTypeOf(Color)";
		String expected = "!(Color = univ) && Person_ada in Color ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "ada.oclIsTypeOf(Person)";
		String expected = "!(Person = univ) && Person_ada in Person ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "ada.oclIsTypeOf(OclAny)";
		String expected = "!(univ = univ) && Person_ada in univ ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "ada.oclIsTypeOf(OclVoid)";
		String expected = "!(Undefined = univ) && Person_ada in Undefined  ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "true.oclIsTypeOf(Boolean)";
		String expected = "!(Boolean = univ) && Boolean_True in Boolean ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "true.oclIsTypeOf(Integer)";
		String expected = "!(ints = univ) && Boolean_True in ints ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "true.oclIsTypeOf(String)";
		String expected = "!(String = univ) && Boolean_True in String ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "true.oclIsTypeOf(Color)";
		String expected = "!(Color = univ) && Boolean_True in Color ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "true.oclIsTypeOf(Person)";
		String expected = "!(Person = univ) && Boolean_True in Person ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "true.oclIsTypeOf(OclAny)";
		String expected = "!(univ = univ) && Boolean_True in univ ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "true.oclIsTypeOf(OclVoid)";
		String expected = "!(Undefined = univ) && Boolean_True in Undefined  ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "(1 = 1).oclIsTypeOf(Boolean)";
		String expected = "Boolean = Boolean ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "(1 = 1).oclIsTypeOf(Integer)";
		String expected = "ints = Boolean ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "(1 = 1).oclIsTypeOf(String)";
		String expected = "String = Boolean ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "(1 = 1).oclIsTypeOf(Color)";
		String expected = "Color = Boolean ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "(1 = 1).oclIsTypeOf(Person)";
		String expected = "Person = Boolean ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "(1 = 1).oclIsTypeOf(OclAny)";
		String expected = "univ = Boolean ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "(1 = 1).oclIsTypeOf(OclVoid)";
		String expected = "Undefined = Boolean ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "'Ada'.oclIsTypeOf(Boolean)";
		String expected = "!(Boolean = univ) && String_Ada in Boolean ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "'Ada'.oclIsTypeOf(Integer)";
		String expected = "!(ints = univ) && String_Ada in ints ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "'Ada'.oclIsTypeOf(String)";
		String expected = "!(String = univ) && String_Ada in String ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "'Ada'.oclIsTypeOf(Color)";
		String expected = "!(Color = univ) && String_Ada in Color ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "'Ada'.oclIsTypeOf(Person)";
		String expected = "!(Person = univ) && String_Ada in Person ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "'Ada'.oclIsTypeOf(OclAny)";
		String expected = "!(univ = univ) && String_Ada in univ ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "'Ada'.oclIsTypeOf(OclVoid)";
		String expected = "!(Undefined = univ) && String_Ada in Undefined  ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "1.oclIsTypeOf(Boolean)";
		String expected = "!(Boolean = univ) && Int[1] in Boolean ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "1.oclIsTypeOf(Integer)";
		String expected = "!(ints = univ) && Int[1] in ints ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "1.oclIsTypeOf(String)";
		String expected = "!(String = univ) && Int[1] in String ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "1.oclIsTypeOf(Color)";
		String expected = "!(Color = univ) && Int[1] in Color ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "1.oclIsTypeOf(Person)";
		String expected = "!(Person = univ) && Int[1] in Person ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "1.oclIsTypeOf(OclAny)";
		String expected = "!(univ = univ) && Int[1] in univ ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "1.oclIsTypeOf(OclVoid)";
		String expected = "!(Undefined = univ) && Int[1] in Undefined  ";
		test("test49", ocl, expected);
	}

}
