package org.tzi.use.kodkod.transform.ocl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.tzi.use.kodkod.transform.ocl.anyOperation.AnyTestSuite;
import org.tzi.use.kodkod.transform.ocl.attributeAccess.AttributeAccess_Test;
import org.tzi.use.kodkod.transform.ocl.booleanOperation.BooleanTestSuite;
import org.tzi.use.kodkod.transform.ocl.classOperation.AllInstances_Test;
import org.tzi.use.kodkod.transform.ocl.ifThenElse.IfThenElse_Test;
import org.tzi.use.kodkod.transform.ocl.integerOperation.IntegerTestSuite;
import org.tzi.use.kodkod.transform.ocl.let.Let_Test;
import org.tzi.use.kodkod.transform.ocl.navigation.Navigation_Test;
import org.tzi.use.kodkod.transform.ocl.setConstructor.SetConstructor_Test;
import org.tzi.use.kodkod.transform.ocl.setOperation.SetTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ AnyTestSuite.class, AttributeAccess_Test.class, BooleanTestSuite.class, AllInstances_Test.class, IfThenElse_Test.class,
		IntegerTestSuite.class, SetConstructor_Test.class, Navigation_Test.class, Let_Test.class, SetTestSuite.class })
public class TestSuite {
}
