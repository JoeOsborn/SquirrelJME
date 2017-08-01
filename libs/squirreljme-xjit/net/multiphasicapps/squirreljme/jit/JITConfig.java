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

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import net.multiphasicapps.squirreljme.jit.link.FieldSymbol;
import net.multiphasicapps.squirreljme.jit.link.MethodSymbol;
import net.multiphasicapps.squirreljme.jit.java.ClassRecompiler;
import net.multiphasicapps.squirreljme.jit.link.MethodFlags;

/**
 * This is used to access the configuration which is needed by the JIT during
 * the JIT compilation set.
 *
 * @param <C> The configuration class.
 * @since 2017/02/02
 */
public abstract class JITConfig
{
	/** Mapping of configuration values. */
	private final Map<String, String> _values;
	
	/** Endianess. */
	private volatile Reference<Endian> _endian;
	
	/**
	 * Initializes the JIT configuration.
	 *
	 * @param __kvp Key/value pairs to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/02
	 */
	public JITConfig(String... __kvp)
		throws NullPointerException
	{
		this(new __KVPMap__(__kvp));
	}
	
	/**
	 * Initializes the JIT configuration.
	 *
	 * @param __kvp Key/value pairs to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/02
	 */
	public JITConfig(Map<String, String> __kvp)
		throws NullPointerException
	{
		// Check
		if (__kvp == null)
			throw new NullPointerException("NARG");
		
		// Copy
		Map<String, String> to = new HashMap<>();
		for (Map.Entry<String, String> e : __kvp.entrySet())
			to.put(__lower(e.getKey()), __lower(e.getValue()));
		this._values = to;
	}
	
	/**
	 * Returns the allocations for arguments for a call to the specified method
	 * with the given flags. Return values are not considered because their
	 * allocations are universally determined.
	 *
	 * The resulting array will be the same length as the input array. Any
	 * values which are {@code null} in the input array will be translated to
	 * {@code null} in the output array. The {@code null}s are specified in the
	 * JIT to indicate that no variable is assigned to that position. An
	 * equivalent call with no {@code null}s should have the same result as
	 * one with inplaced {@code null}s. Trailing {@code null}s should have no
	 * effect.
	 *
	 * @param __t The types which are used for the method arguments.
	 * @return The allocations for the given method call.
	 * @throws JITException If they are not valid.
	 * @throws NullPointerException If no array was specified.
	 * @since 2017/04/16
	 */
	public abstract TypedAllocation[] entryAllocations(NativeType... __t)
		throws JITException, NullPointerException;
	
	/**
	 * Obtains the register dictionary which is used to provide registers that
	 * are available for usage.
	 *
	 * @return The register dictionary.
	 * @since 2017/04/01
	 */
	public abstract RegisterDictionary registerDictionary();
	
	/**
	 * Returns the class which is used to serialize and de-serialize the
	 * JIT.
	 *
	 * @return The serializer for this JIT configuration.
	 * @since 2017/02/01
	 */
	public abstract JITConfigSerializer serializer();
	
	/**
	 * Returns the number of bits which are used by the given CPU.
	 *
	 * @return The CPU bits.
	 * @throws JITException If the bits are not set or they are invalid.
	 * @since 2017/02/13
	 */
	public int bits()
		throws JITException
	{
		String v = internalValue("generic.bits");
		
		// {@squirreljme.error AQ01 CPU bits not specified.}
		if (v == null)
			throw new IllegalStateException("AQ01");
		
		// Convert
		try
		{
			int rv = Integer.parseInt(v);
			
			// {@squirreljme.error AQ02 CPU bits is zero or negative.}
			if (rv <= 0)
				throw new JITException("AQ02");
			
			// {@squirreljme.error AQ03 The CPU bits is not a multiple of 8.}
			if ((rv % 8) != 0)
				throw new JITException("AQ03");
			
			return rv;
		}
		
		// {@squirreljme.error AQ04 Specified CPU bits not a number.}
		catch (NumberFormatException e)
		{
			throw new JITException("AQ04", e);
		}
	}
	
	/**
	 * Returns the data type that is used for the pointer.
	 *
	 * @return The pointer data type.
	 * @since 2017/03/2
	 */
	public final NativeType bitsNativeType()
	{
		switch (bits())
		{
			case 8:		return NativeType.BYTE;
			case 16:	return NativeType.SHORT;
			case 32:	return NativeType.INTEGER;
			case 64:	return NativeType.LONG;
			
				// Unknown
			default:
				throw new RuntimeException("OOPS");
		}
	}
	
