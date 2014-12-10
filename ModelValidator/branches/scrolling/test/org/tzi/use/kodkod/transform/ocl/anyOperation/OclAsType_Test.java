package org.tzi.use.kodkod.transform.ocl.anyOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class OclAsType_Test extends OCLTest {

	@Test
	public void test1() {
		String ocl = "Set{true, 1}->any(x | x.oclIsTypeOf(Boolean))->oclAsType(Boolean) and true";
		String expected = "((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(Boolean = univ) && x in Boolean}) || #((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(Boolean = univ) && x in Boolean}) > 1) => Undefined else ((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(Boolean = univ) && x in Boolean})) = Boolean_True && Boolean_True = Boolean_True";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Set{true, 1}->any(x | x.oclIsTypeOf(Integer))->oclAsType(Integer) + 2";
		String expected = "(((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(ints = univ) && x in ints}) || #((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(ints = univ) && x in ints}) > 1) => Undefined else ((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(ints = univ) && x in ints})) = Undefined || Int[2] = Undefined) => Undefined else Int[int[(((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set || no ((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(ints = univ) && x in ints}) || #((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(ints = univ) && x in ints}) > 1) => Undefined else ((((Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1])) = Undefined_Set) => Undefined_Set else {x: (Undefined_Set in (none + Boolean_True + Int[1])) => Undefined_Set else (none + Boolean_True + Int[1]) | !(ints = univ) && x in ints})] + int[Int[2]]]";
		test("test2", ocl, expected);
	}

}
