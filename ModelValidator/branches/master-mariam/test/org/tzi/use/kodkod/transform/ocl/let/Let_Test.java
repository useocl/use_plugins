package org.tzi.use.kodkod.transform.ocl.let;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Let_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "let i:Integer = 3 in i";
		String expected = "Int[3]";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "let i:Integer = 3 in i+2";
		String expected = "(Int[3] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[3]] + int[Int[2]]]";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "let i:Integer = 3 in i+i";
		String expected = "(Int[3] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[3]] + int[Int[3]]]";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "let i:Integer = 3 in let j:Integer = 4 in i+j";
		String expected = "(Int[3] = Undefined || Int[4] = Undefined) => Undefined else Int[int[Int[3]] + int[Int[4]]]";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "Set{1,2,3,4}->select(x | let j:Integer = 5 in x<j)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(x = Undefined) && !(Int[5] = Undefined) && int[x] < int[Int[5]]}";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "let i:Integer = 2 in Set{1,2,3,4}->select(x | let j:Integer = i in x<j)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(x = Undefined) && !(Int[2] = Undefined) && int[x] < int[Int[2]]}";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "let i:Integer = 2 in Set{1,2,3,4}->select(x | let j:Integer = x in j<i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(x = Undefined) && !(Int[2] = Undefined) && int[x] < int[Int[2]]}";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "let b:Boolean = true in b";
		String expected = "Boolean_True";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "let b:Boolean = true in b or false";
		String expected = "Boolean_True = Boolean_True || Boolean_False = Boolean_True";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "let b1:Boolean = true in let b2:Boolean = true in b1 and b2";
		String expected = "Boolean_True = Boolean_True && Boolean_True = Boolean_True";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "let s:Set(Integer) = Set{1,2,3} in s";
		String expected = "(Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "let s:Set(Integer) = Set{1,2,3} in s->size()";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else Int[#((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3]))]";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "let s:Set(Integer) = Set{1,2,3} in s->union(s)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set || ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) + ((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])))";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "let s1:Set(Integer) = Set{1,2,3} in let s2:Set(Integer) = Set{4,5,6} in s1->union(s2)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set || ((Undefined_Set in (none + Int[4] + Int[5] + Int[6])) => Undefined_Set else (none + Int[4] + Int[5] + Int[6])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) + ((Undefined_Set in (none + Int[4] + Int[5] + Int[6])) => Undefined_Set else (none + Int[4] + Int[5] + Int[6])))";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "Set{1,2,3,4}->select(x | let s:Set(Integer) = Set{1,4,5} in s->excludes(x))";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(((Undefined_Set in (none + Int[1] + Int[4] + Int[5])) => Undefined_Set else (none + Int[1] + Int[4] + Int[5])) = Undefined_Set) && !(x in ((Undefined_Set in (none + Int[1] + Int[4] + Int[5])) => Undefined_Set else (none + Int[1] + Int[4] + Int[5])))}";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "let s1:Set(Integer) = Set{1,4,5} in Set{1,2,3,4}->select(x | let s2:Set(Integer) = s1 in s2->excludes(x))";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(((Undefined_Set in (none + Int[1] + Int[4] + Int[5])) => Undefined_Set else (none + Int[1] + Int[4] + Int[5])) = Undefined_Set) && !(x in ((Undefined_Set in (none + Int[1] + Int[4] + Int[5])) => Undefined_Set else (none + Int[1] + Int[4] + Int[5])))}";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "let s:Set(Boolean) = Set{true} in s";
		String expected = "(Undefined_Set in (none + Boolean_True)) => Undefined_Set else (none + Boolean_True)";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "let s:Set(Integer) = Set{1,2,3} in let i:Integer = 5 in s->including(i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2] + Int[3])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3])) + Int[5])";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "let i:Integer = 1+2 in i";
		String expected = "(Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "let i:Integer = 1+2 in i+2";
		String expected = "(((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) = Undefined || Int[2] = Undefined) => Undefined else Int[int[(Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]] + int[Int[2]]]";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "let i:Integer = 1+2 in i+i";
		String expected = "(((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) = Undefined || ((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) = Undefined) => Undefined else Int[int[(Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]] + int[(Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]]]";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "let i:Integer = 1+2 in let j:Integer = 2+2 in i+j";
		String expected = "(((Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]) = Undefined || ((Int[2] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[2]] + int[Int[2]]]) = Undefined) => Undefined else Int[int[(Int[1] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[2]]]] + int[(Int[2] = Undefined || Int[2] = Undefined) => Undefined else Int[int[Int[2]] + int[Int[2]]]]]";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "Set{1,2,3,4}->select(x | let j:Integer = 1+4 in x<j)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(x = Undefined) && !(((Int[1] = Undefined || Int[4] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[4]]]) = Undefined) && int[x] < int[(Int[1] = Undefined || Int[4] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[4]]]]}";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "let i:Integer = 1+3 in Set{1,2,3,4}->select(x | let j:Integer = i-1 in x<j)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(x = Undefined) && !(((((Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]) = Undefined || Int[1] = Undefined) => Undefined else Int[int[(Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]] - int[Int[1]]]) = Undefined) && int[x] < int[(((Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]) = Undefined || Int[1] = Undefined) => Undefined else Int[int[(Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]] - int[Int[1]]]]}";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "let i:Integer = 1+3 in Set{1,2,3,4}->select(x | let j:Integer = x in j<i)";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(x = Undefined) && !(((Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]) = Undefined) && int[x] < int[(Int[1] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[1]] + int[Int[3]]]]}";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "let b:Boolean = 2<3 in b";
		String expected = "!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "let b:Boolean = 2<3 in b or false";
		String expected = "(!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]]) || Boolean_False = Boolean_True";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "let b1:Boolean = 2<3 in let b2:Boolean = 2<4 in b1 and b2";
		String expected = "!(Int[2] = Undefined) && !(Int[3] = Undefined) && int[Int[2]] < int[Int[3]] && !(Int[2] = Undefined) && !(Int[4] = Undefined) && int[Int[2]] < int[Int[4]]";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "let s:Set(Integer) = Set{1,2}->including(3) in s";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "let s:Set(Integer) = Set{1,2}->including(3) in s->size()";
		String expected = "(((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) = Undefined_Set) => Undefined_Set else Int[#((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3]))]";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "let s:Set(Integer) = Set{1,2}->including(3) in s->union(s)";
		String expected = "(((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) = Undefined_Set || ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) = Undefined_Set) => Undefined_Set else (((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) + ((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])))";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "let s1:Set(Integer) = Set{1,2}->including(3) in let s2:Set(Integer) = Set{4,5}->union(Set{6}) in s1->union(s2)";
		String expected = "(((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) = Undefined_Set || ((((Undefined_Set in (none + Int[4] + Int[5])) => Undefined_Set else (none + Int[4] + Int[5])) = Undefined_Set || ((Undefined_Set in (none + Int[6])) => Undefined_Set else (none + Int[6])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[4] + Int[5])) => Undefined_Set else (none + Int[4] + Int[5])) + ((Undefined_Set in (none + Int[6])) => Undefined_Set else (none + Int[6])))) = Undefined_Set) => Undefined_Set else (((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) + ((((Undefined_Set in (none + Int[4] + Int[5])) => Undefined_Set else (none + Int[4] + Int[5])) = Undefined_Set || ((Undefined_Set in (none + Int[6])) => Undefined_Set else (none + Int[6])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[4] + Int[5])) => Undefined_Set else (none + Int[4] + Int[5])) + ((Undefined_Set in (none + Int[6])) => Undefined_Set else (none + Int[6])))))";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "Set{1,2,3,4}->select(x | let s:Set(Integer) = Set{1,4}->including(5) in s->excludes(x))";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(((((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) + Int[5])) = Undefined_Set) && !(x in ((((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) + Int[5])))}";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = " let s1:Set(Integer) = Set{1,4}->including(5) in Set{1,2,3,4}->select(x | let s2:Set(Integer) = s1 in s2->excludes(x))";
		String expected = "(((Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Int[1] + Int[2] + Int[3] + Int[4])) => Undefined_Set else (none + Int[1] + Int[2] + Int[3] + Int[4]) | !(((((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) + Int[5])) = Undefined_Set) && !(x in ((((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[4])) => Undefined_Set else (none + Int[1] + Int[4])) + Int[5])))}";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "let s:Set(Integer) = Set{1,2}->including(3) in let i:Integer = 2+3 in s->including(i)";
		String expected = "(((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) = Undefined_Set) => Undefined_Set else (((((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) = Undefined_Set) => Undefined_Set else (((Undefined_Set in (none + Int[1] + Int[2])) => Undefined_Set else (none + Int[1] + Int[2])) + Int[3])) + ((Int[2] = Undefined || Int[3] = Undefined) => Undefined else Int[int[Int[2]] + int[Int[3]]]))";
		test("test35", ocl, expected);
	}

}
