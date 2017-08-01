// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit;

/**
 * This represents a variable state which is mutable in that it may be changed
 * as the program compilation progresses.
 *
 * @since 2017/05/25
 */
public final class MutableVariableState
	extends VariableState
{
	/**
	 * This represents a single mutable variable within a tread.
	 *
	 * @since 2017/05/27
	 */
	public final class MutableSlot
		extends VariableState.Slot
	{
	}
	
	/**
	 * This represents a mutable tread which contains mutable slots.
	 *
	 * @since 2017/05/27
	 */
	public final class MutableTread
		extends VariableState.Tread
	{
	}
}

