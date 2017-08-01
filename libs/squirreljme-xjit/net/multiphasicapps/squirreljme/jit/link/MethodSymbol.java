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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * This symbol describes the arguments and the return value which a method
 * consumes and provides.
 *
 * @since 2016/03/15
 */
public final class MethodSymbol
	extends MemberTypeSymbol
{
	/** Offsets to arguments (last is return value). */
	private final int[] _offsets;
	
	/** Length of arguments (last is return value). */
	private final int[] _lengths;
	
	/** Cache for field values (last is return value). */
	private final Reference<FieldSymbol>[] _symbols;
	
	/** Argument list cache. */
	private volatile Reference<List<FieldSymbol>> _arglist;
	
	/**
	 * Initializes the method symbol.
	 *
	 * @param __s The method descriptor.
	 * @throws IllegalSymbolException If this is not a valid method symbol.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/03/15
	 */
	public MethodSymbol(String __s)
		throws IllegalSymbolException, NullPointerException
	{
		super(__s);
		
		// {@squirreljme.error AL0c Method symbols must start with an opening
		// parenthesis. (This symbol)}
		if (charAt(0) != '(')
			throw new IllegalSymbolException(String.format("AL0c %s", this));
		
		// Target offset and length pairings
		List<Long> pairs = new ArrayList<>();
		
		// Go through characters and determine offsets
		int i, n = length();
		boolean latched = false;
		for (i = 1; i < n; i++)
		{
			// Get character here
			char c = charAt(i);
			
			// End?
			if (c == ')')
			{
				latched = true;
				break;
			}
			
			// Potentially starts as an array.
			int startdx = i;
			while (i < n && charAt(i) == '[')
				i++;
			
			// Illegal if at the end, keep unlatched
			if (i >= n)
				break;
			
			// Get character here
			c = charAt(i);
			
			// Class name? Stop at ;
			if (c == 'L')
			{
				while (i < n && charAt(i) != ';')
					i++;
			
				// Did not find a ; potentially
				if (i >= n)
					break;
			}
			
			// Add pair
			pairs.add(((long)startdx) | (((long)((i + 1) - startdx)) << 32L));
		}
		
		// {@squirreljme.error AL0b Method symbol does not end its arguments
		// with a closing parenthesis. (This symbol)}
		if (!latched)
			throw new IllegalSymbolException(String.format("AL0b %s", this));
		
		// Skip end
		// {@squirreljme.error AL0a The method symbol does not specify a return
		// type. (This symbol)}
		if ((++i) >= n)
			throw new IllegalSymbolException(String.format("AL0a %s", this));
		
		// Add final pair
		pairs.add(((long)i) | (((long)(n - i) << 32L)));
		
		// Build pair sets
		int count = pairs.size();
		int[] xoffs, xlens;
		_offsets = xoffs = new int[count];
		_lengths = xlens = new int[count];
		_symbols = __makeFieldRefArray(count);
		
		// Build offset and length array
		for (int j = 0; j < count; j++)
		{
			// Get data
			long vx = pairs.get(j);
			
			// Build
			xoffs[j] = (int)(vx & 0xFFFFFFFFL);
			xlens[j] = (int)((vx >>> 32L) & 0xFFFFFFFFL);
		}
		
		// Cache all symbols to check them
		for (int j = 0; j < count; j++)
			__get(j);
	}
	
	/**
	 * Creates a method symbol using the specified return value and arguments
	 * to the method.
	 *
	 * @param __rv The return value, if {@code null} then {@code void} is used.
	 * @param __args The arguments to the method.
	 * @return The method symbol using the return value specified and its
	 * arguments.
	 * @throws NullPointerException If an argument contains a null field.
	 * @since 2016/06/21
	 */
	public MethodSymbol(FieldSymbol __rv, FieldSymbol... __args)
		throws NullPointerException
	{
		this(__stringize(__rv, __args));
	}
	
	/**
	 * Returns the number of arguments this method symbol contains.
	 *
	 * @return The argument count.
	 * @since 2016/03/20
	 */
	public int argumentCount()
	{
		return _offsets.length - 1;
	}
	
	/**
	 * Returns the list of arguments.
	 *
	 * @return The list of arguments.
	 * @since 2017/04/16
	 */
	public List<FieldSymbol> arguments()
	{
		Reference<List<FieldSymbol>> ref = this._arglist;
		List<FieldSymbol> rv;
		
		// Cache?
		if (ref == null || null == (rv = ref.get()))
			this._arglist = new WeakReference<>((rv = new __ArgList__()));
		
		return rv;
	}
	
	/**
	 * Obtains the field symbol which represents the given argument index.
	 *
	 * @param __i The index to get the argument for.
	 * @return The argument at the given index.
	 * @throws IndexOutOfBoundsException If the index is not within the number
	 * of available arguments.
	 * @since 2016/03/20
	 */
	public FieldSymbol get(int __i)
		throws IndexOutOfBoundsException
	{
		// Exceeds bounds?
		int n = _offsets.length;
		if (__i >= (n - 1))
			throw new IndexOutOfBoundsException(String.format("IOOB %d", __i));
		
		// Get otherwise
		return __get(__i);
	}
	
	/**
	 * Returns the return value of the method.
	 *
	 * @return The method return value or {@code null} if it is {@code void}.
	 * @since 2016/03/20
	 */
	public FieldSymbol returnValue()
	{
		return __get(_offsets.length - 1);
	}
	
	/**
	 * Internal obtaining of field symbol.
	 *
	 * @param __i The index of the symbol.
	 * @return The field symbol for the given argument or return value, if
	 * the return value is void then {@code null} is returned.
	 * @throws IllegalSymbolException If the symbol here is not valid.
	 */
	private FieldSymbol __get(int __i)
		throws IllegalSymbolException, IndexOutOfBoundsException
	{
		// Get array
		Reference<FieldSymbol>[] arr = _symbols;
		
		// Exceeds size?
		int n = arr.length;
		if (__i < 0 || __i >= n)
			throw new IndexOutOfBoundsException(String.format("IOOB %d", __i));
		
		// Read offset and length
		int off = _offsets[__i];
		int len = _lengths[__i];
		
		// If V, it is void
		if (charAt(off) == 'V')
		{
			// {@squirreljme.error AL0d The arguments in a method symbol
			// cannot contain void. (This symbol)}
			if (__i != (n - 1))
				throw new IllegalSymbolException(String.format("AL0d %s",
					this));
			
			// Use null
			return null;
		}
		
		// Lock on the cache
		synchronized (arr)
		{
			// Get reference	
			Reference<FieldSymbol> ref = arr[__i];
			FieldSymbol rv;
			
			// Invalidated?
			if (ref == null || null == (rv = ref.get()))
				arr[__i] = new WeakReference<>((rv = FieldSymbol.of(
					toString().substring(off, off + len))));
			
			// Return it
			return rv;
		}
	}
	
	/**
	 * Used to prevent {@link SuppressWarnings} where it is not needed.
	 *
	 * @param __n The number of elements in the array.
	 * @return The generic array.
	 * @since 2016/03/20
	 */
	@SuppressWarnings({"unchecked"})
	private static Reference<FieldSymbol>[] __makeFieldRefArray(int __n)
	{
		return ((Reference<FieldSymbol>[])((Object)new Reference[__n]));
	}
	
	/**
	 * Creates a method symbol using the specified return value and arguments
	 * to the method.
	 *
	 * @param __rv The return value, if {@code null} then {@code void} is used.
	 * @param __args The arguments to the method.
	 * @return The method symbol using the return value specified and its
	 * arguments.
	 * @throws NullPointerException If an argument contains a null field.
	 * @since 2016/06/21
	 */
	private static String __stringize(FieldSymbol __rv, FieldSymbol... __args)
		throws NullPointerException
	{
		// Defensive copy
		__args = (__args != null ? __args.clone() : new FieldSymbol[0]);
		
		// Build
		StringBuilder sb = new StringBuilder("(");
		for (FieldSymbol fs : __args)
		{
			// Check
			if (fs == null)
				throw new NullPointerException("NARG");
			
			// Append it
			sb.append(fs);
		}
		
		// Return value
		sb.append(')');
		sb.append((__rv != null ? __rv : "V"));
		
		// Build it
		return sb.toString();
	}
	
	/**
	 * This is used to access the argument list as an actual list.
	 *
	 * @since 2017/04/16
	 */
	private final class __ArgList__
		extends AbstractList<FieldSymbol>
		implements RandomAccess
	{
		/**
		 * Does nothing.
		 *
		 * @since 2017/04/16
		 */
		private __ArgList__()
		{
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/04/16
		 */
		@Override
		public FieldSymbol get(int __i)
		{
			return MethodSymbol.this.get(__i);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/04/16
		 */
		@Override
		public int size()
		{
			return MethodSymbol.this.argumentCount();
		}
	}
}

