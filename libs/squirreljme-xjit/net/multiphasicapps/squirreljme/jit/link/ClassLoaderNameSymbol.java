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
 * This represents a symbol as it appears to the {@link ClassLoader}.
 *
 * @since 2016/04/06
 */
public final class ClassLoaderNameSymbol
	extends __BaseSymbol__
{
	/** Is this an array? */
	protected final boolean isarray;
	
	/** The class name of this symbol. */
	private volatile Reference<ClassNameSymbol> _cname;
	
	/** The name of this resource. */
	private volatile Reference<String> _rcname;
	
	/**
	 * Initializes the class loader symbol.
	 *
	 * @param __s The class loader name.
	 * @throws IllegalSymbolException If the symbol contains invalid
	 * characters.
	 * @since 2016/04/06
	 */
	public ClassLoaderNameSymbol(String __s)
		throws IllegalSymbolException
	{
		this(__s, null);
	}
	
	/**
	 * Initializes the class loader symbol with an optional cache.
	 *
	 * @param __s The class loader name.
	 * @param __cl The optional class name to cache.
	 * @throws IllegalSymbolException If the symbol contains invalid
	 * characters.
	 * @since 2016/04/06
	 */
	ClassLoaderNameSymbol(String __s, ClassNameSymbol __cl)
		throws IllegalSymbolException
	{
		super(__s);
		
		// If not an array, it cannot contain any forward slashes
		isarray = ('[' == charAt(0));
		if (!isarray)
		{
			// {@squirreljme.error AL0e Non-array class loader names cannot
			// contain forward slashes. (This symbol; The illegal character)}
			int n = length();
			char c;
			for (int i = 0; i < n; i++)
				if ((c = charAt(i)) == '/')
					throw new IllegalSymbolException(String.format(
						"AL0e %s %c", this, c));
		}
		
		// Pre-cache?
		if (__cl != null)
			_cname = new WeakReference<>(__cl);
		
		// Check for valid class name, since this could be an array too
		asClassName();
	}
	
	/**
	 * Returns this class loader symbol as a binary name symbol.
	 *
	 * @return The symbol as a binary name.
	 * @since 2016/10/01
	 */
	public BinaryNameSymbol asBinaryName()
	{
		return asClassName().asBinaryName();
	}
	
	/**
	 * Returns this class loader symbol as a class name symbol.
	 *
	 * @return The symbol as a class name.
	 * @since 2016/04/06
	 */
	public ClassNameSymbol asClassName()
	{
		// Get reference
		Reference<ClassNameSymbol> ref = _cname;
		ClassNameSymbol rv;
		
		// Needs to be created?
		if (ref == null || null == (rv = ref.get()))
		{
			// Arrays are treated like fields, otherwise the names of classes
			// get their dots replaced with slashes
			_cname = new WeakReference<>((rv = ClassNameSymbol.of(
				(isarray ? toString() : toString(). replace('.', '/')))));
		}
		
		// Return it
		return rv;
	}
	
	/**
	 * Returns the name of this class name as a resoiurce.
	 *
	 * @return The resource name or {@code null} if an array.
	 * @since 2016/04/06
	 */
	public String resourceName()
	{
		// Not valid for arrays
		if (isarray)
			return null;
		
		// Get reference
		Reference<String> ref = _rcname;
		String rv;
		
		// Needs caching?
		if (ref == null || (null == (rv = ref.get())))
			_rcname = new WeakReference<>((rv = '/' + toString().
				replace('.', '/') + ".class"));
		
		// Return it
		return rv;
	}
}