	/**
	 * Returns the target endianess.
	 *
	 * @return The target endianess.
	 * @throws JITException If it is not set.
	 * @since 2017/03/18
	 */
	public Endian endianess()
		throws JITException
	{
		Reference<Endian> ref = this._endian;
		Endian rv;
		
		if (ref == null || null == (rv = ref.get()))
		{
			// {@squirreljme.error AQ05 The endianess has not been specified.}
			String val = internalValue("generic.endianess");
			if (val == null)
				throw new JITException("AQ05");
			
			// Depends
			switch (val)
			{
				case "big": rv = Endian.BIG; break;
				case "little": rv = Endian.LITTLE; break;
				
					// {@squirreljme.error AQ06 Unknown endianess specified.
					// (The endianess)}
				default:
					throw new JITException(String.format("AQ06 %s", val));
			}
			
			// Cache
			this._endian = new WeakReference<>(rv);
		}
		
		return rv;
	}
	
	/**
	 * Obtains an internally set value.
	 *
	 * @param __s The key to get.
	 * @return The value of the given key or {@code null} if no value was set.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/13
	 */
	protected final String internalValue(String __s)
		throws NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		return this._values.get(__s);
	}
	
	/**
	 * Creates an instance of the JIT using this configuration.
	 *
	 * @param __is The input class file.
	 * @return The JIT translator.
	 * @since 2017/04/02
	 */
	public final ClassRecompiler jit(InputStream __is)
		throws NullPointerException
	{
		// Check
		if (__is == null)
			throw new NullPointerException("NARG");
		
		// Create
		return new ClassRecompiler(new DataInputStream(__is), this);
	}
	
	/**
	 * Returns the data type that is used for pointers.
	 *
	 * @return The data type for pointers.
	 * @since 2017/02/19
	 */
	public NativeType pointerNativeType()
	{
		switch (bits())
		{
			case 8:		return NativeType.BYTE;
			case 16:	return NativeType.SHORT;
			case 32:	return NativeType.INTEGER;
			case 64:	return NativeType.LONG;
			
				// {@squirreljme.error AQ07 Unknown pointer data type.}
			default:
				throw new JITException("AQ07");
		}
	}
	
	/**
	 * Maps the specified stack type to the data type.
	 *
	 * @param __t The stack type to map.
	 * @return The data type for the given stack type.
	 * @throws JITException If the type could not be mapped.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/02
	 */
	public final NativeType toNativeType(JavaType __t)
		throws JITException, NullPointerException
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
		/*
		// Depends
		switch (__t)
		{
				// Get type
			case INTEGER:	return NativeType.INTEGER;
			case LONG:		return NativeType.LONG;
			case FLOAT:		return NativeType.FLOAT;
			case DOUBLE:	return NativeType.DOUBLE;
			case OBJECT:	return pointerNativeType();
			
				// {@squirreljme.error AQ08 The specified stack type cannot
				// be mapped to a data type. (The input stack type)}
			default:
				throw new JITException(String.format("AQ08 %s", __t));
		}*/
	}
	
	/**
	 * Returns the native type for the given field symbol.
	 *
	 * @param __f The field symbol to convert.
	 * @return The native type.
	 * @throws JITException If the type could not be mapped.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/16
	 */
	public final NativeType toNativeType(FieldSymbol __f)
		throws JITException, NullPointerException
	{
		// Check
		if (__f == null)
			throw new NullPointerException("NARG");
		
		// Map
		return toNativeType(JavaType.of(__f));
	}
	
	/**
	 * Lowercases the given string.
	 *
	 * @param __s The string to lowercase, may be {@code null}.
	 * @return The lowercased string or {@code null} if it was {@code null}.
	 * @since 2017/02/02
	 */
	static final String __lower(String __s)
	{
		// Do nothing with null
		if (__s == null)
			return null;
		
		// Check for capitals
		int i, n;
		for (i = 0, n = __s.length(); i < n; i++)
		{
			char c = __s.charAt(i);
			if (c >= 'A' && c <= 'Z')
				break;
		}
		
		// None
		if (i >= n)
			return __s;
		
		// Lowercase
		StringBuilder sb = new StringBuilder(n);
		if (i > 0)
			sb.append(__s.substring(0, i));
		
		// Lowercase characters
		for (; i < n; i++)
		{
			char c = __s.charAt(i);
			if (c >= 'A' && c <= 'Z')
				c = (char)('a' + (c - 'A'));
			sb.append(c);
		}
		
		// Use it
		return sb.toString();
	}
}

