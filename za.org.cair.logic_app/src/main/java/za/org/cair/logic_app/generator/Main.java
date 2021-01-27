/*
 * generated by Xtext 2.24.0
 */
package za.org.cair.logic_app.generator;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import za.org.cair.logic_app.LogicLangHelper;
import za.org.cair.logic_app.LogicLangStandaloneSetup;

public class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Aborting: no path to EMF resource provided!");
			return;
		}
		Injector injector = new LogicLangStandaloneSetup().createInjectorAndDoEMFRegistration();
		Main main = injector.getInstance(Main.class);
		main.runGenerator(args[0]);
	}

	@Inject
	private Provider<ResourceSet> resourceSetProvider;

	@Inject
	private IResourceValidator validator;

	@Inject
	private GeneratorDelegate generator;

	@Inject 
	private JavaIoFileSystemAccess fileAccess;

	protected void runGenerator(String string) {
		// Load the resource
		ResourceSet set = resourceSetProvider.get();
		Resource resource = set.getResource(URI.createFileURI(string), true);

		// Validate the resource
		List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		if (!list.isEmpty()) {
			for (Issue issue : list) {
				System.err.println(issue);
			}
			return;
		}

		// Configure and start the generator
		fileAccess.setOutputPath("./");
		GeneratorContext context = new GeneratorContext();
		context.setCancelIndicator(CancelIndicator.NullImpl);
		generator.generate(resource, fileAccess, context);

		System.out.println("Code generation finished.");
	}
	
	/**
	 * These methods are used to run validation on some source code.
	 * They are needed, because for some bizarre reason validation doesn't
	 * work in JUnit 5 test context. 
	 * @param src source code
	 * @return A set of warnings and errors produced by the validator. 
	 * @throws IOException If there was an error creating or deleting temporary files.
	 */
	public static List<Issue> justValidate(String src) throws IOException{
		Injector injector = new LogicLangStandaloneSetup().createInjectorAndDoEMFRegistration();
		Main main = injector.getInstance(Main.class);
		return main.internalJustValidate(src);
	}
	
	private List<Issue> internalJustValidate(String src) throws IOException{
		String tmpFile = LogicLangHelper.randomHash(src.hashCode())+".logic";
		BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
		writer.write(src);
		writer.close();
		
		Resource resource = resourceSetProvider.get().getResource(URI.createFileURI(tmpFile), true);
		List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		new File(tmpFile).delete();
		return issues;
	}
}
