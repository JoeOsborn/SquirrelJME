// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit.java;

/**
 * This represents the location where a variable is stored, which may be in
 * the local variables, on the stack, or within a temporary working area.
 *
 * @since 2017/03/31
 */
public enum VariableLocation
{
	/** Local variables. */
	LOCAL,
	
	/** Stack veriables. */
	STACK,
	
	/** These are temporary variables which are used during processing. */
	TEMPORARY,
	
	/** End. */
	;
	
	/** The number of area types available. */
	public static final int COUNT =
		values().length;
}

