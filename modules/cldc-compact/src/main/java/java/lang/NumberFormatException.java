// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the Mozilla Public License Version 2.0.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.lang;

import cc.squirreljme.runtime.cldc.annotation.Api;

/**
 * This is thrown when the format of a number is not correct.
 *
 * @since 2018/10/12
 */
@Api
public class NumberFormatException
	extends IllegalArgumentException
{
	/**
	 * Initializes the exception with no message or cause.
	 *
	 * @since 2018/10/12
	 */
	@Api
	public NumberFormatException()
	{
	}
	
	/**
	 * Initializes the exception with the given message and no cause.
	 *
	 * @param __m The message.
	 * @since 2018/10/12
	 */
	@Api
	public NumberFormatException(String __m)
	{
		super(__m);
	}
}

