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
 * This represents a field descriptor.
 *
 * @since 2016/03/14
 */
public final class FieldSymbol
	extends MemberTypeSymbol
	implements __ClassNameCompatible__
{
	/** Maximum array size. */
	public static final int MAX_ARRAY_DIMENSIONS =
		255;
	
	/** Integer. */
	public static final FieldSymbol INTEGER =
		new FieldSymbol("I");
	
	/** Long. */
	public static final FieldSymbol LONG =
		new FieldSymbol("J");
	
	/** Float. */
	public static final FieldSymbol FLOAT =
		new FieldSymbol("F");
	
	/** Double. */
	public static final FieldSymbol DOUBLE =
		new FieldSymbol("D");
	
	/** Array dimensions, will be zero if not an array. */
	protected final int dimensions;
	
	/** Component type of the array if it is one. */
	private volatile Reference<FieldSymbol> _componenttype;
	
	/** Base type for the field? */
	private volatile Reference<FieldBaseTypeSymbol> _basetype;
	
	/** As a class name? */
	private volatile Reference<ClassNameSymbol> _clname;
	
	/**
	 * Initializes the field symbol which represents the type of a field.
	 *
	 * @param __s Field descriptor data.
	 * @throws IllegalSymbolException If the field descriptor is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/04/04
	 */
	public FieldSymbol(String __s)
		throws IllegalSymbolException, NullPointerException
	{
		super(__s);
		
		// Read the number of dimensions this has, this modifies how the symbol
		// is handled.
		int n = length();
		int i;
		for (i = 0;	i < n; i++)
			if (charAt(i) != '[')
				break;
		
		// Set
		dimensions = i;
		
		// {@squirreljme.error AL07 There cannot be negative or more than 255
		// dimensions in an array. (This symbol; The dimension count)}
		if (dimensions < 0 || dimensions > MAX_ARRAY_DIMENSIONS)
			throw new IllegalSymbolException(String.format("AL07 %s %d",
				this, dimensions));
		
		// Just cache all of them to check for symbol validity
		binaryName();
		baseType();
	}
	
	/**
	 * Returns the number of dimensions in the array.
	 *
	 * @return The dimensions in the array, or {@code 0} if not an array.
	 * @since 2016/03/14
	 */
	public int arrayDimensions()
	{
		return dimensions;
	}
	
	/**
	 * Returns this binary name symbol as a class name symbol.
	 *
	 * @return The class name symbol representation of this.
	 * @since 2016/04/04
	 */
	public ClassNameSymbol asClassName()
	{
		// Get reference
		Reference<ClassNameSymbol> ref = _clname;
		ClassNameSymbol rv;
		
		// Cache?
		if (ref == null || null == (rv = ref.get()))
		{
			// Map to primitive if one
			String ss;
			switch ((ss = toString()))
			{
				case "B": rv = ClassNameSymbol.BYTE; break;
				case "S": rv = ClassNameSymbol.SHORT; break;
				case "C": rv = ClassNameSymbol.CHARACTER; break;
				case "I": rv = ClassNameSymbol.INTEGER; break;
				case "J": rv = ClassNameSymbol.LONG; break;
				case "F": rv = ClassNameSymbol.FLOAT; break;
				case "D": rv = ClassNameSymbol.DOUBLE; break;
				case "Z": rv = ClassNameSymbol.BOOLEAN; break;
				
					// General
				default:
					// Is a class?
					if (ss.startsWith("L"))
						rv = ClassNameSymbol.of(ss.substring(1,
							ss.length() - 1));
					
					// An array?
					else
						rv = ClassNameSymbol.of(ss);
					break;
			}
			
			// Cache it
			_clname = new WeakReference<>(rv);
		}
		
		// Return it
		return rv;
	}
	
	/**
	 * This returns the base type that the field is.
	 *
	 * @return The base type of the field or {@code null} if it is an array.
	 * @throws IllegalSymbolException If the symbol is not valid.
	 * @since 2016/03/19
	 */
	public FieldBaseTypeSymbol baseType()
		throws IllegalSymbolException
	{
		// Not valid if an array
		if (dimensions != 0)
			return null;
		
		// Get reference
		Reference<FieldBaseTypeSymbol> ref = _basetype;
		FieldBaseTypeSymbol rv = null;
		
		// In reference?
		if (ref != null)
			rv = ref.get();
		
		// Needs caching?
		if (rv == null)
		{
			// Get first character, because this determines what this is
			char fc = charAt(0);
			int n = length();
			
			// Is a class name?
			if (fc == 'L')
			{
				// {@squirreljme.error AL09 Expected a semi-colon at the end
				// of the class name. (This symbol)}
				if (';' != charAt(n - 1))
					throw new IllegalSymbolException(String.format("AL09 %s",
						this));
				
				// Get it
				rv = BinaryNameSymbol.of(toString().substring(1, n - 1));
			}
			
			// Primitive type
			else
			{
				// {@squirreljme.error AL08 Primitive types must only contain
				// a single character. (This symbol)}
				if (n != 1)
					throw new IllegalSymbolException(String.format("AL08 %s",
						this));
				
				// Find matching primitive
				rv = PrimitiveSymbol.byCode(fc);
				
				// {@squirreljme.error AL06 Unknown primitive type identifier.
				// (This symbol)}
				if (rv == null)
					throw new IllegalSymbolException(String.format("AL06 %s",
						this));
			}
			
			// Cache it
			_basetype = new WeakReference<>(rv);
		}
		
		// Return it
		return rv;
	}
	
	/**
	 * Returns the binary name of the field.
	 *
	 * @return The binary name of the field or {@code null} if it does not
	 * represent a class type.
	 * @since 2016/03/19
	 */
	public BinaryNameSymbol binaryName()
	{
		// If the base type is a binary name then use it
		FieldBaseTypeSymbol rv = baseType();
		if (rv instanceof BinaryNameSymbol)
			return (BinaryNameSymbol)rv;
		return null;
	}
	
	/**
	 * Returns the component of the array.
	 *
	 * @return The component type of the array or {@code null} if not an
	 * array.
	 * @throws IllegalSymbolException If the symbol is not valid.
	 * @since 2016/03/14
	 */
	public FieldSymbol componentType()
		throws IllegalSymbolException
	{
		// Not valid if not an array
		if (dimensions != 0)
			return null;
		
		// Get reference
		Reference<FieldSymbol> ref = _componenttype;
		FieldSymbol rv = null;
		
		// In reference?
		if (ref != null)
			rv = ref.get();
		
		// Need caching?
		if (rv == null)	
			_componenttype = new WeakReference<>((rv = new FieldSymbol(
				toString().substring(dimensions))));
		
		// Return it
		return rv;
	}
	
	/**
	 * Is this an array type?
	 *
	 * @return {@code true} if it is an array.
	 * @since 2016/03/23
	 */
	public boolean isArray()
	{
		return dimensions != 0;
	}
	
	/**
	 * Is this a primitive type?
	 *
	 * @return {@code true} if it is a primitive type.
	 * @since 2016/06/20
	 */
	public boolean isPrimitive()
	{
		return primitiveType() != null;
	}
	
	/**
	 * Returns {@code true} if this is a reference type.
	 *
	 * @return {@code true} if a reference type, otherwise {@code false} if
	 * a primitive value.
	 * @since 2016/04/16
	 */
	public boolean isReference()
	{
		return isArray() || primitiveType() == null;
	}
	
	/**
	 * Returns the primitive type of the field.
	 *
	 * @return The primitive type or {@code null} if not one.
	 * @since 2016/03/19
	 */
	public PrimitiveSymbol primitiveType()
	{
		// If the base type is a primitive type then use it
		FieldBaseTypeSymbol rv = baseType();
		if (rv instanceof PrimitiveSymbol)
			return (PrimitiveSymbol)rv;
		return null;
	}
}

