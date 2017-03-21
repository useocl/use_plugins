package org.tzi.use.kodkod.transform.ocl.anyOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Equality_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Undefined = Undefined";
		String expected = "Undefined = Undefined  ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Color::red = Color::red";
		String expected = "Color_red = Color_red ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Color::red = Color::green";
		String expected = "Color_red = Color_green ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Color::green = Color::red";
		String expected = "Color_green = Color_red ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Color::red = Undefined";
		String expected = "Color_red = Undefined ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Undefined = Color::red";
		String expected = "Undefined = Color_red  ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "ada = ada";
		String expected = "Person_ada = Person_ada ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "ada = bob";
		String expected = "Person_ada = Person_bob ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "bob = ada";
		String expected = "Person_bob = Person_ada ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "ada = Undefined";
		String expected = "Person_ada = Undefined ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Undefined = ada";
		String expected = "Undefined = Person_ada  ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "true = true";
		String expected = "Boolean_True = Boolean_True ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "false = false";
		String expected = "Boolean_False = Boolean_False ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "true = false";
		String expected = "Boolean_True = Boolean_False ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "false = true";
		String expected = "Boolean_False = Boolean_True ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "true = Undefined";
		String expected = "Boolean_True = Undefined ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Undefined = true";
		String expected = "Undefined = Boolean_True ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "false = Undefined";
		String expected = "Boolean_False = Undefined ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Undefined = false";
		String expected = "Undefined = Boolean_False  ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "('Ada' = 'Ada') = true";
		String expected = "(String_Ada = String_Ada && Boolean_True = Boolean_True) || (!(String_Ada = String_Ada) && Boolean_True = Boolean_False) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "true = ('Ada' = 'Ada')";
		String expected = "(Boolean_True = Boolean_True && String_Ada = String_Ada) || (Boolean_True = Boolean_False && !(String_Ada = String_Ada)) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "('Ada' = 'Bob') = true";
		String expected = "(String_Ada = String_Bob && Boolean_True = Boolean_True) || (!(String_Ada = String_Bob) && Boolean_True = Boolean_False) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "true = ('Ada' = 'Bob')";
		String expected = "(Boolean_True = Boolean_True && String_Ada = String_Bob) || (Boolean_True = Boolean_False && !(String_Ada = String_Bob))  ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "('Ada' = 'Ada') = false";
		String expected = "(String_Ada = String_Ada && Boolean_False = Boolean_True) || (!(String_Ada = String_Ada) && Boolean_False = Boolean_False) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "false = ('Ada' = 'Ada')";
		String expected = "(Boolean_False = Boolean_True && String_Ada = String_Ada) || (Boolean_False = Boolean_False && !(String_Ada = String_Ada)) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "('Ada' = 'Bob') = false";
		String expected = "(String_Ada = String_Bob && Boolean_False = Boolean_True) || (!(String_Ada = String_Bob) && Boolean_False = Boolean_False) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "false = ('Ada' = 'Bob')";
		String expected = "(Boolean_False = Boolean_True && String_Ada = String_Bob) || (Boolean_False = Boolean_False && !(String_Ada = String_Bob))  ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "('Ada' = 'Ada') = Undefined";
		String expected = "(String_Ada = String_Ada && Undefined = Boolean_True) || (!(String_Ada = String_Ada) && Undefined = Boolean_False) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Undefined = ('Ada' = 'Ada')";
		String expected = "(Undefined = Boolean_True && String_Ada = String_Ada) || (Undefined = Boolean_False && !(String_Ada = String_Ada)) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "('Ada' = 'Bob') = Undefined";
		String expected = "(String_Ada = String_Bob && Undefined = Boolean_True) || (!(String_Ada = String_Bob) && Undefined = Boolean_False) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Undefined = ('Ada' = 'Bob')";
		String expected = "(Undefined = Boolean_True && String_Ada = String_Bob) || (Undefined = Boolean_False && !(String_Ada = String_Bob))   ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "('Ada' = 'Ada') = ('Ada' = 'Ada')";
		String expected = "String_Ada = String_Ada <=> String_Ada = String_Ada ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "('Ada' = 'Ada') = ('Ada' = 'Bob')";
		String expected = "String_Ada = String_Ada <=> String_Ada = String_Bob ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "('Ada' = 'Bob') = ('Ada' = 'Ada')";
		String expected = "String_Ada = String_Bob <=> String_Ada = String_Ada ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "('Ada' = 'Bob') = ('Ada' = 'Bob')";
		String expected = "String_Ada = String_Bob <=> String_Ada = String_Bob   ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "'Ada' = 'Ada'";
		String expected = "String_Ada = String_Ada ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "'Ada' = 'Bob'";
		String expected = "String_Ada = String_Bob ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "'Bob' = 'Ada'";
		String expected = "String_Bob = String_Ada ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "'Ada' = Undefined";
		String expected = "String_Ada = Undefined ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "Undefined = 'Ada'";
		String expected = "Undefined = String_Ada  ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "-1 = -1";
		String expected = "Int[-1] = Int[-1] ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "0 = 0";
		String expected = "Int[0] = Int[0] ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "1 = 1";
		String expected = "Int[1] = Int[1] ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "0 = 1";
		String expected = "Int[0] = Int[1] ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "1 = 0";
		String expected = "Int[1] = Int[0] ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "0 = -1";
		String expected = "Int[0] = Int[-1] ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "-1 = 0";
		String expected = "Int[-1] = Int[0] ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "-1 = 1";
		String expected = "Int[-1] = Int[1] ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "1 = -1";
		String expected = "Int[1] = Int[-1] ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "-1 = Undefined";
		String expected = "Int[-1] = Undefined ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "Undefined = -1";
		String expected = "Undefined = Int[-1] ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "0 = Undefined";
		String expected = "Int[0] = Undefined ";
		test("test52", ocl, expected);
	}

	@Test
	public void test53() {
		String ocl = "Undefined = 0";
		String expected = "Undefined = Int[0] ";
		test("test53", ocl, expected);
	}

	@Test
	public void test54() {
		String ocl = "1 = Undefined";
		String expected = "Int[1] = Undefined ";
		test("test54", ocl, expected);
	}

	@Test
	public void test55() {
		String ocl = "Undefined = 1";
		String expected = "Undefined = Int[1]  ";
		test("test55", ocl, expected);
	}

	@Test
	public void test56() {
		String ocl = "Color::red = ada";
		String expected = "Color_red = Person_ada ";
		test("test56", ocl, expected);
	}

	@Test
	public void test57() {
		String ocl = "ada = Color::red";
		String expected = "Person_ada = Color_red  ";
		test("test57", ocl, expected);
	}

	@Test
	public void test58() {
		String ocl = "Color::red = true";
		String expected = "Color_red = Boolean_True ";
		test("test58", ocl, expected);
	}

	@Test
	public void test59() {
		String ocl = "Color::red = false";
		String expected = "Color_red = Boolean_False ";
		test("test59", ocl, expected);
	}

	@Test
	public void test60() {
		String ocl = "true = Color::red";
		String expected = "Boolean_True = Color_red ";
		test("test60", ocl, expected);
	}

	@Test
	public void test61() {
		String ocl = "false = Color::red";
		String expected = "Boolean_False = Color_red  ";
		test("test61", ocl, expected);
	}

	@Test
	public void test62() {
		String ocl = "Color::red = ('Ada' = 'Ada')";
		String expected = "(Color_red = Boolean_True && String_Ada = String_Ada) || (Color_red = Boolean_False && !(String_Ada = String_Ada)) ";
		test("test62", ocl, expected);
	}

	@Test
	public void test63() {
		String ocl = "Color::red = ('Ada' = 'Bob')";
		String expected = "(Color_red = Boolean_True && String_Ada = String_Bob) || (Color_red = Boolean_False && !(String_Ada = String_Bob))  ";
		test("test63", ocl, expected);
	}

	@Test
	public void test64() {
		String ocl = "('Ada' = 'Ada') = Color::red";
		String expected = "(String_Ada = String_Ada && Color_red = Boolean_True) || (!(String_Ada = String_Ada) && Color_red = Boolean_False) ";
		test("test64", ocl, expected);
	}

	@Test
	public void test65() {
		String ocl = "('Ada' = 'Bob') = Color::red";
		String expected = "(String_Ada = String_Bob && Color_red = Boolean_True) || (!(String_Ada = String_Bob) && Color_red = Boolean_False)   ";
		test("test65", ocl, expected);
	}

	@Test
	public void test66() {
		String ocl = "Color::red = 'Ada'";
		String expected = "Color_red = String_Ada ";
		test("test66", ocl, expected);
	}

	@Test
	public void test67() {
		String ocl = "'Ada' = Color::red";
		String expected = "String_Ada = Color_red  ";
		test("test67", ocl, expected);
	}

	@Test
	public void test68() {
		String ocl = "Color::red = 0";
		String expected = "Color_red = Int[0] ";
		test("test68", ocl, expected);
	}

	@Test
	public void test69() {
		String ocl = "0 = Color::red";
		String expected = "Int[0] = Color_red  ";
		test("test69", ocl, expected);
	}

	@Test
	public void test70() {
		String ocl = "ada = true";
		String expected = "Person_ada = Boolean_True ";
		test("test70", ocl, expected);
	}

	@Test
	public void test71() {
		String ocl = "ada = false";
		String expected = "Person_ada = Boolean_False ";
		test("test71", ocl, expected);
	}

	@Test
	public void test72() {
		String ocl = "true = ada";
		String expected = "Boolean_True = Person_ada ";
		test("test72", ocl, expected);
	}

	@Test
	public void test73() {
		String ocl = "false = ada";
		String expected = "Boolean_False = Person_ada  ";
		test("test73", ocl, expected);
	}

	@Test
	public void test74() {
		String ocl = "ada = ('Ada' = 'Ada')";
		String expected = "(Person_ada = Boolean_True && String_Ada = String_Ada) || (Person_ada = Boolean_False && !(String_Ada = String_Ada)) ";
		test("test74", ocl, expected);
	}

	@Test
	public void test75() {
		String ocl = "ada = ('Ada' = 'Bob')";
		String expected = "(Person_ada = Boolean_True && String_Ada = String_Bob) || (Person_ada = Boolean_False && !(String_Ada = String_Bob))  ";
		test("test75", ocl, expected);
	}

	@Test
	public void test76() {
		String ocl = "('Ada' = 'Ada') = ada";
		String expected = "(String_Ada = String_Ada && Person_ada = Boolean_True) || (!(String_Ada = String_Ada) && Person_ada = Boolean_False) ";
		test("test76", ocl, expected);
	}

	@Test
	public void test77() {
		String ocl = "('Ada' = 'Bob') = ada";
		String expected = "(String_Ada = String_Bob && Person_ada = Boolean_True) || (!(String_Ada = String_Bob) && Person_ada = Boolean_False)   ";
		test("test77", ocl, expected);
	}

	@Test
	public void test78() {
		String ocl = "ada = 'Ada'";
		String expected = "Person_ada = String_Ada ";
		test("test78", ocl, expected);
	}

	@Test
	public void test79() {
		String ocl = "'Ada' = ada";
		String expected = "String_Ada = Person_ada  ";
		test("test79", ocl, expected);
	}

	@Test
	public void test80() {
		String ocl = "ada = 0";
		String expected = "Person_ada = Int[0] ";
		test("test80", ocl, expected);
	}

	@Test
	public void test81() {
		String ocl = "0 = ada";
		String expected = "Int[0] = Person_ada  ";
		test("test81", ocl, expected);
	}

	@Test
	public void test82() {
		String ocl = "true = 'Ada'";
		String expected = "Boolean_True = String_Ada ";
		test("test82", ocl, expected);
	}

	@Test
	public void test83() {
		String ocl = "false = 'Ada'";
		String expected = "Boolean_False = String_Ada ";
		test("test83", ocl, expected);
	}

	@Test
	public void test84() {
		String ocl = "'Ada' = true";
		String expected = "String_Ada = Boolean_True ";
		test("test84", ocl, expected);
	}

	@Test
	public void test85() {
		String ocl = "'Ada' = false";
		String expected = "String_Ada = Boolean_False  ";
		test("test85", ocl, expected);
	}

	@Test
	public void test86() {
		String ocl = "true = 0";
		String expected = "Boolean_True = Int[0] ";
		test("test86", ocl, expected);
	}

	@Test
	public void test87() {
		String ocl = "false = 0";
		String expected = "Boolean_False = Int[0] ";
		test("test87", ocl, expected);
	}

	@Test
	public void test88() {
		String ocl = "0 = false";
		String expected = "Int[0] = Boolean_False ";
		test("test88", ocl, expected);
	}

	@Test
	public void test89() {
		String ocl = "0 = true";
		String expected = "Int[0] = Boolean_True  ";
		test("test89", ocl, expected);
	}

	@Test
	public void test90() {
		String ocl = "('Ada' = 'Ada') = 'Ada'";
		String expected = "(String_Ada = String_Ada && String_Ada = Boolean_True) || (!(String_Ada = String_Ada) && String_Ada = Boolean_False) ";
		test("test90", ocl, expected);
	}

	@Test
	public void test91() {
		String ocl = "('Ada' = 'Bob') = 'Ada'";
		String expected = "(String_Ada = String_Bob && String_Ada = Boolean_True) || (!(String_Ada = String_Bob) && String_Ada = Boolean_False)  ";
		test("test91", ocl, expected);
	}

	@Test
	public void test92() {
		String ocl = "'Ada' = ('Ada' = 'Ada')";
		String expected = "(String_Ada = Boolean_True && String_Ada = String_Ada) || (String_Ada = Boolean_False && !(String_Ada = String_Ada)) ";
		test("test92", ocl, expected);
	}

	@Test
	public void test93() {
		String ocl = "'Ada' = ('Ada' = 'Bob')";
		String expected = "(String_Ada = Boolean_True && String_Ada = String_Bob) || (String_Ada = Boolean_False && !(String_Ada = String_Bob))   ";
		test("test93", ocl, expected);
	}

	@Test
	public void test94() {
		String ocl = "('Ada' = 'Ada') = 0";
		String expected = "(String_Ada = String_Ada && Int[0] = Boolean_True) || (!(String_Ada = String_Ada) && Int[0] = Boolean_False) ";
		test("test94", ocl, expected);
	}

	@Test
	public void test95() {
		String ocl = "('Ada' = 'Bob') = 0";
		String expected = "(String_Ada = String_Bob && Int[0] = Boolean_True) || (!(String_Ada = String_Bob) && Int[0] = Boolean_False)  ";
		test("test95", ocl, expected);
	}

	@Test
	public void test96() {
		String ocl = "0 = ('Ada' = 'Ada')";
		String expected = "(Int[0] = Boolean_True && String_Ada = String_Ada) || (Int[0] = Boolean_False && !(String_Ada = String_Ada)) ";
		test("test96", ocl, expected);
	}

	@Test
	public void test97() {
		String ocl = "0 = ('Ada' = 'Bob')";
		String expected = "(Int[0] = Boolean_True && String_Ada = String_Bob) || (Int[0] = Boolean_False && !(String_Ada = String_Bob))   ";
		test("test97", ocl, expected);
	}

	@Test
	public void test98() {
		String ocl = "'Ada' = 0";
		String expected = "String_Ada = Int[0] ";
		test("test98", ocl, expected);
	}

	@Test
	public void test99() {
		String ocl = "0 = 'Ada'";
		String expected = "Int[0] = String_Ada ";
		test("test99", ocl, expected);
	}

}
