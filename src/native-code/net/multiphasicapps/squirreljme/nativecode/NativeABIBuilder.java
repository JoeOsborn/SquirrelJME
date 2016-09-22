// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.nativecode;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.multiphasicapps.squirreljme.nativecode.base.NativeEndianess;
import net.multiphasicapps.squirreljme.nativecode.base.NativeFloatType;

/**
 * This class is used to generate instances of {@link NativeABI} which is
 * used by the register allocator and the generic JIT compiler to determine
 * how other methods are called.
 *
 * @since 2016/09/01
 */
public final class NativeABIBuilder
{
	/** Lock. */
	protected final Object lock =
		new Object();
	
	/** Integer registers. */
	final Map<NativeRegister, NativeRegisterIntegerType> _intregs =
		new LinkedHashMap<>();
	
	/** Floating point registers. */
	final Map<NativeRegister, NativeRegisterFloatType> _floatregs =
		new LinkedHashMap<>();
	
	/** Saved registers. */
	final Set<NativeRegister> _saved =
		new LinkedHashSet<>();
	
	/** Temporary registers. */
	final Set<NativeRegister> _temps =
		new LinkedHashSet<>();
	
	/** Arguments. */
	final Set<NativeRegister> _args =
		new LinkedHashSet<>();
	
	/** Results. */
	final Set<NativeRegister> _result =
		new LinkedHashSet<>();
	
	/** The current stack register. */
	volatile NativeRegister _stack;
	
	/** The stack direction. */
	volatile NativeStackDirection _stackdir;
	
	/** The stack alignment. */
	volatile int _stackalign =
		-1;
	
	/** The pointer size. */
	volatile int _pointersize =
		-1;
	
	/** The stack value alignment. */
	volatile int _stackvaluealign =
		1;
	
	/** The endianess of the CPU. */
	volatile NativeEndianess _endianess;
	
