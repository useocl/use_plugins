package org.tzi.use.kodkod.transform.ocl.anyOperation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ Equality_Test.class, Inequality_Test.class, IsDefined_Test.class, OclAsType_Test.class, OclIsKindOf_Test.class,
		OclIsTypeOf_Test.class, OclIsUndefined_Test.class })
public class AnyTestSuite {
}
