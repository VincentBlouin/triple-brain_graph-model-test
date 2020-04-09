/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import com.googlecode.junittoolbox.WildcardPatternSuite;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.runner.RunWith;
import com.googlecode.junittoolbox.SuiteClasses;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("**/*Test.class")
public class ModelTests extends  ModelTestRunner {}