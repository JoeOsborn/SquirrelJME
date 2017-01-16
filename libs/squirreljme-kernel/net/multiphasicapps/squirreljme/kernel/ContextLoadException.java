// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.kernel;

/**
 * This is thrown when at class initialization time the class could not be
 * initializes to be used by the current process.
 *
 * Some reasons the class may fail to load in a context:
 * It depends on another class which is not available to the run-time.
 * The executable of a given class failed to load.
 *
 * @since 2017/01/16
 */
public class ContextLoadException
	extends KernelException
{
	/**
	 * Initialize the exception with no message or cause.
	 *
	 * @since 2017/01/16
	 */
	public ContextLoadException()
	{
	}
	
	/**
	 * Initialize the exception with a message and no cause.
	 *
	 * @param __m The message.
	 * @since 2017/01/16
	 */
	public ContextLoadException(String __m)
	{
		super(__m);
	}
	
	/**
	 * Initialize the exception with a message and cause.
	 *
	 * @param __m The message.
	 * @param __c The cause.
	 * @since 2017/01/16
	 */
	public ContextLoadException(String __m, Throwable __c)
	{
		super(__m, __c);
	}
	
	/**
	 * Initialize the exception with no message and with a cause.
	 *
	 * @param __c The cause.
	 * @since 2017/01/16
	 */
	public ContextLoadException(Throwable __c)
	{
		super(__c);
	}
}

