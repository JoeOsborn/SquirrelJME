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

import net.multiphasicapps.squirreljme.jit.link.IdentifierSymbol;
import net.multiphasicapps.squirreljme.jit.link.MethodSymbol;
import net.multiphasicapps.squirreljme.jit.link.ClassExport;
import net.multiphasicapps.squirreljme.jit.link.Export;
import net.multiphasicapps.squirreljme.jit.link.MethodFlags;

/**
 * This contains a method that is exported by a class.
 *
 * @since 2017/04/04
 */
public final class ExportedMethod
	extends ExportedMember
{
	/**
	 * Initializes the exported method.
	 *
	 * @param __ce The containing class.
	 * @param __flags The flags for the method.
	 * @param __name The name of the method.
	 * @param __type The type of the method.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/09
	 */
	ExportedMethod(ClassExport __ce, MethodFlags __flags,
		IdentifierSymbol __name, MethodSymbol __type)
		throws NullPointerException
	{
		super(__ce, __flags, __name, __type);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/04/01
	 */
	@Override
	public boolean equals(Object __o)
	{
		return (__o instanceof ExportedMethod) && super.equals(__o);
	}
	
	/**
	 * Returns the flags for this exported method.
	 *
	 * @return The method flags.
	 * @since 2017/04/11
	 */
	public MethodFlags methodFlags()
	{
		return (MethodFlags)this.flags;
	}
	
	/**
	 * Returns the type of the method.
	 *
	 * @return The method type.
	 * @since 2017/04/12
	 */
	public MethodSymbol methodType()
	{
		return (MethodSymbol)this.type;
	}
}

