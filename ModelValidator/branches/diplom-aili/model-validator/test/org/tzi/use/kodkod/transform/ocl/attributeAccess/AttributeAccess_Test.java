package org.tzi.use.kodkod.transform.ocl.attributeAccess;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class AttributeAccess_Test extends OCLTest {
	
	@Test
	public void test1() {
		String ocl = "ada.name";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Person_name) ";
		test("test1", ocl, expected);
	}

	
	@Test
	public void test2() {
		String ocl = "bob.name";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . Person_name) ";
		test("test2", ocl, expected);
	}

	
	@Test
	public void test3() {
		String ocl = "ada.nicknames";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_nicknames) ";
		test("test3", ocl, expected);
	}

	
	@Test
	public void test4() {
		String ocl = "bob.nicknames";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . Person_nicknames) ";
		test("test4", ocl, expected);
	}

	
	@Test
	public void test5() {
		String ocl = "cyd.nicknames";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_nicknames) ";
		test("test5", ocl, expected);
	}

	
	@Test
	public void test6() {
		String ocl = "dan.nicknames";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (Person_dan . Person_nicknames) ";
		test("test6", ocl, expected);
	}

	
	@Test
	public void test7() {
		String ocl = "eve.nicknames";
		String expected = "(Person_eve = Undefined) => Undefined_Set else (Person_eve . Person_nicknames) ";
		test("test7", ocl, expected);
	}

	
	@Test
	public void test8() {
		String ocl = "ada.bestFriend";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend) ";
		test("test8", ocl, expected);
	}

	
	@Test
	public void test9() {
		String ocl = "bob.bestFriend";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend) ";
		test("test9", ocl, expected);
	}

	
	@Test
	public void test10() {
		String ocl = "ada.age";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Person_age) ";
		test("test10", ocl, expected);
	}

	
	@Test
	public void test11() {
		String ocl = "bob.age";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . Person_age) ";
		test("test11", ocl, expected);
	}

	
	@Test
	public void test12() {
		String ocl = "ada.alive";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Person_alive) ";
		test("test12", ocl, expected);
	}

	
	@Test
	public void test13() {
		String ocl = "bob.alive";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . Person_alive) ";
		test("test13", ocl, expected);
	}

	
	@Test
	public void test14() {
		String ocl = "cyd.alive";
		String expected = "(Person_cyd = Undefined) => Undefined else (Person_cyd . Person_alive) ";
		test("test14", ocl, expected);
	}

	
	@Test
	public void test15() {
		String ocl = "ada.luckyNumbers";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . Person_luckyNumbers) ";
		test("test15", ocl, expected);
	}

	
	@Test
	public void test16() {
		String ocl = "bob.luckyNumbers";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . Person_luckyNumbers) ";
		test("test16", ocl, expected);
	}

	
	@Test
	public void test17() {
		String ocl = "cyd.luckyNumbers";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers) ";
		test("test17", ocl, expected);
	}

	
	@Test
	public void test18() {
		String ocl = "dan.luckyNumbers";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (Person_dan . Person_luckyNumbers) ";
		test("test18", ocl, expected);
	}

	
	@Test
	public void test19() {
		String ocl = "eve.luckyNumbers";
		String expected = "(Person_eve = Undefined) => Undefined_Set else (Person_eve . Person_luckyNumbers) ";
		test("test19", ocl, expected);
	}

	
	@Test
	public void test20() {
		String ocl = "ada.favoriteColor";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Person_favoriteColor) ";
		test("test20", ocl, expected);
	}

	
	@Test
	public void test21() {
		String ocl = "bob.favoriteColor";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . Person_favoriteColor) ";
		test("test21", ocl, expected);
	}

	
	@Test
	public void test22() {
		String ocl = "ada.bestFriend.name";
		String expected = "(((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) = Undefined) => Undefined else (((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) . Person_name) ";
		test("test22", ocl, expected);
	}

	
	@Test
	public void test23() {
		String ocl = "ada.bestFriend.nicknames";
		String expected = "(((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) = Undefined) => Undefined_Set else (((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) . Person_nicknames) ";
		test("test23", ocl, expected);
	}

	
	@Test
	public void test24() {
		String ocl = "ada.bestFriend.bestFriend";
		String expected = "(((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) = Undefined) => Undefined else (((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) . Person_bestFriend) ";
		test("test24", ocl, expected);
	}

	
	@Test
	public void test25() {
		String ocl = "ada.bestFriend.age";
		String expected = "(((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) = Undefined) => Undefined else (((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) . Person_age) ";
		test("test25", ocl, expected);
	}

	
	@Test
	public void test26() {
		String ocl = "ada.bestFriend.alive";
		String expected = "(((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) = Undefined) => Undefined else (((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) . Person_alive) ";
		test("test26", ocl, expected);
	}

	
	@Test
	public void test27() {
		String ocl = "ada.bestFriend.luckyNumbers";
		String expected = "(((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) = Undefined) => Undefined_Set else (((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) . Person_luckyNumbers) ";
		test("test27", ocl, expected);
	}

	
	@Test
	public void test28() {
		String ocl = "ada.bestFriend.favoriteColor";
		String expected = "(((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) = Undefined) => Undefined else (((Person_ada = Undefined) => Undefined else (Person_ada . Person_bestFriend)) . Person_favoriteColor) ";
		test("test28", ocl, expected);
	}

	
	@Test
	public void test29() {
		String ocl = "bob.bestFriend.name";
		String expected = "(((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) = Undefined) => Undefined else (((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) . Person_name) ";
		test("test29", ocl, expected);
	}

	
	@Test
	public void test30() {
		String ocl = "bob.bestFriend.nicknames";
		String expected = "(((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) = Undefined) => Undefined_Set else (((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) . Person_nicknames) ";
		test("test30", ocl, expected);
	}

	
	@Test
	public void test31() {
		String ocl = "bob.bestFriend.bestFriend";
		String expected = "(((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) = Undefined) => Undefined else (((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) . Person_bestFriend) ";
		test("test31", ocl, expected);
	}

	
	@Test
	public void test32() {
		String ocl = "bob.bestFriend.age";
		String expected = "(((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) = Undefined) => Undefined else (((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) . Person_age) ";
		test("test32", ocl, expected);
	}

	
	@Test
	public void test33() {
		String ocl = "bob.bestFriend.alive";
		String expected = "(((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) = Undefined) => Undefined else (((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) . Person_alive) ";
		test("test33", ocl, expected);
	}

	
	@Test
	public void test34() {
		String ocl = "bob.bestFriend.luckyNumbers";
		String expected = "(((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) = Undefined) => Undefined_Set else (((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) . Person_luckyNumbers) ";
		test("test34", ocl, expected);
	}

	
	@Test
	public void test35() {
		String ocl = "bob.bestFriend.favoriteColor";
		String expected = "(((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) = Undefined) => Undefined else (((Person_bob = Undefined) => Undefined else (Person_bob . Person_bestFriend)) . Person_favoriteColor) ";
		test("test35", ocl, expected);
	}

	
	@Test
	public void test36() {
		String ocl = "cyd.bestFriend.name";
		String expected = "(((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) = Undefined) => Undefined else (((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) . Person_name) ";
		test("test36", ocl, expected);
	}

	
	@Test
	public void test37() {
		String ocl = "cyd.bestFriend.nicknames";
		String expected = "(((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) = Undefined) => Undefined_Set else (((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) . Person_nicknames) ";
		test("test37", ocl, expected);
	}

	
	@Test
	public void test38() {
		String ocl = "cyd.bestFriend.bestFriend";
		String expected = "(((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) = Undefined) => Undefined else (((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) . Person_bestFriend) ";
		test("test38", ocl, expected);
	}

	
	@Test
	public void test39() {
		String ocl = "cyd.bestFriend.age";
		String expected = "(((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) = Undefined) => Undefined else (((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) . Person_age) ";
		test("test39", ocl, expected);
	}

	
	@Test
	public void test40() {
		String ocl = "cyd.bestFriend.alive";
		String expected = "(((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) = Undefined) => Undefined else (((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) . Person_alive) ";
		test("test40", ocl, expected);
	}

	
	@Test
	public void test41() {
		String ocl = "cyd.bestFriend.luckyNumbers";
		String expected = "(((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) = Undefined) => Undefined_Set else (((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) . Person_luckyNumbers) ";
		test("test41", ocl, expected);
	}

	
	@Test
	public void test42() {
		String ocl = "cyd.bestFriend.favoriteColor";
		String expected = "(((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) = Undefined) => Undefined else (((Person_cyd = Undefined) => Undefined else (Person_cyd . Person_bestFriend)) . Person_favoriteColor) ";
		test("test42", ocl, expected);
	}

}
