package org.tzi.use.kodkod.transform.ocl.classOperation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class AllInstances_Test extends OCLTest {
	@Test
	public void test1() {
		String ocl = "Person.allInstances()";
		String expected = "Person ";
		test("test1",ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "Animal.allInstances()";
		String expected = "Animal ";
		test("test2",ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "Car.allInstances()";
		String expected = "Car  ";
		test("test3",ocl, expected);
	}

}
