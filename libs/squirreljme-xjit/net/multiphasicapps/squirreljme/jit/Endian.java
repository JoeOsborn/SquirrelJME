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
 * This represents the byte orders which are available for the JIT to use which
 * determines the byte order of the memory when accessed by the CPU.
 *
 * @since 2017/01/31
 */
public enum Endian
{
	/** Big endian. */
	BIG,
	
	/** Little endian. */
	LITTLE,
	
	/** End. */
	;
}

