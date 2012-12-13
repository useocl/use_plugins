package org.tzi.use.kodkod.transform.ocl.setOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class IsUnique_Test extends OCLTest {

	@Test
	public void test1() {
		String ocl = "Set{}->isUnique(i | i)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | !(i1 = i2) => !(i1 = i2)) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{}->isUnique(i | true)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Set{}->isUnique(i | false)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "Set{}->isUnique(i | i = 1)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{}->isUnique(i | i <> 1)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "Set{}->isUnique(i | Undefined)";
		String expected = "!(none = Undefined_Set) && (all i1: none, i2: none | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "Set{ada}->select(false)->isUnique(p | p)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(p1 = p2))";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "Set{ada}->select(false)->isUnique(p | p.name)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_name)) = ((p2 = Undefined) => Undefined else (p2 . Person_name))))";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "Set{ada}->select(false)->isUnique(p | p.nicknames)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_nicknames)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_nicknames))))";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "Set{ada}->select(false)->isUnique(p | p.bestFriend)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_bestFriend)) = ((p2 = Undefined) => Undefined else (p2 . Person_bestFriend))))";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "Set{ada}->select(false)->isUnique(p | p.age)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_age)) = ((p2 = Undefined) => Undefined else (p2 . Person_age))))";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "Set{ada}->select(false)->isUnique(p | p.alive)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive))))";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "Set{ada}->select(false)->isUnique(p | p.luckyNumbers)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers))))";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "Set{ada}->select(false)->isUnique(p | Undefined)";
		String expected = "!(((((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True}, p2: (((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) => Undefined_Set else {$elem0: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | Boolean_False = Boolean_True} | !(p1 = p2) => !(Undefined = Undefined))";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{1}->isUnique(i | i)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i1 = i2) => !(i1 = i2)) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "Set{1}->isUnique(i | true)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "Set{1}->isUnique(i | false)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "Set{1}->isUnique(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "Set{1}->isUnique(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "Set{1}->isUnique(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]), i2: (Undefined_Set in (none + Int[1])) => Undefined_Set else (none + Int[1]) | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "Set{ada}->isUnique(p | p)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(p1 = p2)) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "Set{ada}->isUnique(p | p.name)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_name)) = ((p2 = Undefined) => Undefined else (p2 . Person_name)))) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{ada}->isUnique(p | p.nicknames)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_nicknames)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_nicknames)))) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "Set{ada}->isUnique(p | p.bestFriend)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_bestFriend)) = ((p2 = Undefined) => Undefined else (p2 . Person_bestFriend)))) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "Set{ada}->isUnique(p | p.age)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_age)) = ((p2 = Undefined) => Undefined else (p2 . Person_age)))) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "Set{ada}->isUnique(p | p.alive)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive)))) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "Set{ada}->isUnique(p | p.luckyNumbers)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers)))) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "Set{ada}->isUnique(p | Undefined)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(Undefined = Undefined)) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "Set{1,2}->isUnique(i | i)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i1 = i2) => !(i1 = i2)) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "Set{1,2}->isUnique(i | true)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "Set{1,2}->isUnique(i | false)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "Set{1,2}->isUnique(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "Set{1,2}->isUnique(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "Set{1,2}->isUnique(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]), i2: (Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2]) | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "Set{ada,bob}->isUnique(p | p)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(p1 = p2)) ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "Set{ada,bob}->isUnique(p | p.name)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_name)) = ((p2 = Undefined) => Undefined else (p2 . Person_name)))) ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "Set{ada,bob}->isUnique(p | p.nicknames)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_nicknames)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_nicknames)))) ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "Set{ada,bob}->isUnique(p | p.bestFriend)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_bestFriend)) = ((p2 = Undefined) => Undefined else (p2 . Person_bestFriend)))) ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "Set{ada,bob}->isUnique(p | p.age)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_age)) = ((p2 = Undefined) => Undefined else (p2 . Person_age)))) ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "Set{ada,bob}->isUnique(p | p.alive)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive)))) ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "Set{ada,bob}->isUnique(p | p.luckyNumbers)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers)))) ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "Set{bob,cyd}->isUnique(p | p.luckyNumbers)";
		String expected = "!(((Undefined_Set in (none + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_bob + Person_cyd) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers)))) ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "Set{ada,bob}->isUnique(p | Undefined)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob), p2: (Undefined_Set in (none + Person_ada + Person_bob)) => Undefined_Set else (none + Person_ada + Person_bob) | !(p1 = p2) => !(Undefined = Undefined)) ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "Set{1,2,3}->isUnique(i | i)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i1 = i2) => !(i1 = i2)) ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "Set{1,2,3}->isUnique(i | true)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "Set{1,2,3}->isUnique(i | false)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "Set{1,2,3}->isUnique(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "Set{1,2,3}->isUnique(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "Set{1,2,3}->isUnique(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]), i2: (Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]) | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | p)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(p1 = p2)) ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | p.name)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_name)) = ((p2 = Undefined) => Undefined else (p2 . Person_name)))) ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | p.nicknames)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_nicknames)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_nicknames)))) ";
		test("test52", ocl, expected);
	}

	@Test
	public void test53() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | p.bestFriend)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_bestFriend)) = ((p2 = Undefined) => Undefined else (p2 . Person_bestFriend)))) ";
		test("test53", ocl, expected);
	}

	@Test
	public void test54() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | p.age)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_age)) = ((p2 = Undefined) => Undefined else (p2 . Person_age)))) ";
		test("test54", ocl, expected);
	}

	@Test
	public void test55() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | p.alive)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive)))) ";
		test("test55", ocl, expected);
	}

	@Test
	public void test56() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | p.luckyNumbers)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers)))) ";
		test("test56", ocl, expected);
	}

	@Test
	public void test57() {
		String ocl = "Set{ada,bob,cyd}->isUnique(p | Undefined)";
		String expected = "!(((Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd), p2: (Undefined_Set in (none + Person_ada + Person_bob + Person_cyd)) => Undefined_Set else (none + Person_ada + Person_bob + Person_cyd) | !(p1 = p2) => !(Undefined = Undefined)) ";
		test("test57", ocl, expected);
	}

	@Test
	public void test58() {
		String ocl = "Set{ada}->isUnique(p | p.alive)";
		String expected = "!(((Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada), p2: (Undefined_Set in (none + Person_ada)) => Undefined_Set else (none + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive)))) ";
		test("test58", ocl, expected);
	}

	@Test
	public void test59() {
		String ocl = "Set{Undefined,1}->isUnique(i | i)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | !(i1 = i2) => !(i1 = i2)) ";
		test("test59", ocl, expected);
	}

	@Test
	public void test60() {
		String ocl = "Set{Undefined,1}->isUnique(i | true)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test60", ocl, expected);
	}

	@Test
	public void test61() {
		String ocl = "Set{Undefined,1}->isUnique(i | false)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test61", ocl, expected);
	}

	@Test
	public void test62() {
		String ocl = "Set{Undefined,1}->isUnique(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test62", ocl, expected);
	}

	@Test
	public void test63() {
		String ocl = "Set{Undefined,1}->isUnique(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test63", ocl, expected);
	}

	@Test
	public void test64() {
		String ocl = "Set{Undefined,1}->isUnique(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1])) => Undefined_Set else (none + Undefined + Int[1]), i2: (Undefined_Set in (none + Undefined + Int[1]) ) => Undefined_Set else (none + Undefined + Int[1]) | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test64", ocl, expected);
	}

	@Test
	public void test65() {
		String ocl = "Set{Undefined,ada}->isUnique(p | p)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(p1 = p2)) ";
		test("test65", ocl, expected);
	}

	@Test
	public void test66() {
		String ocl = "Set{Undefined,ada}->isUnique(p | p.name)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_name)) = ((p2 = Undefined) => Undefined else (p2 . Person_name)))) ";
		test("test66", ocl, expected);
	}

	@Test
	public void test67() {
		String ocl = "Set{Undefined,ada}->isUnique(p | p.nicknames)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_nicknames)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_nicknames)))) ";
		test("test67", ocl, expected);
	}

	@Test
	public void test68() {
		String ocl = "Set{Undefined,ada}->isUnique(p | p.bestFriend)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_bestFriend)) = ((p2 = Undefined) => Undefined else (p2 . Person_bestFriend)))) ";
		test("test68", ocl, expected);
	}

	@Test
	public void test69() {
		String ocl = "Set{Undefined,ada}->isUnique(p | p.age)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_age)) = ((p2 = Undefined) => Undefined else (p2 . Person_age)))) ";
		test("test69", ocl, expected);
	}

	@Test
	public void test70() {
		String ocl = "Set{Undefined,ada}->isUnique(p | p.alive)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive)))) ";
		test("test70", ocl, expected);
	}

	@Test
	public void test71() {
		String ocl = "Set{Undefined,ada}->isUnique(p | p.luckyNumbers)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers)))) ";
		test("test71", ocl, expected);
	}

	@Test
	public void test72() {
		String ocl = "Set{Undefined,ada}->isUnique(p | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada), p2: (Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada) | !(p1 = p2) => !(Undefined = Undefined)) ";
		test("test72", ocl, expected);
	}

	@Test
	public void test73() {
		String ocl = "Set{Undefined,1,2}->isUnique(i | i)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i1 = i2) => !(i1 = i2)) ";
		test("test73", ocl, expected);
	}

	@Test
	public void test74() {
		String ocl = "Set{Undefined,1,2}->isUnique(i | true)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test74", ocl, expected);
	}

	@Test
	public void test75() {
		String ocl = "Set{Undefined,1,2}->isUnique(i | false)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test75", ocl, expected);
	}

	@Test
	public void test76() {
		String ocl = "Set{Undefined,1,2}->isUnique(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test76", ocl, expected);
	}

	@Test
	public void test77() {
		String ocl = "Set{Undefined,1,2}->isUnique(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test77", ocl, expected);
	}

	@Test
	public void test78() {
		String ocl = "Set{Undefined,1,2}->isUnique(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2])) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]), i2: (Undefined_Set in (none + Undefined + Int[1] + Int[2])) => Undefined_Set else (none + Undefined + Int[1] + Int[2]) | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test78", ocl, expected);
	}

	@Test
	public void test79() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | p)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(p1 = p2)) ";
		test("test79", ocl, expected);
	}

	@Test
	public void test80() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | p.name)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_name)) = ((p2 = Undefined) => Undefined else (p2 . Person_name)))) ";
		test("test80", ocl, expected);
	}

	@Test
	public void test81() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | p.nicknames)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_nicknames)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_nicknames)))) ";
		test("test81", ocl, expected);
	}

	@Test
	public void test82() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | p.bestFriend)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_bestFriend)) = ((p2 = Undefined) => Undefined else (p2 . Person_bestFriend)))) ";
		test("test82", ocl, expected);
	}

	@Test
	public void test83() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | p.age)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_age)) = ((p2 = Undefined) => Undefined else (p2 . Person_age)))) ";
		test("test83", ocl, expected);
	}

	@Test
	public void test84() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | p.alive)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive)))) ";
		test("test84", ocl, expected);
	}

	@Test
	public void test85() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | p.luckyNumbers)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers)))) ";
		test("test85", ocl, expected);
	}

	@Test
	public void test86() {
		String ocl = "Set{Undefined,ada,bob}->isUnique(p | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob)) = Undefined_Set) && (all p1: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob), p2: (Undefined_Set in (none + Undefined + Person_ada + Person_bob)) => Undefined_Set else (none + Undefined + Person_ada + Person_bob) | !(p1 = p2) => !(Undefined = Undefined)) ";
		test("test86", ocl, expected);
	}

	@Test
	public void test87() {
		String ocl = "Set{Undefined}->isUnique(i | i)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i1 = i2) => !(i1 = i2)) ";
		test("test87", ocl, expected);
	}

	@Test
	public void test88() {
		String ocl = "Set{Undefined}->isUnique(i | true)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test88", ocl, expected);
	}

	@Test
	public void test89() {
		String ocl = "Set{Undefined}->isUnique(i | false)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test89", ocl, expected);
	}

	@Test
	public void test90() {
		String ocl = "Set{Undefined}->isUnique(i | i = 1)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test90", ocl, expected);
	}

	@Test
	public void test91() {
		String ocl = "Set{Undefined}->isUnique(i | i <> 1)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test91", ocl, expected);
	}

	@Test
	public void test92() {
		String ocl = "Set{Undefined}->isUnique(i | Undefined)";
		String expected = "!(((Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined)) = Undefined_Set) && (all i1: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined), i2: (Undefined_Set in (none + Undefined)) => Undefined_Set else (none + Undefined) | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test92", ocl, expected);
	}

	@Test
	public void test93() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | p)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(p1 = p2))";
		test("test93", ocl, expected);
	}

	@Test
	public void test94() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | p.name)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_name)) = ((p2 = Undefined) => Undefined else (p2 . Person_name)))) ";
		test("test94", ocl, expected);
	}

	@Test
	public void test95() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | p.nicknames)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_nicknames)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_nicknames)))) ";
		test("test95", ocl, expected);
	}

	@Test
	public void test96() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | p.bestFriend)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_bestFriend)) = ((p2 = Undefined) => Undefined else (p2 . Person_bestFriend)))) ";
		test("test96", ocl, expected);
	}

	@Test
	public void test97() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | p.age)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_age)) = ((p2 = Undefined) => Undefined else (p2 . Person_age)))) ";
		test("test97", ocl, expected);
	}

	@Test
	public void test98() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | p.alive)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined else (p1 . Person_alive)) = ((p2 = Undefined) => Undefined else (p2 . Person_alive)))) ";
		test("test98", ocl, expected);
	}

	@Test
	public void test99() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | p.luckyNumbers)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(((p1 = Undefined) => Undefined_Set else (p1 . Person_luckyNumbers)) = ((p2 = Undefined) => Undefined_Set else (p2 . Person_luckyNumbers)))) ";
		test("test99", ocl, expected);
	}

	@Test
	public void test100() {
		String ocl = "Set{Undefined,ada}->excluding(ada)->isUnique(p | Undefined)";
		String expected = "!(((((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada)) = Undefined_Set) && (all p1: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada), p2: (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Undefined + Person_ada)) => Undefined_Set else (none + Undefined + Person_ada)) - Person_ada) | !(p1 = p2) => !(Undefined = Undefined)) ";
		test("test100", ocl, expected);
	}

	@Test
	public void test101() {
		String ocl = "cyd.luckyNumbers->isUnique(i | i)";
		String expected = "!(((Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers), i2: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers) | !(i1 = i2) => !(i1 = i2)) ";
		test("test101", ocl, expected);
	}

	@Test
	public void test102() {
		String ocl = "cyd.luckyNumbers->isUnique(i | true)";
		String expected = "!(((Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers), i2: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers) | !(i1 = i2) => !(Boolean_True = Boolean_True)) ";
		test("test102", ocl, expected);
	}

	@Test
	public void test103() {
		String ocl = "cyd.luckyNumbers->isUnique(i | false)";
		String expected = "!(((Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers), i2: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers) | !(i1 = i2) => !(Boolean_False = Boolean_False)) ";
		test("test103", ocl, expected);
	}

	@Test
	public void test104() {
		String ocl = "cyd.luckyNumbers->isUnique(i | i = 1)";
		String expected = "!(((Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers), i2: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers) | !(i1 = i2) => !(i1 = Int[1] <=> i2 = Int[1])) ";
		test("test104", ocl, expected);
	}

	@Test
	public void test105() {
		String ocl = "cyd.luckyNumbers->isUnique(i | i <> 1)";
		String expected = "!(((Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers), i2: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers) | !(i1 = i2) => !(!(i1 = Int[1]) <=> !(i2 = Int[1]))) ";
		test("test105", ocl, expected);
	}

	@Test
	public void test106() {
		String ocl = "cyd.luckyNumbers->isUnique(i | Undefined)";
		String expected = "!(((Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers)) = Undefined_Set) && (all i1: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers), i2: (Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Person_luckyNumbers) | !(i1 = i2) => !(Undefined = Undefined)) ";
		test("test106", ocl, expected);
	}

}
