// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.lang;

public abstract class Number
{
	public Number()
	{
		super();
		throw new todo.TODO();
	}
	
	public abstract double doubleValue();
	
	public abstract float floatValue();
	
	public abstract int intValue();
	
	public abstract long longValue();
	
	public byte byteValue()
	{
		throw new todo.TODO();
	}
	
	public short shortValue()
	{
		throw new todo.TODO();
	}
}

