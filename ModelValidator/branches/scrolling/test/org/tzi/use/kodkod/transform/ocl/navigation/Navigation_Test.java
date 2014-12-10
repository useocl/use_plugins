package org.tzi.use.kodkod.transform.ocl.navigation;

import org.junit.Test;
import org.tzi.use.kodkod.transform.ocl.OCLTest;

public class Navigation_Test extends OCLTest {

	@Test
	public void test1() {
		String ocl = "ada.employerA";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Job_A) ";
		test("test1", ocl, expected);
	}

	@Test
	public void test2() {
		String ocl = "ibm.employeeA";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm) ";
		test("test2", ocl, expected);
	}

	@Test
	public void test3() {
		String ocl = "uf.person.employerA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . Job_A) ";
		test("test3", ocl, expected);
	}

	@Test
	public void test4() {
		String ocl = "uf.company.employeeA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else (Job_A . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company))) ";
		test("test4", ocl, expected);
	}

	@Test
	public void test5() {
		String ocl = "ada.employerA";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Job_A) ";
		test("test5", ocl, expected);
	}

	@Test
	public void test6() {
		String ocl = "ibm.employeeA";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm) ";
		test("test6", ocl, expected);
	}

	@Test
	public void test7() {
		String ocl = "ada.employerA";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Job_A) ";
		test("test7", ocl, expected);
	}

	@Test
	public void test8() {
		String ocl = "bob.employerA";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . Job_A) ";
		test("test8", ocl, expected);
	}

	@Test
	public void test9() {
		String ocl = "ibm.employeeA";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm) ";
		test("test9", ocl, expected);
	}

	@Test
	public void test10() {
		String ocl = "apple.employeeA";
		String expected = "(Company_apple = Undefined) => Undefined_Set else (Job_A . Company_apple) ";
		test("test10", ocl, expected);
	}

	@Test
	public void test11() {
		String ocl = "ada.employerA";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . Job_A) ";
		test("test11", ocl, expected);
	}

	@Test
	public void test12() {
		String ocl = "bob.employerA";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . Job_A) ";
		test("test12", ocl, expected);
	}

	@Test
	public void test13() {
		String ocl = "ibm.employeeA";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (Job_A . Company_ibm) ";
		test("test13", ocl, expected);
	}

	@Test
	public void test14() {
		String ocl = "apple.employeeA";
		String expected = "(Company_apple = Undefined) => Undefined_Set else (Job_A . Company_apple) ";
		test("test14", ocl, expected);
	}

	@Test
	public void test15() {
		String ocl = "ada.employerAC";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . (univ . Job_AC_assoc)) ";
		test("test15", ocl, expected);
	}

	@Test
	public void test16() {
		String ocl = "ada.job_AC";
		String expected = "(Person_ada = Undefined) => Undefined else ((Job_AC_assoc . univ) . Person_ada) ";
		test("test16", ocl, expected);
	}

	@Test
	public void test17() {
		String ocl = "ibm.employeeAC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (univ . (Job_AC_assoc . Company_ibm)) ";
		test("test17", ocl, expected);
	}

	@Test
	public void test18() {
		String ocl = "ibm.job_AC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else ((Job_AC_assoc . Company_ibm) . univ) ";
		test("test18", ocl, expected);
	}

	@Test
	public void test19() {
		String ocl = "uf.person.employerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . Job_AC_assoc)) ";
		test("test19", ocl, expected);
	}

	@Test
	public void test20() {
		String ocl = "uf.person.job_AC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined else ((Job_AC_assoc . univ) . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))) ";
		test("test20", ocl, expected);
	}

	@Test
	public void test21() {
		String ocl = "uf.company.employeeAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else (univ . (Job_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)))) ";
		test("test21", ocl, expected);
	}

	@Test
	public void test22() {
		String ocl = "uf.company.job_AC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else ((Job_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company))) . univ) ";
		test("test22", ocl, expected);
	}

	@Test
	public void test23() {
		String ocl = "uf.job.employeeAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_job)) = Undefined) => Undefined else ((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_job)) . Job_AC_assoc) . univ) ";
		test("test23", ocl, expected);
	}

	@Test
	public void test24() {
		String ocl = "uf.job.employerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_job)) = Undefined) => Undefined else (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_job)) . Job_AC_assoc)) ";
		test("test24", ocl, expected);
	}

	@Test
	public void test25() {
		String ocl = "ada.employerAC";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . (univ . Job_AC_assoc)) ";
		test("test25", ocl, expected);
	}

	@Test
	public void test26() {
		String ocl = "ada.job_AC";
		String expected = "(Person_ada = Undefined) => Undefined else ((Job_AC_assoc . univ) . Person_ada) ";
		test("test26", ocl, expected);
	}

	@Test
	public void test27() {
		String ocl = "ibm.employeeAC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (univ . (Job_AC_assoc . Company_ibm)) ";
		test("test27", ocl, expected);
	}

	@Test
	public void test28() {
		String ocl = "ibm.job_AC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else ((Job_AC_assoc . Company_ibm) . univ) ";
		test("test28", ocl, expected);
	}

	@Test
	public void test29() {
		String ocl = "jobAdaIbm.employeeAC";
		String expected = "(Job_AC_jobAdaIbm = Undefined) => Undefined else ((Job_AC_jobAdaIbm . Job_AC_assoc) . univ) ";
		test("test29", ocl, expected);
	}

	@Test
	public void test30() {
		String ocl = "jobAdaIbm.employerAC";
		String expected = "(Job_AC_jobAdaIbm = Undefined) => Undefined else (univ . (Job_AC_jobAdaIbm . Job_AC_assoc)) ";
		test("test30", ocl, expected);
	}

	@Test
	public void test31() {
		String ocl = "ada.employerAC";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . (univ . Job_AC_assoc)) ";
		test("test31", ocl, expected);
	}

	@Test
	public void test32() {
		String ocl = "ada.job_AC";
		String expected = "(Person_ada = Undefined) => Undefined else ((Job_AC_assoc . univ) . Person_ada) ";
		test("test32", ocl, expected);
	}

	@Test
	public void test33() {
		String ocl = "bob.employerAC";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . (univ . Job_AC_assoc)) ";
		test("test33", ocl, expected);
	}

	@Test
	public void test34() {
		String ocl = "bob.job_AC";
		String expected = "(Person_bob = Undefined) => Undefined else ((Job_AC_assoc . univ) . Person_bob) ";
		test("test34", ocl, expected);
	}

	@Test
	public void test35() {
		String ocl = "ibm.employeeAC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (univ . (Job_AC_assoc . Company_ibm)) ";
		test("test35", ocl, expected);
	}

	@Test
	public void test36() {
		String ocl = "ibm.job_AC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else ((Job_AC_assoc . Company_ibm) . univ) ";
		test("test36", ocl, expected);
	}

	@Test
	public void test37() {
		String ocl = "apple.employeeAC";
		String expected = "(Company_apple = Undefined) => Undefined_Set else (univ . (Job_AC_assoc . Company_apple)) ";
		test("test37", ocl, expected);
	}

	@Test
	public void test38() {
		String ocl = "apple.job_AC";
		String expected = "(Company_apple = Undefined) => Undefined_Set else ((Job_AC_assoc . Company_apple) . univ) ";
		test("test38", ocl, expected);
	}

	@Test
	public void test39() {
		String ocl = "jobAdaIbm.employeeAC";
		String expected = "(Job_AC_jobAdaIbm = Undefined) => Undefined else ((Job_AC_jobAdaIbm . Job_AC_assoc) . univ) ";
		test("test39", ocl, expected);
	}

	@Test
	public void test40() {
		String ocl = "jobAdaIbm.employerAC";
		String expected = "(Job_AC_jobAdaIbm = Undefined) => Undefined else (univ . (Job_AC_jobAdaIbm . Job_AC_assoc)) ";
		test("test40", ocl, expected);
	}

	@Test
	public void test41() {
		String ocl = "jobBobApple.employeeAC";
		String expected = "(Job_AC_jobBobApple = Undefined) => Undefined else ((Job_AC_jobBobApple . Job_AC_assoc) . univ) ";
		test("test41", ocl, expected);
	}

	@Test
	public void test42() {
		String ocl = "jobBobApple.employerAC";
		String expected = "(Job_AC_jobBobApple = Undefined) => Undefined else (univ . (Job_AC_jobBobApple . Job_AC_assoc)) ";
		test("test42", ocl, expected);
	}

	@Test
	public void test43() {
		String ocl = "ada.employerAC";
		String expected = "(Person_ada = Undefined) => Undefined else (Person_ada . (univ . Job_AC_assoc)) ";
		test("test43", ocl, expected);
	}

	@Test
	public void test44() {
		String ocl = "ada.job_AC";
		String expected = "(Person_ada = Undefined) => Undefined else ((Job_AC_assoc . univ) . Person_ada) ";
		test("test44", ocl, expected);
	}

	@Test
	public void test45() {
		String ocl = "bob.employerAC";
		String expected = "(Person_bob = Undefined) => Undefined else (Person_bob . (univ . Job_AC_assoc)) ";
		test("test45", ocl, expected);
	}

	@Test
	public void test46() {
		String ocl = "bob.job_AC";
		String expected = "(Person_bob = Undefined) => Undefined else ((Job_AC_assoc . univ) . Person_bob) ";
		test("test46", ocl, expected);
	}

	@Test
	public void test47() {
		String ocl = "ibm.employeeAC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else (univ . (Job_AC_assoc . Company_ibm)) ";
		test("test47", ocl, expected);
	}

	@Test
	public void test48() {
		String ocl = "ibm.job_AC";
		String expected = "(Company_ibm = Undefined) => Undefined_Set else ((Job_AC_assoc . Company_ibm) . univ) ";
		test("test48", ocl, expected);
	}

	@Test
	public void test49() {
		String ocl = "apple.employeeAC";
		String expected = "(Company_apple = Undefined) => Undefined_Set else (univ . (Job_AC_assoc . Company_apple)) ";
		test("test49", ocl, expected);
	}

	@Test
	public void test50() {
		String ocl = "apple.job_AC";
		String expected = "(Company_apple = Undefined) => Undefined_Set else ((Job_AC_assoc . Company_apple) . univ) ";
		test("test50", ocl, expected);
	}

	@Test
	public void test51() {
		String ocl = "jobAdaIbm.employeeAC";
		String expected = "(Job_AC_jobAdaIbm = Undefined) => Undefined else ((Job_AC_jobAdaIbm . Job_AC_assoc) . univ) ";
		test("test51", ocl, expected);
	}

	@Test
	public void test52() {
		String ocl = "jobAdaIbm.employerAC";
		String expected = "(Job_AC_jobAdaIbm = Undefined) => Undefined else (univ . (Job_AC_jobAdaIbm . Job_AC_assoc)) ";
		test("test52", ocl, expected);
	}

	@Test
	public void test53() {
		String ocl = "jobBobIbm.employeeAC";
		String expected = "(Job_AC_jobBobIbm = Undefined) => Undefined else ((Job_AC_jobBobIbm . Job_AC_assoc) . univ) ";
		test("test53", ocl, expected);
	}

	@Test
	public void test54() {
		String ocl = "jobBobIbm.employerAC";
		String expected = "(Job_AC_jobBobIbm = Undefined) => Undefined else (univ . (Job_AC_jobBobIbm . Job_AC_assoc)) ";
		test("test54", ocl, expected);
	}

	@Test
	public void test55() {
		String ocl = "ada.childA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . Parent_A) ";
		test("test55", ocl, expected);
	}

	@Test
	public void test56() {
		String ocl = "ada.parentA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Parent_A . Person_ada) ";
		test("test56", ocl, expected);
	}

	@Test
	public void test57() {
		String ocl = "uf.person.childA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . Parent_A) ";
		test("test57", ocl, expected);
	}

	@Test
	public void test58() {
		String ocl = "uf.person.parentA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (Parent_A . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))) ";
		test("test58", ocl, expected);
	}

	@Test
	public void test59() {
		String ocl = "ada.childA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . Parent_A) ";
		test("test59", ocl, expected);
	}

	@Test
	public void test60() {
		String ocl = "ada.parentA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Parent_A . Person_ada) ";
		test("test60", ocl, expected);
	}

	@Test
	public void test61() {
		String ocl = "bob.childA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . Parent_A) ";
		test("test61", ocl, expected);
	}

	@Test
	public void test62() {
		String ocl = "bob.parentA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Parent_A . Person_bob) ";
		test("test62", ocl, expected);
	}

	@Test
	public void test63() {
		String ocl = "ada.childA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . Parent_A) ";
		test("test63", ocl, expected);
	}

	@Test
	public void test64() {
		String ocl = "ada.parentA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Parent_A . Person_ada) ";
		test("test64", ocl, expected);
	}

	@Test
	public void test65() {
		String ocl = "bob.childA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . Parent_A) ";
		test("test65", ocl, expected);
	}

	@Test
	public void test66() {
		String ocl = "bob.parentA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Parent_A . Person_bob) ";
		test("test66", ocl, expected);
	}

	@Test
	public void test67() {
		String ocl = "cyd.childA";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . Parent_A) ";
		test("test67", ocl, expected);
	}

	@Test
	public void test68() {
		String ocl = "cyd.parentA";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Parent_A . Person_cyd) ";
		test("test68", ocl, expected);
	}

	@Test
	public void test69() {
		String ocl = "ada.childAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . Parent_AC_assoc)) ";
		test("test69", ocl, expected);
	}

	@Test
	public void test70() {
		String ocl = "ada.parentAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Parent_AC_assoc . Person_ada)) ";
		test("test70", ocl, expected);
	}

	@Test
	public void test71() {
		String ocl = "ada.parent_AC[parentAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Parent_AC_assoc . univ) . Person_ada) ";
		test("test71", ocl, expected);
	}

	@Test
	public void test72() {
		String ocl = "ada.parent_AC[childAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Parent_AC_assoc . Person_ada) . univ) ";
		test("test72", ocl, expected);
	}

	@Test
	public void test73() {
		String ocl = "uf.person.childAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . Parent_AC_assoc)) ";
		test("test73", ocl, expected);
	}

	@Test
	public void test74() {
		String ocl = "uf.person.parentAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . (Parent_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) ";
		test("test74", ocl, expected);
	}

	@Test
	public void test75() {
		String ocl = "uf.parent.parentAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_parent)) = Undefined) => Undefined else ((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_parent)) . Parent_AC_assoc) . univ) ";
		test("test75", ocl, expected);
	}

	@Test
	public void test76() {
		String ocl = "uf.parent.childAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_parent)) = Undefined) => Undefined else (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_parent)) . Parent_AC_assoc)) ";
		test("test76", ocl, expected);
	}

	@Test
	public void test77() {
		String ocl = "ada.childAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . Parent_AC_assoc)) ";
		test("test77", ocl, expected);
	}

	@Test
	public void test78() {
		String ocl = "ada.parentAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Parent_AC_assoc . Person_ada)) ";
		test("test78", ocl, expected);
	}

	@Test
	public void test79() {
		String ocl = "ada.parent_AC[parentAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Parent_AC_assoc . univ) . Person_ada) ";
		test("test79", ocl, expected);
	}

	@Test
	public void test80() {
		String ocl = "ada.parent_AC[childAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Parent_AC_assoc . Person_ada) . univ) ";
		test("test80", ocl, expected);
	}

	@Test
	public void test81() {
		String ocl = "bob.childAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . Parent_AC_assoc)) ";
		test("test81", ocl, expected);
	}

	@Test
	public void test82() {
		String ocl = "bob.parentAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Parent_AC_assoc . Person_bob)) ";
		test("test82", ocl, expected);
	}

	@Test
	public void test83() {
		String ocl = "bob.parent_AC[parentAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Parent_AC_assoc . univ) . Person_bob) ";
		test("test83", ocl, expected);
	}

	@Test
	public void test84() {
		String ocl = "bob.parent_AC[childAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Parent_AC_assoc . Person_bob) . univ) ";
		test("test84", ocl, expected);
	}

	@Test
	public void test85() {
		String ocl = "parentAdaBob.parentAC";
		String expected = "(Parent_AC_parentAdaBob = Undefined) => Undefined else ((Parent_AC_parentAdaBob . Parent_AC_assoc) . univ) ";
		test("test85", ocl, expected);
	}

	@Test
	public void test86() {
		String ocl = "parentAdaBob.childAC";
		String expected = "(Parent_AC_parentAdaBob = Undefined) => Undefined else (univ . (Parent_AC_parentAdaBob . Parent_AC_assoc)) ";
		test("test86", ocl, expected);
	}

	@Test
	public void test87() {
		String ocl = "ada.childAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . Parent_AC_assoc)) ";
		test("test87", ocl, expected);
	}

	@Test
	public void test88() {
		String ocl = "ada.parentAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Parent_AC_assoc . Person_ada)) ";
		test("test88", ocl, expected);
	}

	@Test
	public void test89() {
		String ocl = "ada.parent_AC[parentAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Parent_AC_assoc . univ) . Person_ada) ";
		test("test89", ocl, expected);
	}

	@Test
	public void test90() {
		String ocl = "ada.parent_AC[childAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Parent_AC_assoc . Person_ada) . univ) ";
		test("test90", ocl, expected);
	}

	@Test
	public void test91() {
		String ocl = "bob.childAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . Parent_AC_assoc)) ";
		test("test91", ocl, expected);
	}

	@Test
	public void test92() {
		String ocl = "bob.parentAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Parent_AC_assoc . Person_bob)) ";
		test("test92", ocl, expected);
	}

	@Test
	public void test93() {
		String ocl = "bob.parent_AC[parentAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Parent_AC_assoc . univ) . Person_bob) ";
		test("test93", ocl, expected);
	}

	@Test
	public void test94() {
		String ocl = "bob.parent_AC[childAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Parent_AC_assoc . Person_bob) . univ) ";
		test("test94", ocl, expected);
	}

	@Test
	public void test95() {
		String ocl = "cyd.childAC";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . (univ . Parent_AC_assoc)) ";
		test("test95", ocl, expected);
	}

	@Test
	public void test96() {
		String ocl = "cyd.parentAC";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Parent_AC_assoc . Person_cyd)) ";
		test("test96", ocl, expected);
	}

	@Test
	public void test97() {
		String ocl = "cyd.parent_AC[parentAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Parent_AC_assoc . univ) . Person_cyd) ";
		test("test97", ocl, expected);
	}

	@Test
	public void test98() {
		String ocl = "cyd.parent_AC[childAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Parent_AC_assoc . Person_cyd) . univ) ";
		test("test98", ocl, expected);
	}

	@Test
	public void test99() {
		String ocl = "parentAdaBob.parentAC";
		String expected = "(Parent_AC_parentAdaBob = Undefined) => Undefined else ((Parent_AC_parentAdaBob . Parent_AC_assoc) . univ) ";
		test("test99", ocl, expected);
	}

	@Test
	public void test100() {
		String ocl = "parentAdaBob.childAC";
		String expected = "(Parent_AC_parentAdaBob = Undefined) => Undefined else (univ . (Parent_AC_parentAdaBob . Parent_AC_assoc)) ";
		test("test100", ocl, expected);
	}

	@Test
	public void test101() {
		String ocl = "parentAdaCyd.parentAC";
		String expected = "(Parent_AC_parentAdaCyd = Undefined) => Undefined else ((Parent_AC_parentAdaCyd . Parent_AC_assoc) . univ) ";
		test("test101", ocl, expected);
	}

	@Test
	public void test102() {
		String ocl = "parentAdaCyd.childAC";
		String expected = "(Parent_AC_parentAdaCyd = Undefined) => Undefined else (univ . (Parent_AC_parentAdaCyd . Parent_AC_assoc)) ";
		test("test102", ocl, expected);
	}

	@Test
	public void test103() {
		String ocl = "ada.sellerA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . Buy_A) . univ) ";
		test("test103", ocl, expected);
	}

	@Test
	public void test104() {
		String ocl = "ada.petA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . Buy_A)) ";
		test("test104", ocl, expected);
	}

	@Test
	public void test105() {
		String ocl = "petShop.buyerA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else ((Buy_A . univ) . Company_petShop) ";
		test("test105", ocl, expected);
	}

	@Test
	public void test106() {
		String ocl = "petShop.petA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . Buy_A)) ";
		test("test106", ocl, expected);
	}

	@Test
	public void test107() {
		String ocl = "wolfi.buyerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else ((Buy_A . Animal_wolfi) . univ) ";
		test("test107", ocl, expected);
	}

	@Test
	public void test108() {
		String ocl = "wolfi.sellerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (Buy_A . Animal_wolfi)) ";
		test("test108", ocl, expected);
	}

	@Test
	public void test109() {
		String ocl = "uf.person.sellerA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else ((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . Buy_A) . univ) ";
		test("test109", ocl, expected);
	}

	@Test
	public void test110() {
		String ocl = "uf.person.petA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . Buy_A)) ";
		test("test110", ocl, expected);
	}

	@Test
	public void test111() {
		String ocl = "uf.company.buyerA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else ((Buy_A . univ) . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company))) ";
		test("test111", ocl, expected);
	}

	@Test
	public void test112() {
		String ocl = "uf.company.petA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) . (univ . Buy_A)) ";
		test("test112", ocl, expected);
	}

	@Test
	public void test113() {
		String ocl = "uf.animal.buyerA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal)) = Undefined) => Undefined_Set else ((Buy_A . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal))) . univ) ";
		test("test113", ocl, expected);
	}

	@Test
	public void test114() {
		String ocl = "uf.animal.sellerA";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal)) = Undefined) => Undefined_Set else (univ . (Buy_A . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal)))) ";
		test("test114", ocl, expected);
	}

	@Test
	public void test115() {
		String ocl = "ada.sellerA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . Buy_A) . univ) ";
		test("test115", ocl, expected);
	}

	@Test
	public void test116() {
		String ocl = "ada.petA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . Buy_A)) ";
		test("test116", ocl, expected);
	}

	@Test
	public void test117() {
		String ocl = "petShop.buyerA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else ((Buy_A . univ) . Company_petShop) ";
		test("test117", ocl, expected);
	}

	@Test
	public void test118() {
		String ocl = "petShop.petA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . Buy_A)) ";
		test("test118", ocl, expected);
	}

	@Test
	public void test119() {
		String ocl = "wolfi.buyerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else ((Buy_A . Animal_wolfi) . univ) ";
		test("test119", ocl, expected);
	}

	@Test
	public void test120() {
		String ocl = "wolfi.sellerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (Buy_A . Animal_wolfi)) ";
		test("test120", ocl, expected);
	}

	@Test
	public void test121() {
		String ocl = "ada.sellerA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . Buy_A) . univ) ";
		test("test121", ocl, expected);
	}

	@Test
	public void test122() {
		String ocl = "ada.petA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . Buy_A)) ";
		test("test122", ocl, expected);
	}

	@Test
	public void test123() {
		String ocl = "bob.sellerA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . Buy_A) . univ) ";
		test("test123", ocl, expected);
	}

	@Test
	public void test124() {
		String ocl = "bob.petA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . Buy_A)) ";
		test("test124", ocl, expected);
	}

	@Test
	public void test125() {
		String ocl = "petShop.buyerA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else ((Buy_A . univ) . Company_petShop) ";
		test("test125", ocl, expected);
	}

	@Test
	public void test126() {
		String ocl = "petShop.petA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . Buy_A)) ";
		test("test126", ocl, expected);
	}

	@Test
	public void test127() {
		String ocl = "wolfi.buyerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else ((Buy_A . Animal_wolfi) . univ) ";
		test("test127", ocl, expected);
	}

	@Test
	public void test128() {
		String ocl = "wolfi.sellerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (Buy_A . Animal_wolfi)) ";
		test("test128", ocl, expected);
	}

	@Test
	public void test129() {
		String ocl = "stan.buyerA";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else ((Buy_A . Animal_stan) . univ) ";
		test("test129", ocl, expected);
	}

	@Test
	public void test130() {
		String ocl = "stan.sellerA";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (univ . (Buy_A . Animal_stan)) ";
		test("test130", ocl, expected);
	}

	@Test
	public void test131() {
		String ocl = "ada.sellerA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . Buy_A) . univ) ";
		test("test131", ocl, expected);
	}

	@Test
	public void test132() {
		String ocl = "ada.petA";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . Buy_A)) ";
		test("test132", ocl, expected);
	}

	@Test
	public void test133() {
		String ocl = "bob.sellerA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . Buy_A) . univ) ";
		test("test133", ocl, expected);
	}

	@Test
	public void test134() {
		String ocl = "bob.petA";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . Buy_A)) ";
		test("test134", ocl, expected);
	}

	@Test
	public void test135() {
		String ocl = "petShop.buyerA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else ((Buy_A . univ) . Company_petShop) ";
		test("test135", ocl, expected);
	}

	@Test
	public void test136() {
		String ocl = "petShop.petA";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . Buy_A)) ";
		test("test136", ocl, expected);
	}

	@Test
	public void test137() {
		String ocl = "wolfi.buyerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else ((Buy_A . Animal_wolfi) . univ) ";
		test("test137", ocl, expected);
	}

	@Test
	public void test138() {
		String ocl = "wolfi.sellerA";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (Buy_A . Animal_wolfi)) ";
		test("test138", ocl, expected);
	}

	@Test
	public void test139() {
		String ocl = "stan.buyerA";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else ((Buy_A . Animal_stan) . univ) ";
		test("test139", ocl, expected);
	}

	@Test
	public void test140() {
		String ocl = "stan.sellerA";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (univ . (Buy_A . Animal_stan)) ";
		test("test140", ocl, expected);
	}

	@Test
	public void test141() {
		String ocl = "ada.sellerAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . (univ . Buy_AC_assoc)) . univ) ";
		test("test141", ocl, expected);
	}

	@Test
	public void test142() {
		String ocl = "ada.petAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . (univ . Buy_AC_assoc))) ";
		test("test142", ocl, expected);
	}

	@Test
	public void test143() {
		String ocl = "ada.buy_AC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . univ) . Person_ada) ";
		test("test143", ocl, expected);
	}

	@Test
	public void test144() {
		String ocl = "petShop.buyerAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . univ) . Company_petShop)) ";
		test("test144", ocl, expected);
	}

	@Test
	public void test145() {
		String ocl = "petShop.petAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . (univ . Buy_AC_assoc))) ";
		test("test145", ocl, expected);
	}

	@Test
	public void test146() {
		String ocl = "petShop.buy_AC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . Company_petShop) . univ) ";
		test("test146", ocl, expected);
	}

	@Test
	public void test147() {
		String ocl = "wolfi.buyerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . Animal_wolfi) . univ)) ";
		test("test147", ocl, expected);
	}

	@Test
	public void test148() {
		String ocl = "wolfi.sellerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (univ . (Buy_AC_assoc . Animal_wolfi))) ";
		test("test148", ocl, expected);
	}

	@Test
	public void test149() {
		String ocl = "wolfi.buy_AC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (((Buy_AC_assoc . Animal_wolfi) . univ) . univ) ";
		test("test149", ocl, expected);
	}

	@Test
	public void test150() {
		String ocl = "uf.person.sellerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else ((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . Buy_AC_assoc)) . univ) ";
		test("test150", ocl, expected);
	}

	@Test
	public void test151() {
		String ocl = "uf.person.petAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . Buy_AC_assoc))) ";
		test("test151", ocl, expected);
	}

	@Test
	public void test152() {
		String ocl = "uf.person.buy_AC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . univ) . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))) ";
		test("test152", ocl, expected);
	}

	@Test
	public void test153() {
		String ocl = "uf.company.buyerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . univ) . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)))) ";
		test("test153", ocl, expected);
	}

	@Test
	public void test154() {
		String ocl = "uf.company.petAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) . (univ . (univ . Buy_AC_assoc))) ";
		test("test154", ocl, expected);
	}

	@Test
	public void test155() {
		String ocl = "uf.company.buy_AC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company)) = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_company))) . univ) ";
		test("test155", ocl, expected);
	}

	@Test
	public void test156() {
		String ocl = "uf.animal.buyerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal)) = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal))) . univ)) ";
		test("test156", ocl, expected);
	}

	@Test
	public void test157() {
		String ocl = "uf.animal.sellerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal)) = Undefined) => Undefined_Set else (univ . (univ . (Buy_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal))))) ";
		test("test157", ocl, expected);
	}

	@Test
	public void test158() {
		String ocl = "uf.animal.buy_AC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal)) = Undefined) => Undefined_Set else (((Buy_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_animal))) . univ) . univ) ";
		test("test158", ocl, expected);
	}

	@Test
	public void test159() {
		String ocl = "uf.buy.buyerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_buy)) = Undefined) => Undefined else (((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_buy)) . Buy_AC_assoc) . univ) . univ) ";
		test("test159", ocl, expected);
	}

	@Test
	public void test160() {
		String ocl = "uf.buy.sellerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_buy)) = Undefined) => Undefined else ((univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_buy)) . Buy_AC_assoc)) . univ) ";
		test("test160", ocl, expected);
	}

	@Test
	public void test161() {
		String ocl = "uf.buy.petAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_buy)) = Undefined) => Undefined else (univ . (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_buy)) . Buy_AC_assoc))) ";
		test("test161", ocl, expected);
	}

	@Test
	public void test162() {
		String ocl = "ada.sellerAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . (univ . Buy_AC_assoc)) . univ) ";
		test("test162", ocl, expected);
	}

	@Test
	public void test163() {
		String ocl = "ada.petAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . (univ . Buy_AC_assoc))) ";
		test("test163", ocl, expected);
	}

	@Test
	public void test164() {
		String ocl = "ada.buy_AC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . univ) . Person_ada) ";
		test("test164", ocl, expected);
	}

	@Test
	public void test165() {
		String ocl = "petShop.buyerAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . univ) . Company_petShop)) ";
		test("test165", ocl, expected);
	}

	@Test
	public void test166() {
		String ocl = "petShop.petAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . (univ . Buy_AC_assoc))) ";
		test("test166", ocl, expected);
	}

	@Test
	public void test167() {
		String ocl = "petShop.buy_AC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . Company_petShop) . univ) ";
		test("test167", ocl, expected);
	}

	@Test
	public void test168() {
		String ocl = "wolfi.buyerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . Animal_wolfi) . univ)) ";
		test("test168", ocl, expected);
	}

	@Test
	public void test169() {
		String ocl = "wolfi.sellerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (univ . (Buy_AC_assoc . Animal_wolfi))) ";
		test("test169", ocl, expected);
	}

	@Test
	public void test170() {
		String ocl = "wolfi.buy_AC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (((Buy_AC_assoc . Animal_wolfi) . univ) . univ) ";
		test("test170", ocl, expected);
	}

	@Test
	public void test171() {
		String ocl = "buyAdaPetShopWolfi.buyerAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else (((Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc) . univ) . univ) ";
		test("test171", ocl, expected);
	}

	@Test
	public void test172() {
		String ocl = "buyAdaPetShopWolfi.sellerAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else ((univ . (Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc)) . univ) ";
		test("test172", ocl, expected);
	}

	@Test
	public void test173() {
		String ocl = "buyAdaPetShopWolfi.petAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else (univ . (univ . (Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc))) ";
		test("test173", ocl, expected);
	}

	@Test
	public void test174() {
		String ocl = "ada.sellerAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . (univ . Buy_AC_assoc)) . univ) ";
		test("test174", ocl, expected);
	}

	@Test
	public void test175() {
		String ocl = "ada.petAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . (univ . Buy_AC_assoc))) ";
		test("test175", ocl, expected);
	}

	@Test
	public void test176() {
		String ocl = "ada.buy_AC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . univ) . Person_ada) ";
		test("test176", ocl, expected);
	}

	@Test
	public void test177() {
		String ocl = "bob.sellerAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . (univ . Buy_AC_assoc)) . univ) ";
		test("test177", ocl, expected);
	}

	@Test
	public void test178() {
		String ocl = "bob.petAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . (univ . Buy_AC_assoc))) ";
		test("test178", ocl, expected);
	}

	@Test
	public void test179() {
		String ocl = "bob.buy_AC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . univ) . Person_bob) ";
		test("test179", ocl, expected);
	}

	@Test
	public void test180() {
		String ocl = "petShop.buyerAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . univ) . Company_petShop)) ";
		test("test180", ocl, expected);
	}

	@Test
	public void test181() {
		String ocl = "petShop.petAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . (univ . Buy_AC_assoc))) ";
		test("test181", ocl, expected);
	}

	@Test
	public void test182() {
		String ocl = "petShop.buy_AC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . Company_petShop) . univ) ";
		test("test182", ocl, expected);
	}

	@Test
	public void test183() {
		String ocl = "wolfi.buyerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . Animal_wolfi) . univ)) ";
		test("test183", ocl, expected);
	}

	@Test
	public void test184() {
		String ocl = "wolfi.sellerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (univ . (Buy_AC_assoc . Animal_wolfi))) ";
		test("test184", ocl, expected);
	}

	@Test
	public void test185() {
		String ocl = "wolfi.buy_AC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (((Buy_AC_assoc . Animal_wolfi) . univ) . univ) ";
		test("test185", ocl, expected);
	}

	@Test
	public void test186() {
		String ocl = "stan.buyerAC";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . Animal_stan) . univ)) ";
		test("test186", ocl, expected);
	}

	@Test
	public void test187() {
		String ocl = "stan.sellerAC";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (univ . (univ . (Buy_AC_assoc . Animal_stan))) ";
		test("test187", ocl, expected);
	}

	@Test
	public void test188() {
		String ocl = "stan.buy_AC";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (((Buy_AC_assoc . Animal_stan) . univ) . univ) ";
		test("test188", ocl, expected);
	}

	@Test
	public void test189() {
		String ocl = "buyAdaPetShopWolfi.buyerAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else (((Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc) . univ) . univ) ";
		test("test189", ocl, expected);
	}

	@Test
	public void test190() {
		String ocl = "buyAdaPetShopWolfi.sellerAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else ((univ . (Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc)) . univ) ";
		test("test190", ocl, expected);
	}

	@Test
	public void test191() {
		String ocl = "buyAdaPetShopWolfi.petAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else (univ . (univ . (Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc))) ";
		test("test191", ocl, expected);
	}

	@Test
	public void test192() {
		String ocl = "buyBobPetShopStan.buyerAC";
		String expected = "(Buy_AC_buyBobPetShopStan = Undefined) => Undefined else (((Buy_AC_buyBobPetShopStan . Buy_AC_assoc) . univ) . univ) ";
		test("test192", ocl, expected);
	}

	@Test
	public void test193() {
		String ocl = "buyBobPetShopStan.sellerAC";
		String expected = "(Buy_AC_buyBobPetShopStan = Undefined) => Undefined else ((univ . (Buy_AC_buyBobPetShopStan . Buy_AC_assoc)) . univ) ";
		test("test193", ocl, expected);
	}

	@Test
	public void test194() {
		String ocl = "buyBobPetShopStan.petAC";
		String expected = "(Buy_AC_buyBobPetShopStan = Undefined) => Undefined else (univ . (univ . (Buy_AC_buyBobPetShopStan . Buy_AC_assoc))) ";
		test("test194", ocl, expected);
	}

	@Test
	public void test195() {
		String ocl = "ada.sellerAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . (univ . Buy_AC_assoc)) . univ) ";
		test("test195", ocl, expected);
	}

	@Test
	public void test196() {
		String ocl = "ada.petAC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . (univ . Buy_AC_assoc))) ";
		test("test196", ocl, expected);
	}

	@Test
	public void test197() {
		String ocl = "ada.buy_AC";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . univ) . Person_ada) ";
		test("test197", ocl, expected);
	}

	@Test
	public void test198() {
		String ocl = "bob.sellerAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . (univ . Buy_AC_assoc)) . univ) ";
		test("test198", ocl, expected);
	}

	@Test
	public void test199() {
		String ocl = "bob.petAC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . (univ . Buy_AC_assoc))) ";
		test("test199", ocl, expected);
	}

	@Test
	public void test200() {
		String ocl = "bob.buy_AC";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . univ) . Person_bob) ";
		test("test200", ocl, expected);
	}

	@Test
	public void test201() {
		String ocl = "petShop.buyerAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . univ) . Company_petShop)) ";
		test("test201", ocl, expected);
	}

	@Test
	public void test202() {
		String ocl = "petShop.petAC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (Company_petShop . (univ . (univ . Buy_AC_assoc))) ";
		test("test202", ocl, expected);
	}

	@Test
	public void test203() {
		String ocl = "petShop.buy_AC";
		String expected = "(Company_petShop = Undefined) => Undefined_Set else (((Buy_AC_assoc . univ) . Company_petShop) . univ) ";
		test("test203", ocl, expected);
	}

	@Test
	public void test204() {
		String ocl = "wolfi.buyerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . Animal_wolfi) . univ)) ";
		test("test204", ocl, expected);
	}

	@Test
	public void test205() {
		String ocl = "wolfi.sellerAC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (univ . (univ . (Buy_AC_assoc . Animal_wolfi))) ";
		test("test205", ocl, expected);
	}

	@Test
	public void test206() {
		String ocl = "wolfi.buy_AC";
		String expected = "(Animal_wolfi = Undefined) => Undefined_Set else (((Buy_AC_assoc . Animal_wolfi) . univ) . univ) ";
		test("test206", ocl, expected);
	}

	@Test
	public void test207() {
		String ocl = "stan.buyerAC";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (univ . ((Buy_AC_assoc . Animal_stan) . univ)) ";
		test("test207", ocl, expected);
	}

	@Test
	public void test208() {
		String ocl = "stan.sellerAC";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (univ . (univ . (Buy_AC_assoc . Animal_stan))) ";
		test("test208", ocl, expected);
	}

	@Test
	public void test209() {
		String ocl = "stan.buy_AC";
		String expected = "(Animal_stan = Undefined) => Undefined_Set else (((Buy_AC_assoc . Animal_stan) . univ) . univ) ";
		test("test209", ocl, expected);
	}

	@Test
	public void test210() {
		String ocl = "buyAdaPetShopWolfi.buyerAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else (((Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc) . univ) . univ) ";
		test("test210", ocl, expected);
	}

	@Test
	public void test211() {
		String ocl = "buyAdaPetShopWolfi.sellerAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else ((univ . (Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc)) . univ) ";
		test("test211", ocl, expected);
	}

	@Test
	public void test212() {
		String ocl = "buyAdaPetShopWolfi.petAC";
		String expected = "(Buy_AC_buyAdaPetShopWolfi = Undefined) => Undefined else (univ . (univ . (Buy_AC_buyAdaPetShopWolfi . Buy_AC_assoc))) ";
		test("test212", ocl, expected);
	}

	@Test
	public void test213() {
		String ocl = "buyBobPetShopStan.buyerAC";
		String expected = "(Buy_AC_buyBobPetShopStan = Undefined) => Undefined else (((Buy_AC_buyBobPetShopStan . Buy_AC_assoc) . univ) . univ) ";
		test("test213", ocl, expected);
	}

	@Test
	public void test214() {
		String ocl = "buyBobPetShopStan.sellerAC";
		String expected = "(Buy_AC_buyBobPetShopStan = Undefined) => Undefined else ((univ . (Buy_AC_buyBobPetShopStan . Buy_AC_assoc)) . univ) ";
		test("test214", ocl, expected);
	}

	@Test
	public void test215() {
		String ocl = "buyBobPetShopStan.petAC";
		String expected = "(Buy_AC_buyBobPetShopStan = Undefined) => Undefined else (univ . (univ . (Buy_AC_buyBobPetShopStan . Buy_AC_assoc))) ";
		test("test215", ocl, expected);
	}

	@Test
	public void test216() {
		String ocl = "ada.examinerA[examineeA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . Exam_A) . univ) ";
		test("test216", ocl, expected);
	}

	@Test
	public void test217() {
		String ocl = "ada.recorderA[examineeA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . Exam_A)) ";
		test("test217", ocl, expected);
	}

	@Test
	public void test218() {
		String ocl = "ada.examineeA[examinerA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_ada) ";
		test("test218", ocl, expected);
	}

	@Test
	public void test219() {
		String ocl = "ada.recorderA[examinerA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . Exam_A)) ";
		test("test219", ocl, expected);
	}

	@Test
	public void test220() {
		String ocl = "ada.examineeA[recorderA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Exam_A . Person_ada) . univ) ";
		test("test220", ocl, expected);
	}

	@Test
	public void test221() {
		String ocl = "ada.examinerA[recorderA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Exam_A . Person_ada)) ";
		test("test221", ocl, expected);
	}

	@Test
	public void test222() {
		String ocl = "bob.examinerA[examineeA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . Exam_A) . univ) ";
		test("test222", ocl, expected);
	}

	@Test
	public void test223() {
		String ocl = "bob.recorderA[examineeA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . Exam_A)) ";
		test("test223", ocl, expected);
	}

	@Test
	public void test224() {
		String ocl = "bob.examineeA[examinerA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_bob) ";
		test("test224", ocl, expected);
	}

	@Test
	public void test225() {
		String ocl = "bob.recorderA[examinerA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . Exam_A)) ";
		test("test225", ocl, expected);
	}

	@Test
	public void test226() {
		String ocl = "bob.examineeA[recorderA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Exam_A . Person_bob) . univ) ";
		test("test226", ocl, expected);
	}

	@Test
	public void test227() {
		String ocl = "bob.examinerA[recorderA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Exam_A . Person_bob)) ";
		test("test227", ocl, expected);
	}

	@Test
	public void test228() {
		String ocl = "cyd.examinerA[examineeA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Person_cyd . Exam_A) . univ) ";
		test("test228", ocl, expected);
	}

	@Test
	public void test229() {
		String ocl = "cyd.recorderA[examineeA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Person_cyd . Exam_A)) ";
		test("test229", ocl, expected);
	}

	@Test
	public void test230() {
		String ocl = "cyd.examineeA[examinerA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_cyd) ";
		test("test230", ocl, expected);
	}

	@Test
	public void test231() {
		String ocl = "cyd.recorderA[examinerA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . (univ . Exam_A)) ";
		test("test231", ocl, expected);
	}

	@Test
	public void test232() {
		String ocl = "cyd.examineeA[recorderA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Exam_A . Person_cyd) . univ) ";
		test("test232", ocl, expected);
	}

	@Test
	public void test233() {
		String ocl = "cyd.examinerA[recorderA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Exam_A . Person_cyd)) ";
		test("test233", ocl, expected);
	}

	@Test
	public void test234() {
		String ocl = "uf.person.examinerA[examineeA]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else ((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . Exam_A) . univ) ";
		test("test234", ocl, expected);
	}

	@Test
	public void test235() {
		String ocl = "uf.person.recorderA[examineeA]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . Exam_A)) ";
		test("test235", ocl, expected);
	}

	@Test
	public void test236() {
		String ocl = "uf.person.examineeA[examinerA]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else ((Exam_A . univ) . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))) ";
		test("test236", ocl, expected);
	}

	@Test
	public void test237() {
		String ocl = "uf.person.recorderA[examinerA]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . Exam_A)) ";
		test("test237", ocl, expected);
	}

	@Test
	public void test238() {
		String ocl = "uf.person.examineeA[recorderA]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else ((Exam_A . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))) . univ) ";
		test("test238", ocl, expected);
	}

	@Test
	public void test239() {
		String ocl = "uf.person.examinerA[recorderA]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . (Exam_A . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) ";
		test("test239", ocl, expected);
	}

	@Test
	public void test240() {
		String ocl = "ada.examinerA[examineeA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . Exam_A) . univ) ";
		test("test240", ocl, expected);
	}

	@Test
	public void test241() {
		String ocl = "ada.recorderA[examineeA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . Exam_A)) ";
		test("test241", ocl, expected);
	}

	@Test
	public void test242() {
		String ocl = "ada.examineeA[examinerA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_ada) ";
		test("test242", ocl, expected);
	}

	@Test
	public void test243() {
		String ocl = "ada.recorderA[examinerA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . Exam_A)) ";
		test("test243", ocl, expected);
	}

	@Test
	public void test244() {
		String ocl = "ada.examineeA[recorderA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Exam_A . Person_ada) . univ) ";
		test("test244", ocl, expected);
	}

	@Test
	public void test245() {
		String ocl = "ada.examinerA[recorderA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Exam_A . Person_ada)) ";
		test("test245", ocl, expected);
	}

	@Test
	public void test246() {
		String ocl = "bob.examinerA[examineeA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . Exam_A) . univ) ";
		test("test246", ocl, expected);
	}

	@Test
	public void test247() {
		String ocl = "bob.recorderA[examineeA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . Exam_A)) ";
		test("test247", ocl, expected);
	}

	@Test
	public void test248() {
		String ocl = "bob.examineeA[examinerA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_bob) ";
		test("test248", ocl, expected);
	}

	@Test
	public void test249() {
		String ocl = "bob.recorderA[examinerA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . Exam_A)) ";
		test("test249", ocl, expected);
	}

	@Test
	public void test250() {
		String ocl = "bob.examineeA[recorderA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Exam_A . Person_bob) . univ) ";
		test("test250", ocl, expected);
	}

	@Test
	public void test251() {
		String ocl = "bob.examinerA[recorderA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Exam_A . Person_bob)) ";
		test("test251", ocl, expected);
	}

	@Test
	public void test252() {
		String ocl = "cyd.examinerA[examineeA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Person_cyd . Exam_A) . univ) ";
		test("test252", ocl, expected);
	}

	@Test
	public void test253() {
		String ocl = "cyd.recorderA[examineeA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Person_cyd . Exam_A)) ";
		test("test253", ocl, expected);
	}

	@Test
	public void test254() {
		String ocl = "cyd.examineeA[examinerA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_cyd) ";
		test("test254", ocl, expected);
	}

	@Test
	public void test255() {
		String ocl = "cyd.recorderA[examinerA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . (univ . Exam_A)) ";
		test("test255", ocl, expected);
	}

	@Test
	public void test256() {
		String ocl = "cyd.examineeA[recorderA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Exam_A . Person_cyd) . univ) ";
		test("test256", ocl, expected);
	}

	@Test
	public void test257() {
		String ocl = "cyd.examinerA[recorderA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Exam_A . Person_cyd)) ";
		test("test257", ocl, expected);
	}

	@Test
	public void test258() {
		String ocl = "ada.examinerA[examineeA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . Exam_A) . univ) ";
		test("test258", ocl, expected);
	}

	@Test
	public void test259() {
		String ocl = "ada.recorderA[examineeA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . Exam_A)) ";
		test("test259", ocl, expected);
	}

	@Test
	public void test260() {
		String ocl = "ada.examineeA[examinerA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_ada) ";
		test("test260", ocl, expected);
	}

	@Test
	public void test261() {
		String ocl = "ada.recorderA[examinerA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . Exam_A)) ";
		test("test261", ocl, expected);
	}

	@Test
	public void test262() {
		String ocl = "ada.examineeA[recorderA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Exam_A . Person_ada) . univ) ";
		test("test262", ocl, expected);
	}

	@Test
	public void test263() {
		String ocl = "ada.examinerA[recorderA]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Exam_A . Person_ada)) ";
		test("test263", ocl, expected);
	}

	@Test
	public void test264() {
		String ocl = "bob.examinerA[examineeA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . Exam_A) . univ) ";
		test("test264", ocl, expected);
	}

	@Test
	public void test265() {
		String ocl = "bob.recorderA[examineeA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . Exam_A)) ";
		test("test265", ocl, expected);
	}

	@Test
	public void test266() {
		String ocl = "bob.examineeA[examinerA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_bob) ";
		test("test266", ocl, expected);
	}

	@Test
	public void test267() {
		String ocl = "bob.recorderA[examinerA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . Exam_A)) ";
		test("test267", ocl, expected);
	}

	@Test
	public void test268() {
		String ocl = "bob.examineeA[recorderA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Exam_A . Person_bob) . univ) ";
		test("test268", ocl, expected);
	}

	@Test
	public void test269() {
		String ocl = "bob.examinerA[recorderA]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Exam_A . Person_bob)) ";
		test("test269", ocl, expected);
	}

	@Test
	public void test270() {
		String ocl = "cyd.examinerA[examineeA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Person_cyd . Exam_A) . univ) ";
		test("test270", ocl, expected);
	}

	@Test
	public void test271() {
		String ocl = "cyd.recorderA[examineeA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Person_cyd . Exam_A)) ";
		test("test271", ocl, expected);
	}

	@Test
	public void test272() {
		String ocl = "cyd.examineeA[examinerA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_cyd) ";
		test("test272", ocl, expected);
	}

	@Test
	public void test273() {
		String ocl = "cyd.recorderA[examinerA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . (univ . Exam_A)) ";
		test("test273", ocl, expected);
	}

	@Test
	public void test274() {
		String ocl = "cyd.examineeA[recorderA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Exam_A . Person_cyd) . univ) ";
		test("test274", ocl, expected);
	}

	@Test
	public void test275() {
		String ocl = "cyd.examinerA[recorderA]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Exam_A . Person_cyd)) ";
		test("test275", ocl, expected);
	}

	@Test
	public void test276() {
		String ocl = "dan.examinerA[examineeA]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else ((Person_dan . Exam_A) . univ) ";
		test("test276", ocl, expected);
	}

	@Test
	public void test277() {
		String ocl = "dan.recorderA[examineeA]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (univ . (Person_dan . Exam_A)) ";
		test("test277", ocl, expected);
	}

	@Test
	public void test278() {
		String ocl = "dan.examineeA[examinerA]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else ((Exam_A . univ) . Person_dan) ";
		test("test278", ocl, expected);
	}

	@Test
	public void test279() {
		String ocl = "dan.recorderA[examinerA]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (Person_dan . (univ . Exam_A)) ";
		test("test279", ocl, expected);
	}

	@Test
	public void test280() {
		String ocl = "dan.examineeA[recorderA]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else ((Exam_A . Person_dan) . univ) ";
		test("test280", ocl, expected);
	}

	@Test
	public void test281() {
		String ocl = "dan.examinerA[recorderA]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (univ . (Exam_A . Person_dan)) ";
		test("test281", ocl, expected);
	}

	@Test
	public void test282() {
		String ocl = "ada.examinerAC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . (univ . Exam_AC_assoc)) . univ) ";
		test("test282", ocl, expected);
	}

	@Test
	public void test283() {
		String ocl = "ada.recorderAC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . (univ . Exam_AC_assoc))) ";
		test("test283", ocl, expected);
	}

	@Test
	public void test284() {
		String ocl = "ada.examineeAC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_ada)) ";
		test("test284", ocl, expected);
	}

	@Test
	public void test285() {
		String ocl = "ada.recorderAC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . (univ . Exam_AC_assoc))) ";
		test("test285", ocl, expected);
	}

	@Test
	public void test286() {
		String ocl = "ada.examineeAC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_ada) . univ)) ";
		test("test286", ocl, expected);
	}

	@Test
	public void test287() {
		String ocl = "ada.examinerAC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_ada))) ";
		test("test287", ocl, expected);
	}

	@Test
	public void test288() {
		String ocl = "ada.exam_AC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_ada) ";
		test("test288", ocl, expected);
	}

	@Test
	public void test289() {
		String ocl = "ada.exam_AC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_ada) . univ) ";
		test("test289", ocl, expected);
	}

	@Test
	public void test290() {
		String ocl = "ada.exam_AC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_ada) . univ) . univ) ";
		test("test290", ocl, expected);
	}

	@Test
	public void test291() {
		String ocl = "bob.examinerAC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . (univ . Exam_AC_assoc)) . univ) ";
		test("test291", ocl, expected);
	}

	@Test
	public void test292() {
		String ocl = "bob.recorderAC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . (univ . Exam_AC_assoc))) ";
		test("test292", ocl, expected);
	}

	@Test
	public void test293() {
		String ocl = "bob.examineeAC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_bob)) ";
		test("test293", ocl, expected);
	}

	@Test
	public void test294() {
		String ocl = "bob.recorderAC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . (univ . Exam_AC_assoc))) ";
		test("test294", ocl, expected);
	}

	@Test
	public void test295() {
		String ocl = "bob.examineeAC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_bob) . univ)) ";
		test("test295", ocl, expected);
	}

	@Test
	public void test296() {
		String ocl = "bob.examinerAC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_bob))) ";
		test("test296", ocl, expected);
	}

	@Test
	public void test297() {
		String ocl = "bob.exam_AC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_bob) ";
		test("test297", ocl, expected);
	}

	@Test
	public void test298() {
		String ocl = "bob.exam_AC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_bob) . univ) ";
		test("test298", ocl, expected);
	}

	@Test
	public void test299() {
		String ocl = "bob.exam_AC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_bob) . univ) . univ) ";
		test("test299", ocl, expected);
	}

	@Test
	public void test300() {
		String ocl = "cyd.examinerAC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Person_cyd . (univ . Exam_AC_assoc)) . univ) ";
		test("test300", ocl, expected);
	}

	@Test
	public void test301() {
		String ocl = "cyd.recorderAC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Person_cyd . (univ . Exam_AC_assoc))) ";
		test("test301", ocl, expected);
	}

	@Test
	public void test302() {
		String ocl = "cyd.examineeAC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_cyd)) ";
		test("test302", ocl, expected);
	}

	@Test
	public void test303() {
		String ocl = "cyd.recorderAC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . (univ . (univ . Exam_AC_assoc))) ";
		test("test303", ocl, expected);
	}

	@Test
	public void test304() {
		String ocl = "cyd.examineeAC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_cyd) . univ)) ";
		test("test304", ocl, expected);
	}

	@Test
	public void test305() {
		String ocl = "cyd.examinerAC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_cyd))) ";
		test("test305", ocl, expected);
	}

	@Test
	public void test306() {
		String ocl = "cyd.exam_AC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_cyd) ";
		test("test306", ocl, expected);
	}

	@Test
	public void test307() {
		String ocl = "cyd.exam_AC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_cyd) . univ) ";
		test("test307", ocl, expected);
	}

	@Test
	public void test308() {
		String ocl = "cyd.exam_AC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_cyd) . univ) . univ) ";
		test("test308", ocl, expected);
	}

	@Test
	public void test309() {
		String ocl = "uf.person.examinerAC[examineeAC]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else ((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . Exam_AC_assoc)) . univ) ";
		test("test309", ocl, expected);
	}

	@Test
	public void test310() {
		String ocl = "uf.person.recorderAC[examineeAC]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . Exam_AC_assoc))) ";
		test("test310", ocl, expected);
	}

	@Test
	public void test311() {
		String ocl = "uf.person.examineeAC[examinerAC]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)))) ";
		test("test311", ocl, expected);
	}

	@Test
	public void test312() {
		String ocl = "uf.person.recorderAC[examinerAC]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) . (univ . (univ . Exam_AC_assoc))) ";
		test("test312", ocl, expected);
	}

	@Test
	public void test313() {
		String ocl = "uf.person.examineeAC[recorderAC]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))) . univ)) ";
		test("test313", ocl, expected);
	}

	@Test
	public void test314() {
		String ocl = "uf.person.examinerAC[recorderAC]";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person)) = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . ((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_person))))) ";
		test("test314", ocl, expected);
	}

	@Test
	public void test315() {
		String ocl = "uf.exam.examineeAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_exam)) = Undefined) => Undefined else (((((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_exam)) . Exam_AC_assoc) . univ) . univ ) ";
		test("test315", ocl, expected);
	}

	@Test
	public void test316() {
		String ocl = "uf.exam.examinerAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_exam)) = Undefined) => Undefined else ((univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_exam)) . Exam_AC_assoc)) . univ) ";
		test("test316", ocl, expected);
	}

	@Test
	public void test317() {
		String ocl = "uf.exam.recorderAC";
		String expected = "(((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_exam)) = Undefined) => Undefined else (univ . (univ . (((UndefinedFactory_uf = Undefined) => Undefined else (UndefinedFactory_uf . UndefinedFactory_exam)) . Exam_AC_assoc))) ";
		test("test317", ocl, expected);
	}

	@Test
	public void test318() {
		String ocl = "ada.examinerAC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . (univ . Exam_AC_assoc)) . univ) ";
		test("test318", ocl, expected);
	}

	@Test
	public void test319() {
		String ocl = "ada.recorderAC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . (univ . Exam_AC_assoc))) ";
		test("test319", ocl, expected);
	}

	@Test
	public void test320() {
		String ocl = "ada.examineeAC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_ada)) ";
		test("test320", ocl, expected);
	}

	@Test
	public void test321() {
		String ocl = "ada.recorderAC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . (univ . Exam_AC_assoc))) ";
		test("test321", ocl, expected);
	}

	@Test
	public void test322() {
		String ocl = "ada.examineeAC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_ada) . univ)) ";
		test("test322", ocl, expected);
	}

	@Test
	public void test323() {
		String ocl = "ada.examinerAC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_ada))) ";
		test("test323", ocl, expected);
	}

	@Test
	public void test324() {
		String ocl = "ada.exam_AC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_ada) ";
		test("test324", ocl, expected);
	}

	@Test
	public void test325() {
		String ocl = "ada.exam_AC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_ada) . univ) ";
		test("test325", ocl, expected);
	}

	@Test
	public void test326() {
		String ocl = "ada.exam_AC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_ada) . univ) . univ) ";
		test("test326", ocl, expected);
	}

	@Test
	public void test327() {
		String ocl = "bob.examinerAC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . (univ . Exam_AC_assoc)) . univ) ";
		test("test327", ocl, expected);
	}

	@Test
	public void test328() {
		String ocl = "bob.recorderAC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . (univ . Exam_AC_assoc))) ";
		test("test328", ocl, expected);
	}

	@Test
	public void test329() {
		String ocl = "bob.examineeAC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_bob)) ";
		test("test329", ocl, expected);
	}

	@Test
	public void test330() {
		String ocl = "bob.recorderAC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . (univ . Exam_AC_assoc))) ";
		test("test330", ocl, expected);
	}

	@Test
	public void test331() {
		String ocl = "bob.examineeAC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_bob) . univ)) ";
		test("test331", ocl, expected);
	}

	@Test
	public void test332() {
		String ocl = "bob.examinerAC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_bob))) ";
		test("test332", ocl, expected);
	}

	@Test
	public void test333() {
		String ocl = "bob.exam_AC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_bob) ";
		test("test333", ocl, expected);
	}

	@Test
	public void test334() {
		String ocl = "bob.exam_AC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_bob) . univ) ";
		test("test334", ocl, expected);
	}

	@Test
	public void test335() {
		String ocl = "bob.exam_AC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_bob) . univ) . univ) ";
		test("test335", ocl, expected);
	}

	@Test
	public void test336() {
		String ocl = "cyd.examinerAC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Person_cyd . (univ . Exam_AC_assoc)) . univ) ";
		test("test336", ocl, expected);
	}

	@Test
	public void test337() {
		String ocl = "cyd.recorderAC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Person_cyd . (univ . Exam_AC_assoc))) ";
		test("test337", ocl, expected);
	}

	@Test
	public void test338() {
		String ocl = "cyd.examineeAC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_cyd)) ";
		test("test338", ocl, expected);
	}

	@Test
	public void test339() {
		String ocl = "cyd.recorderAC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . (univ . (univ . Exam_AC_assoc))) ";
		test("test339", ocl, expected);
	}

	@Test
	public void test340() {
		String ocl = "cyd.examineeAC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_cyd) . univ)) ";
		test("test340", ocl, expected);
	}

	@Test
	public void test341() {
		String ocl = "cyd.examinerAC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_cyd))) ";
		test("test341", ocl, expected);
	}

	@Test
	public void test342() {
		String ocl = "cyd.exam_AC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_cyd) ";
		test("test342", ocl, expected);
	}

	@Test
	public void test343() {
		String ocl = "cyd.exam_AC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_cyd) . univ) ";
		test("test343", ocl, expected);
	}

	@Test
	public void test344() {
		String ocl = "cyd.exam_AC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_cyd) . univ) . univ) ";
		test("test344", ocl, expected);
	}

	@Test
	public void test345() {
		String ocl = "examAdaBobCyd.examineeAC";
		String expected = "(Exam_AC_examAdaBobCyd = Undefined) => Undefined else (((Exam_AC_examAdaBobCyd . Exam_AC_assoc) . univ) . univ) ";
		test("test345", ocl, expected);
	}

	@Test
	public void test346() {
		String ocl = "examAdaBobCyd.examinerAC";
		String expected = "(Exam_AC_examAdaBobCyd = Undefined) => Undefined else ((univ . (Exam_AC_examAdaBobCyd . Exam_AC_assoc)) . univ) ";
		test("test346", ocl, expected);
	}

	@Test
	public void test347() {
		String ocl = "examAdaBobCyd.recorderAC";
		String expected = "(Exam_AC_examAdaBobCyd = Undefined) => Undefined else (univ . (univ . (Exam_AC_examAdaBobCyd . Exam_AC_assoc))) ";
		test("test347", ocl, expected);
	}

	@Test
	public void test348() {
		String ocl = "ada.examinerAC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else ((Person_ada . (univ . Exam_AC_assoc)) . univ) ";
		test("test348", ocl, expected);
	}

	@Test
	public void test349() {
		String ocl = "ada.recorderAC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (Person_ada . (univ . Exam_AC_assoc))) ";
		test("test349", ocl, expected);
	}

	@Test
	public void test350() {
		String ocl = "ada.examineeAC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_ada)) ";
		test("test350", ocl, expected);
	}

	@Test
	public void test351() {
		String ocl = "ada.recorderAC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (Person_ada . (univ . (univ . Exam_AC_assoc))) ";
		test("test351", ocl, expected);
	}

	@Test
	public void test352() {
		String ocl = "ada.examineeAC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_ada) . univ)) ";
		test("test352", ocl, expected);
	}

	@Test
	public void test353() {
		String ocl = "ada.examinerAC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_ada))) ";
		test("test353", ocl, expected);
	}

	@Test
	public void test354() {
		String ocl = "ada.exam_AC[examineeAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_ada) ";
		test("test354", ocl, expected);
	}

	@Test
	public void test355() {
		String ocl = "ada.exam_AC[examinerAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_ada) . univ) ";
		test("test355", ocl, expected);
	}

	@Test
	public void test356() {
		String ocl = "ada.exam_AC[recorderAC]";
		String expected = "(Person_ada = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_ada) . univ) . univ) ";
		test("test356", ocl, expected);
	}

	@Test
	public void test357() {
		String ocl = "bob.examinerAC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else ((Person_bob . (univ . Exam_AC_assoc)) . univ) ";
		test("test357", ocl, expected);
	}

	@Test
	public void test358() {
		String ocl = "bob.recorderAC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (Person_bob . (univ . Exam_AC_assoc))) ";
		test("test358", ocl, expected);
	}

	@Test
	public void test359() {
		String ocl = "bob.examineeAC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_bob)) ";
		test("test359", ocl, expected);
	}

	@Test
	public void test360() {
		String ocl = "bob.recorderAC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (Person_bob . (univ . (univ . Exam_AC_assoc))) ";
		test("test360", ocl, expected);
	}

	@Test
	public void test361() {
		String ocl = "bob.examineeAC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_bob) . univ)) ";
		test("test361", ocl, expected);
	}

	@Test
	public void test362() {
		String ocl = "bob.examinerAC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_bob))) ";
		test("test362", ocl, expected);
	}

	@Test
	public void test363() {
		String ocl = "bob.exam_AC[examineeAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_bob) ";
		test("test363", ocl, expected);
	}

	@Test
	public void test364() {
		String ocl = "bob.exam_AC[examinerAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_bob) . univ) ";
		test("test364", ocl, expected);
	}

	@Test
	public void test365() {
		String ocl = "bob.exam_AC[recorderAC]";
		String expected = "(Person_bob = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_bob) . univ) . univ) ";
		test("test365", ocl, expected);
	}

	@Test
	public void test366() {
		String ocl = "cyd.examinerAC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else ((Person_cyd . (univ . Exam_AC_assoc)) . univ) ";
		test("test366", ocl, expected);
	}

	@Test
	public void test367() {
		String ocl = "cyd.recorderAC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (Person_cyd . (univ . Exam_AC_assoc))) ";
		test("test367", ocl, expected);
	}

	@Test
	public void test368() {
		String ocl = "cyd.examineeAC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_cyd)) ";
		test("test368", ocl, expected);
	}

	@Test
	public void test369() {
		String ocl = "cyd.recorderAC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (Person_cyd . (univ . (univ . Exam_AC_assoc))) ";
		test("test369", ocl, expected);
	}

	@Test
	public void test370() {
		String ocl = "cyd.examineeAC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_cyd) . univ)) ";
		test("test370", ocl, expected);
	}

	@Test
	public void test371() {
		String ocl = "cyd.examinerAC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_cyd))) ";
		test("test371", ocl, expected);
	}

	@Test
	public void test372() {
		String ocl = "cyd.exam_AC[examineeAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_cyd) ";
		test("test372", ocl, expected);
	}

	@Test
	public void test373() {
		String ocl = "cyd.exam_AC[examinerAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_cyd) . univ) ";
		test("test373", ocl, expected);
	}

	@Test
	public void test374() {
		String ocl = "cyd.exam_AC[recorderAC]";
		String expected = "(Person_cyd = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_cyd) . univ) . univ) ";
		test("test374", ocl, expected);
	}

	@Test
	public void test375() {
		String ocl = "dan.examinerAC[examineeAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else ((Person_dan . (univ . Exam_AC_assoc)) . univ) ";
		test("test375", ocl, expected);
	}

	@Test
	public void test376() {
		String ocl = "dan.recorderAC[examineeAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (univ . (Person_dan . (univ . Exam_AC_assoc))) ";
		test("test376", ocl, expected);
	}

	@Test
	public void test377() {
		String ocl = "dan.examineeAC[examinerAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . univ) . Person_dan)) ";
		test("test377", ocl, expected);
	}

	@Test
	public void test378() {
		String ocl = "dan.recorderAC[examinerAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (Person_dan . (univ . (univ . Exam_AC_assoc))) ";
		test("test378", ocl, expected);
	}

	@Test
	public void test379() {
		String ocl = "dan.examineeAC[recorderAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (univ . ((Exam_AC_assoc . Person_dan) . univ)) ";
		test("test379", ocl, expected);
	}

	@Test
	public void test380() {
		String ocl = "dan.examinerAC[recorderAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (univ . (univ . (Exam_AC_assoc . Person_dan))) ";
		test("test380", ocl, expected);
	}

	@Test
	public void test381() {
		String ocl = "dan.exam_AC[examineeAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . univ) . Person_dan) ";
		test("test381", ocl, expected);
	}

	@Test
	public void test382() {
		String ocl = "dan.exam_AC[examinerAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (((Exam_AC_assoc . univ) . Person_dan) . univ) ";
		test("test382", ocl, expected);
	}

	@Test
	public void test383() {
		String ocl = "dan.exam_AC[recorderAC]";
		String expected = "(Person_dan = Undefined) => Undefined_Set else (((Exam_AC_assoc . Person_dan) . univ) . univ) ";
		test("test383", ocl, expected);
	}

	@Test
	public void test384() {
		String ocl = "examAdaBobCyd.examineeAC";
		String expected = "(Exam_AC_examAdaBobCyd = Undefined) => Undefined else (((Exam_AC_examAdaBobCyd . Exam_AC_assoc) . univ) . univ) ";
		test("test384", ocl, expected);
	}

	@Test
	public void test385() {
		String ocl = "examAdaBobCyd.examinerAC";
		String expected = "(Exam_AC_examAdaBobCyd = Undefined) => Undefined else ((univ . (Exam_AC_examAdaBobCyd . Exam_AC_assoc)) . univ) ";
		test("test385", ocl, expected);
	}

	@Test
	public void test386() {
		String ocl = "examAdaBobCyd.recorderAC";
		String expected = "(Exam_AC_examAdaBobCyd = Undefined) => Undefined else (univ . (univ . (Exam_AC_examAdaBobCyd . Exam_AC_assoc))) ";
		test("test386", ocl, expected);
	}

	@Test
	public void test387() {
		String ocl = "examDanBobCyd.examineeAC";
		String expected = "(Exam_AC_examDanBobCyd = Undefined) => Undefined else (((Exam_AC_examDanBobCyd . Exam_AC_assoc) . univ) . univ) ";
		test("test387", ocl, expected);
	}

	@Test
	public void test388() {
		String ocl = "examDanBobCyd.examinerAC";
		String expected = "(Exam_AC_examDanBobCyd = Undefined) => Undefined else ((univ . (Exam_AC_examDanBobCyd . Exam_AC_assoc)) . univ) ";
		test("test388", ocl, expected);
	}

	@Test
	public void test389() {
		String ocl = "examDanBobCyd.recorderAC";
		String expected = "(Exam_AC_examDanBobCyd = Undefined) => Undefined else (univ . (univ . (Exam_AC_examDanBobCyd . Exam_AC_assoc))) ";
		test("test389", ocl, expected);
	}

}
