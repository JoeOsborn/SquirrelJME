// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.midp.lcdui;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This is the base class for displays.
 *
 * @since 2016/10/14
 */
public abstract class VirtualDisplay
{
	/** The display ID. */
	protected final byte id;
	
	/** The owning server. */
	protected final DisplayServer server;
	
	/**
	 * Initilaizes the virtual display.
	 *
	 * @param __ds The owning display server.
	 * @param __id The id of the display.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/14
	 */
	public VirtualDisplay(DisplayServer __ds, byte __id)
		throws NullPointerException
	{
		// Check
		if (__ds == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.id = __id;
		this.server = __ds;
	}
	
	/**
	 * This is the output loop where display events may be written to.
	 *
	 * @param __out The output data stream.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/15
	 */
	public abstract void outputLoop(DataOutputStream __out)
		throws IOException, NullPointerException;
	
	/**
	 * Returns the value of the given display property.
	 *
	 * @param __dp The display property to get.
	 * @return The value of the property.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/14
	 */
	public abstract int property(DisplayProperty __dp)
		throws NullPointerException;
	
	/**
	 * Returns the display ID.
	 *
	 * @return The display ID.
	 * @since 2016/10/14
	 */
	public final byte id()
	{
		return this.id;
	}
}

