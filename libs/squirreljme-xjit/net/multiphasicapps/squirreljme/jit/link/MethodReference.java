// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit.link;

import net.multiphasicapps.squirreljme.jit.link.ClassNameSymbol;
import net.multiphasicapps.squirreljme.jit.link.IdentifierSymbol;
import net.multiphasicapps.squirreljme.jit.link.MethodSymbol;

/**
 * This represents a reference to an interface method or a standard method.
 *
 * @since 2016/08/14
 */
public final class MethodReference
	extends MemberReference
{
	/** Is this an interface method reference? */
	protected final boolean isinterface;
	
	/**
	 * Initializes the method reference.
	 *
	 * @param __cn The class name.
	 * @param __mn The member name.
	 * @param __mt the member type.
	 * @param __int Is this an interface linkage?
	 * @since 2016/08/14
	 */
	public MethodReference(ClassNameSymbol __cn, IdentifierSymbol __mn,
		MethodSymbol __mt, boolean __int)
	{
		super(__cn, __mn, __mt);
		
		// Set
		this.isinterface =__int;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/14
	 */
	@Override
	public boolean equals(Object __o)
	{
		return super.equals(__o) && (__o instanceof MethodReference) &&
			this.isinterface == ((MethodReference)__o).isinterface;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/14
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode() ^ (this.isinterface ? 0xFFFFFFFF : 0);
	}
	
	/**
	 * Is this an interface method reference?
	 *
	 * @return {@code true} if this refers to an interface method.
	 * @since 2016/08/14
	 */
	public final boolean isInterface()
	{
		return this.isinterface;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/14
	 */
	@Override
	public MethodSymbol memberType()
	{
		return (MethodSymbol)this.membertype;
	}
	
	/**
	 * Returns the type of method that this is.
	 *
	 * @return The method type.
	 * @since 2016/09/06
	 */
	public MethodSymbol methodType()
	{
		return (MethodSymbol)this.membertype;
	}
}

