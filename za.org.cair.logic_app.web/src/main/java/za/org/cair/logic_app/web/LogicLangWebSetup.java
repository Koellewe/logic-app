/*
 * generated by Xtext 2.24.0
 */
package za.org.cair.logic_app.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.xtext.util.Modules2;
import za.org.cair.logic_app.LogicLangRuntimeModule;
import za.org.cair.logic_app.LogicLangStandaloneSetup;
import za.org.cair.logic_app.ide.LogicLangIdeModule;

/**
 * Initialization support for running Xtext languages in web applications.
 */
public class LogicLangWebSetup extends LogicLangStandaloneSetup {
	
	@Override
	public Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new LogicLangRuntimeModule(), new LogicLangIdeModule(), new LogicLangWebModule()));
	}
	
}
