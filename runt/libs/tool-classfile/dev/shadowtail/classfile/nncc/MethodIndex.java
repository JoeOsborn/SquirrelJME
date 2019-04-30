// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodName;

/**
 * Represents an index of a method in the method table of a class.
 *
 * @since 2019/04/30
 */
public final class MethodIndex
{
	/** The name of the class. */
	public final ClassName inclass;
	
	/** The method name. */
	public final MethodName name;
	
	/** The method type. */
	public final MethodDescriptor type;
	
	/** The hashcode. */
	private int _hash;
	
	/** The string form. */
	private Reference<String> _string;
	
	/**
	 * Initializes the index holder.
	 *
	 * @param __cl The class this is in.
	 * @param __n The name of the method.
	 * @param __t The method type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/30
	 */
	public MethodIndex(ClassName __cl, MethodName __n, MethodDescriptor __t)
		throws NullPointerException
	{
		if (__cl == null || __n == null || __t == null)
			throw new NullPointerException("NARG");
		
		this.inclass = __cl;
		this.name = __n;
		this.type = __t;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/30
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof MethodIndex))
			return false;
		
		if (this.hashCode() != __o.hashCode())
			return false;
		
		MethodIndex o = (MethodIndex)__o;
		return this.inclass.equals(o.inclass) &&
			this.name.equals(o.name) &&
			this.type.equals(o.type);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/30
	 */
	@Override
	public final int hashCode()
	{
		int rv = this._hash;
		if (rv == 0)
			this._hash = (rv = this.inclass.hashCode() ^
				this.name.hashCode() ^ this.type.hashCode());
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/30
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = String.format(
				"%s::%s:%s", this.inclass, this.name, this.type)));
		
		return rv;
	}
}

