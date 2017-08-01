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

/**
 * This is the base class for linkage from one method to another member. This
 * information is used by the linker to determine which links can actually be
 * made.
 *
 * @since 2016/09/06
 */
public abstract class MemberLinkage<L extends MemberReference>
	implements Linkage
{
	/** The source method. */
	protected final MethodReference from;
	
	/** The target field. */
	protected final L to;
	
	/**
	 * Initializes the base linkage details.
	 *
	 * @param __from The source method.
	 * @param __to The target member.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/06
	 */
	MemberLinkage(MethodReference __from, L __to)
		throws NullPointerException
	{
		// Check
		if (__from == null || __to == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.from = __from;
		this.to = __to;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/06
	 */
	@Override
	public boolean equals(Object __o)
	{
		// Must be the same
		if (!(__o instanceof MemberLinkage))
			return false;
		
		// Cast
		MemberLinkage<?> o = (MemberLinkage<?>)__o;
		return this.from.equals(o.from) && this.to.equals(o.to);
	}
	
	/**
	 * Returns the method which imports the member specified by {@link #to()}.
	 *
	 * @return The method which desires to link to another member.
	 * @since 2016/09/06
	 */
	public final MethodReference from()
	{
		return this.from;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/06
	 */
	@Override
	public int hashCode()
	{
		return this.from.hashCode() ^ this.to.hashCode();
	}
	
	/**
	 * Returns the target of the link.
	 *
	 * @return The member which is linked into.
	 * @since 2016/09/06
	 */
	public final L to()
	{
		return this.to;
	}
}

