// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the Mozilla Public License Version 2.0.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jdwp.trips;

/**
 * Trip on virtual machine state.
 *
 * @since 2021/04/11
 */
public interface JDWPTripVmState
	extends JDWPTrip
{
	/**
	 * Is the virtual machine alive?
	 * 
	 * @param __bootThread The initial starting thread.
	 * @param __alive If the virtual machine is alive.
	 * @since 2021/04/11
	 */
	void alive(Object __bootThread, boolean __alive);
}
