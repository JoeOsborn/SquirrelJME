// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the Mozilla Public License Version 2.0.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.classfile;

/**
 * Represents a long constant value.
 *
 * @since 2018/05/21
 */
public final class ConstantValueLong
	extends ConstantValueNumber
{
	/**
	 * Initializes the constant value.
	 *
	 * @param __v The value.
	 * @since 2018/05/21
	 */
	public ConstantValueLong(long __v)
	{
		super(__v, ConstantValueType.LONG);
	}
}

