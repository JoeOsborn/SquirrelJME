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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This is a link from a method which access a field.
 *
 * @since 2016/09/06
 */
public final class FieldLinkage
	extends MemberLinkage<FieldReference>
{
	/** The type of link this is. */
	protected final FieldAccessType type;
	
	/** String reference. */
	private volatile Reference<String> _string;
	
	/**
	 * Initializes the link of one method to a field.
	 *
	 * @param __from The source method.
	 * @param __to The target field.
	 * @param __t How the field was accessed.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/06
	 */
	public FieldLinkage(MethodReference __from,
		FieldReference __to, FieldAccessType __t)
		throws NullPointerException
	{
		super(__from, __to);
		
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.type = __t;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/06
	 */
	@Override
	public boolean equals(Object __o)
	{
		// Must match
		if (!(__o instanceof FieldLinkage))
			return false;
		
		// Check super first
		if (!super.equals(__o))
			return false;
		
		return this.type.equals(((FieldLinkage)__o).type);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/06
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode() ^ this.type.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/06
	 */
	@Override
	public String toString()
	{
		// Get
		Reference<String> ref = this._string;
		String rv;
		
		// Cache?
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = this.from + "->" +
				this.to + "~[" + this.type + "]"));
		
		return rv;
	}
	
	/**
	 * Returns the type of access which was performed.
	 *
	 * @return The field access type.
	 * @since 2016/09/06
	 */
	public FieldAccessType type()
	{
		return this.type;
	}
}

