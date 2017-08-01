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

import net.multiphasicapps.squirreljme.unsafe.SquirrelJME;

public class Runtime
{
	private Runtime()
	{
		super();
		throw new todo.TODO();
	}
	
	/**
	 * Indicates that the application exits with the given code.
	 *
	 * @param __e The exit code.
	 * @since 2017/02/08
	 */
	public void exit(int __a)
	{
		SquirrelJME.exit(__a);
	}
	
	public long freeMemory()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Indicates that the application should have garbage collection be
	 * performed. It is unspecified when garbage collection occurs.
	 *
	 * @since 2017/02/08
	 */
	public void gc()
	{
		SquirrelJME.gc();
	}
	
	public long maxMemory()
	{
		throw new todo.TODO();
	}
	
	public long totalMemory()
	{
		throw new todo.TODO();
	}
	
	public static Runtime getRuntime()
	{
		throw new todo.TODO();
	}
}

