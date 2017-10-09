// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit.verifier;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This represents a single node within the class inheritence tree.
 *
 * @since 2017/10/09
 */
public final class FamilyNode
{
	/**
	 * Initializes the node within the tree.
	 *
	 * @param __tr The owning tree reference.
	 * @throws NullPointerException On null arguments.
	 * @throws VerificationException If there is a circular dependency within
	 * the loaded classes.
	 * @since 2017/10/09
	 */
	FamilyNode(Reference<FamilyTree> __tr)
		throws NullPointerException, VerificationException
	{
		if (__tr == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
}

