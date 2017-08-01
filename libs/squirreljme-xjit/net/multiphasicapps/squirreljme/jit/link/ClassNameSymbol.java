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
 * This is the name of the class as it appears in the constant pool of a class.
 *
 * It is either a {@link FieldSymbol} or a {@link BinaryNameSymbol}.
 *
 * @since 2016/03/15
 */
public final class ClassNameSymbol
	extends __BaseSymbol__
{
	/** Is this an array? */
	protected final boolean isarray;
	
	/** Is this a primitive type? */
	protected final boolean isprimitive;
	
	/** As a field symbol. */
	private volatile Reference<FieldSymbol> _asfield;
	
	/** As a binary name symbol. */
	private volatile Reference<BinaryNameSymbol> _asbinary;
	
	/** Class loader name. */
	private volatile Reference<ClassLoaderNameSymbol> _asclname;
	
	/**
	 * Initializes the class name symbol.
	 *
	 * @param __s The descriptor.
	 * @throws IllegalSymbolException If the class name is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/03/15
	 */
	public ClassNameSymbol(String __s)
		throws IllegalSymbolException, NullPointerException
	{
		this(__s, false, null);
	}
	
	/**
	 * Initializes the class name symbol.
	 *
	 * @param __s The descriptor.
	 * @param __prim Is this a primitive type?
	 * @param __clname Class loader name cache, is optional.
	 * @throws IllegalSymbolException If the class name is not valid.
	 * @since 2016/03/31
	 */
	private ClassNameSymbol(String __s, boolean __prim,
		ClassLoaderNameSymbol __clname)
		throws IllegalSymbolException, NullPointerException
	{
		super(__s);
		
		// Is an array?
		isarray = charAt(0) == '[';
		isprimitive = __prim;
		
		// Cache the class loader name
		if (__clname != null)
			_asclname = new WeakReference<>(__clname);
		
		// Check it by making it
		if (isarray || isprimitive)
			asField();
		else
			asBinaryName();
	}
	
	/**
	 * Returns the class name as a binary name.
	 *
	 * @return The binary name representation of the class.
	 * @throws IllegalSymbolException If this is an array or it is not a valid.
	 * @since 2016/03/15
	 */
	public BinaryNameSymbol asBinaryName()
		throws IllegalSymbolException
	{
		// {@squirreljme.error AL04 Primitive types or arrays cannot be
		// represented as a binary name. (This symbol)}
		if (isarray || isprimitive)
			throw new IllegalSymbolException(String.format("AL04 %s",
				toString()));
		
		// Get reference
		Reference<BinaryNameSymbol> ref = _asbinary;
		BinaryNameSymbol rv = null;
		
		// In the ref?
		if (ref != null)
			rv = ref.get();
		
		// Needs creation?
		if (rv == null)
			_asbinary = new WeakReference<>(
				(rv = BinaryNameSymbol.of(toString())));
		
		// Return it
		return rv;
	}
	
	/**
	 * Returns this class name as a class loader name.
	 *
	 * @return This class name as a class loader name.
	 * @since 2016/04/06
	 */
	public ClassLoaderNameSymbol asClassLoaderName()
	{
		// Get reference
		Reference<ClassLoaderNameSymbol> ref = _asclname;
		ClassLoaderNameSymbol rv;
		
		// Needs creation?
		if (ref == null || null == (rv = ref.get()))
			_asclname = new WeakReference<>((rv = new ClassLoaderNameSymbol(
				(isarray ? toString() : toString().replace('/', '.')), this)));
		
		// Return it
		return rv;
	}
	
	/**
	 * Returns the class name as a field.
	 *
	 * @return The field representation of the class.
	 * @throws IllegalSymbolException If this would produce an invalid field
	 * symbol.
	 * @since 2016/03/15
	 */
	public FieldSymbol asField()
		throws IllegalSymbolException
	{
		// Get reference
		Reference<FieldSymbol> ref = _asfield;
		FieldSymbol rv = null;
		
		// In the ref?
		if (ref != null)
			rv = ref.get();
		
		// Needs creation?
		if (rv == null)
			_asfield = new WeakReference<>((rv = FieldSymbol.of(
				((isarray || isprimitive) ? toString() :
					'L' + toString() + ';'))));
		
		// Return it
		return rv;
	}
	
	/**
	 * Is this an array?
	 *
	 * @return {@code true} if it is an array.
	 * @since 2016/03/15
	 */
	public boolean isArray()
	{
		return isarray;
	}
	
	/**
	 * Is this a primitive type?
	 *
	 * @return {@code true} if this is a primitive type.
	 * @since 2016/04/07
	 */
	public boolean isPrimitive()
	{
		return isprimitive;
	}
	
	/**
	 * Returns the package which contains this class.
	 *
	 * @return The parent package this is in.
	 * @since 2016/05/12
	 */
	public BinaryNameSymbol parentPackage()
	{
		// Arrays and primitive types are always in the special package
		if (isarray || isprimitive)
			return BinaryNameSymbol.SPECIAL_PACKAGE;
		
		// Otherwise, this is already done by the binary name
		return asBinaryName().parentPackage();
	}
}

