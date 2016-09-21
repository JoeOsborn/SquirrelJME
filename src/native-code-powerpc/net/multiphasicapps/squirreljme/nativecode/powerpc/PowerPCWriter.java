// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.nativecode.powerpc;

import java.io.OutputStream;
import net.multiphasicapps.squirreljme.nativecode.NativeAllocation;
import net.multiphasicapps.squirreljme.nativecode.NativeCodeException;
import net.multiphasicapps.squirreljme.nativecode.NativeCodeWriter;
import net.multiphasicapps.squirreljme.nativecode.NativeCodeWriterOptions;

/**
 * This writes PowerPC machine code.
 *
 * @since 2016/09/14
 */
public class PowerPCWriter
	implements NativeCodeWriter
{
	/** The options to use for code generation. */
	protected final NativeCodeWriterOptions options;
	
	/** The instruction writer. */
	protected final PowerPCOutputStream output;
	
	/**
	 * Initializes the native code generator.
	 *
	 * @param __o The options to use.
	 * @param __os The output stream of machine code.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/15
	 */
	public PowerPCWriter(NativeCodeWriterOptions __o, OutputStream __os)
		throws NullPointerException
	{
		// Check
		if (__o == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.options = __o;
		this.output = new PowerPCOutputStream(__o, __os);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/21
	 */
	@Override
	public void copy(NativeAllocation __src, NativeAllocation __dest)
		throws NativeCodeException, NullPointerException
	{
		// Check
		if (__src == null || __dest == null)
			throw new NullPointerException("NARG");
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/15
	 */
	@Override
	public final NativeCodeWriterOptions options()
	{
		return this.options;
	}
}

