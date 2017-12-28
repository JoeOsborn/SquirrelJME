// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.runtime.kernel;

/**
 * This represents the flags which may be used to represent a task.
 *
 * @since 2017/12/27
 */
public interface KernelTaskFlag
{
	/** The mask which is used for the process status. */
	public static final int STATUS_MASK =
		0x0000_0007;
	
	/** Is this a system task? */
	public static final int SYSTEM =
		0x0000_0008;
}

