// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit.bin;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import net.multiphasicapps.squirreljme.jit.JITConfig;
import net.multiphasicapps.util.sorted.SortedTreeMap;

/**
 * This class contains the state of the linker, essentially it is the root
 * class which contains all of the other state needed to generate the output
 * executable. SquirrelJME operates in a purely static nature with no dynamic
 * function (that is simulated on top of the static state).
 *
 * @since 2017/06/15
 */
public final class LinkerState
{
	/**
	 * This is a singular self reference which sub-classes refer to when they
	 * need to access the linker state. This allows the JIT to be garbage
	 * collected in the reference counted SquirrelJME. This reference here is
	 * valid because if the holding class instance is garbage collected the
	 * references being only weak would be invalidated.
	 */
	protected final Reference<LinkerState> selfref =
		new WeakReference<>(this);
	
	/** Accesses which are performed statically and must be checked. */
	protected final Accesses accesses =
		new Accesses(this.selfref);
	
	/** Conditions which must all pass for compilation to succeed. */
	protected final Conditions conditions =
		new Conditions(this.selfref);
	
	/** The dynamic code generation reference table. */
	protected final Dynamics dynamics =
		new Dynamics(this.selfref);
	
	/** This contains the packages which exist to the linker. */
	protected final Packages packages =
		new Packages(this.selfref);
	
	/** Clusters which are available to the linker. */
	protected final Clusters clusters =
		new Clusters(this.selfref);
	
	/** Sections which should exit in the output executable. */
	protected final Sections sections =
		new Sections(this.selfref);
	
	/** Class units. */
	protected final Units units =
		new Units(this.selfref);
	
	/** The section counter which is used to count new sections. */
	protected final SectionCounter sectioncounter;
	
	/** The JIT configuration used for the output. */
	protected final JITConfig config;
	
	/**
	 * Initializes the linker state which holds everything which is needed to
	 * generate output code and state needed for the static executables to run.
	 *
	 * @param __conf The configuration to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/06/17
	 */
	public LinkerState(JITConfig __conf)
		throws NullPointerException
	{
		// Check
		if (__conf == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.config = __conf;
		this.sectioncounter = __conf.createSectionCounter();
	}
	
	/**
	 * Returns the cluster manager.
	 *
	 * @return The cluster manager.
	 * @since 2017/06/18 
	 */
	public final Clusters clusters()
	{
		return this.clusters;
	}
	
	/**
	 * Returns the condition table.
	 *
	 * @return The table of conditions which must pass.
	 * @since 2017/07/07
	 */
	public final Conditions conditions()
	{
		return this.conditions;
	}
	
	/**
	 * Returns the JIT configuration currently being used.
	 *
	 * @return The JIT configuration being used.
	 * @since 2017/06/28
	 */
	public final JITConfig config()
	{
		return this.config;
	}
	
	/**
	 * Creates a new fragment builder which is used to construct a fragment.
	 *
	 * @param __t The type of section this belongs in.
	 * @return A fragment builder used for building fragments.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/07/03
	 */
	public final FragmentBuilder createFragmentBuilder(SectionType __t)
		throws NullPointerException
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		return new FragmentBuilder(this.selfref, __t);
	}
	
	/**
	 * Returns the section manager which is used to provide access to the
	 * sections contained within the target executable.
	 *
	 * @return The section manager.
	 * @since 2017/07/02
	 */
	public final Sections sections()
	{
		return this.sections;
	}
	
	/**
	 * Returns the units which are available for usage.
	 *
	 * @return The units which are available.
	 * @since 2017/07/08
	 */
	public final Units units()
	{
		return this.units;
	}
	
	/**
	 * Returns the reference to this linker state.
	 *
	 * @return The linker state reference.
	 * @since 2017/06/20
	 */
	final Reference<LinkerState> __reference()
	{
		return this.selfref;
	}
	
	/**
	 * Returns the section counter which is used to map resources and classes
	 * to sections as needed.
	 *
	 * @return The section counter.
	 * @since 2017/07/02
	 */
	final SectionCounter __sectionCounter()
	{
		return this.sectioncounter;
	}
}

