// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import cc.squirreljme.plugin.util.SingleTaskOutputFile;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.process.JavaExecSpec;
import org.gradle.process.internal.DslExecActionFactory;
import org.gradle.workers.WorkerExecutor;

/**
 * Used to test the virtual machine, generates test results from the run.
 *
 * @since 2020/08/07
 */
public class MultiVMTestTask
	extends DefaultTask
	implements MultiVMExecutableTask
{
	/** Property for running single test. */
	public static final String SINGLE_TEST_PROPERTY =
		"test.single";
	
	/** The source set used. */
	protected final String sourceSet;
	
	/** The virtual machine type. */
	protected final VirtualMachineSpecifier vmType;
	
	/**
	 * Initializes the task.
	 * 
	 * @param __executor The executor for the task.
	 * @param __sourceSet The source set to use.
	 * @param __vmType The virtual machine type.
	 * @param __libTask The task used to create libraries, this may be directly
	 * depended upon.
	 * @since 2020/08/07
	 */
	@Inject
	public MultiVMTestTask(WorkerExecutor __executor, DslExecActionFactory __execSpec, String __sourceSet,
		VirtualMachineSpecifier __vmType, MultiVMLibraryTask __libTask)
		throws NullPointerException
	{
		if (__executor == null || __sourceSet == null || __vmType == null ||
			__libTask == null)
			throw new NullPointerException("NARG");
			
		// These are used at the test stage
		this.sourceSet = __sourceSet;
		this.vmType = __vmType;
		
		// Set details of this task
		this.setGroup("squirreljme");
		this.setDescription("Runs the various automated tests.");
		
		// Depends on the library to exist first
		this.dependsOn(this.getProject().provider(
			new VMRunDependencies(this, __sourceSet, __vmType)));
		
		// Additionally this depends on the emulator backend to be available
		this.dependsOn(new VMEmulatorDependencies(this, __vmType));
		
		// Add the entire JAR as input, so that if it changes for any reason
		// then all tests should be considered invalid and rerun
		this.getInputs().file(this.getProject().provider(
			new SingleTaskOutputFile(__libTask)));
		
		// All of the input source files to be tested
		this.getInputs().files(this.getProject().provider(
			new VMTestInputs(this, __sourceSet)));
		
		// All of the test results that are created
		this.getOutputs().files(this.getProject().provider(
			new VMTestOutputs(this, __sourceSet, __vmType)));
		
		// Only run if there are actual tests to run
		this.onlyIf(new CheckForTests(__sourceSet));
		
		// Performs the action of the task
		this.doLast(new MultiVMTestTaskAction(__executor, __sourceSet,
			__vmType));
		
		System.err.printf("DEBUG -- Spec? %s%n",
			__execSpec.newDecoratedJavaExecAction().getCommandLine());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/08/21
	 */
	@Override
	public String getSourceSet()
	{
		return this.sourceSet;
	}
}