	/**
	 * Adds an argument register.
	 *
	 * @param __r The register that is part of the input arguments for a
	 * method.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/01
	 */
	public final void addArgument(NativeRegister __r)
		throws NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			this._args.add(__r);
		}
	}
	
	/**
	 * Adds a register to the register list.
	 *
	 * @param __r The register to add.
	 * @param __t The type of value the register is.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/02
	 */
	public final void addRegister(NativeRegister __r, NativeRegisterType __t)
		throws NullPointerException
	{
		// Check
		if (__r == null || __t == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			// Integer?
			if (__t instanceof NativeRegisterIntegerType)
				this._intregs.put(__r, (NativeRegisterIntegerType)__t);
			
			// Floating point?
			if (__t instanceof NativeRegisterFloatType)
				this._floatregs.put(__r, (NativeRegisterFloatType)__t);
		}
	}
	
	/**
	 * Adds a result register.
	 *
	 * @param __r The register that is used for the return value.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/01
	 */
	public final void addResult(NativeRegister __r)
		throws NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			this._result.add(__r);
		}
	}
	
	/**
	 * Adds a callee saved register.
	 *
	 * @param __r The register that is callee saved.
	 * @throws NativeCodeException If the register is temporary or is the stack
	 * register.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/01
	 */
	public final void addSaved(NativeRegister __r)
		throws NativeCodeException, NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			// {@squirreljme.error AR1b Cannot add the specified register
			// because it is the stack register or is temporary. (The register
			// being added)}
			if (this._temps.contains(__r) || this._stack.equals(__r))
				throw new NativeCodeException(String.format("AR1b %s", __r));
			
			this._saved.add(__r);
		}
	}
	
	/**
	 * Adds a caller saved register.
	 *
	 * @param __r The register that is caller saved.
	 * @throws NativeCodeException If the register is saved or is the stack register.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/01
	 */
	public final void addTemporary(NativeRegister __r)
		throws NativeCodeException, NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			// {@squirreljme.error AR1c Cannot add the specified register
			// because it is the stack register or is saved. (The register
			// being added)}
			if (this._saved.contains(__r) || this._stack.equals(__r))
				throw new NativeCodeException(String.format("AR1c %s", __r));
			
			this._temps.add(__r);
		}
	}
	
	/**
	 * Builds the given ABI set.
	 *
	 * @return The generic ABI set.
	 * @throws NativeCodeException If a group contains no registers, the stack
	 * register was not set, or the direction register was not set.
	 * @since 2016/09/01
	 */
	public final NativeABI build()
		throws NativeCodeException
	{
		// Lock
		synchronized (this.lock)
		{
			return new NativeABI(this);
		}
	}
	
	/**
	 * Sets the used endianess of the CPU.
	 *
	 * @param __e The endianess to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/22
	 */
	public final void endianess(NativeEndianess __e)
		throws NullPointerException
	{
		// Check
		if (__e == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			this._endianess = __e;
		}
	}
	
	/**
	 * Sets the pointer size.
	 *
	 * @param __b The number of bits used for pointers.
	 * @throws NativeCodeException If the number of bits is zero or negative;
	 * is not a power of two; or is not a multiple of eight.
	 * @since 2016/09/08
	 */
	public final void pointerSize(int __b)
		throws NativeCodeException
	{
		// {@squirreljme.error AR1k An attempt was made to set the pointer
		// size used in the ABI to a value that is zero, negative, or not a
		// multiple of eight. (The specified size)}
		if (__b <= 0 || Integer.bitCount(__b) != 1 || (__b & 7) != 0)
			throw new NativeCodeException(String.format("AR1k %d", __b));
		
		// Lock
		synchronized (this.lock)
		{
			this._pointersize = __b;
		}
	}
	
	/**
	 * Sets the stack register.
	 *
	 * @param __r The register to use for the stack.
	 * @throws NativeCodeException If the register is temporary or saved.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/01
	 */
	public final void stack(NativeRegister __r)
		throws NativeCodeException, NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			// {@squirreljme.error AR0p Cannot use the specified register as
			// the stack register because it is saved and/or temporary. (The
			// register to be used as the stack register)}
			if (this._saved.contains(__r) || this._temps.contains(__r))
				throw new NativeCodeException(String.format("AR0p %s", __r));
			
			this._stack = __r;
		}
	}
	
	/**
	 * Sets the requires stack alignment needed for method calls.
	 *
	 * @param __i The number of bytes to align to.
	 * @throws NativeCodeException If the alignment is zero or negative.
	 * @since 2016/09/01
	 */
	public final void stackAlignment(int __i)
		throws NativeCodeException
	{
		// {@squirreljme.error AR0u The stack alignment is zero or negative.
		// (The alignment)}
		if (__i <= 0)
			throw new NativeCodeException(String.format("AR0u %d", __i));
		
		// Lock
		synchronized (this.lock)
		{
			this._stackalign = __i;
		}
	}
	
	/**
	 * Sets the direction of the stack.
	 *
	 * @param __d The stack direction.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/01
	 */
	public final void stackDirection(NativeStackDirection __d)
		throws NullPointerException
	{
		// Check
		if (__d == null)
			throw new NullPointerException("NARG");
		
		// Lock
		synchronized (this.lock)
		{
			this._stackdir = __d;
		}
	}
	
	/**
	 * Sets the alignment of values on the stack.
	 *
	 * @param __a The alignment to use for values.
	 * @throws NativeCodeException If the alignment requested is zero or
	 * negative.
	 * @since 2016/09/21
	 */
	public final void stackValueAlignment(int __a)
		throws NativeCodeException
	{
		// {@squirreljme.error AR07 Cannot set the stack value alignment to
		// a zero or negative value. (The alignment count)}
		if (__a <= 0)
			throw new NativeCodeException(String.format("AR07 %s", __a));
		
		// Lock
		synchronized (this.lock)
		{
			this._stackvaluealign = __a;
		}
	}
	
	/**
	 * Returns the integer type that matches the bit size of the given bits.
	 *
	 * @param __t The number of bits the CPU uses
	 * @return The integer type.
	 * @throws NativeCodeException If the type was not known.
	 * @since 2016/09/02
	 */
	public static NativeRegisterIntegerType intRegisterType(int __t)
		throws NativeCodeException
	{
		// Depends on the bits
		switch (__t)
		{
			case 8: return NativeRegisterIntegerType.BYTE;
			case 16: return NativeRegisterIntegerType.SHORT;
			case 32: return NativeRegisterIntegerType.INTEGER;
			case 64: return NativeRegisterIntegerType.LONG;
			
				// {@squirreljme.error AR1f Could not get the integer register
				// type from the specified bit count. (The bit count)}
			default:
				throw new NativeCodeException(String.format("AR1f %d", __t));
		}
	}
	
	/**
	 * Returns the floating point type that matches the hardware floating
	 * point type.
	 *
	 * @param __t The floating point type to use.
	 * @return The floating point type.
	 * @throws NativeCodeException If the type could not be determined.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/02
	 */
	public static NativeRegisterFloatType floatRegisterType(
		NativeFloatType __t)
		throws NativeCodeException, NullPointerException
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Depends on the float type
		switch (__t)
		{
			case HARD32: return NativeRegisterFloatType.FLOAT;
			case HARD64: return NativeRegisterFloatType.DOUBLE;
			
				// {@squirreljme.error AR1g Could not get the float register
				// type from the specified floating point type. (The floating
				// point type)}
			default:
				throw new NativeCodeException(String.format("AR1g %s", __t));
		}
	}
}

