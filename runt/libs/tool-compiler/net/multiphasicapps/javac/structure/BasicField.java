// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.javac.structure;

import net.multiphasicapps.javac.token.BufferedTokenSource;
import net.multiphasicapps.javac.token.Token;
import net.multiphasicapps.javac.token.TokenSource;
import net.multiphasicapps.javac.token.TokenType;

/**
 * This represents a basic field.
 *
 * @since 2018/04/28
 */
public final class BasicField
	extends FieldStructure
{
	/**
	 * Parses a basic field that is contained within a class.
	 *
	 * @param __mods The modifiers to the field.
	 * @param __in The input tokens.
	 * @return The parsed field.
	 * @throws NullPointerException On null arguments.
	 * @throws StructureParseException If it is not a valid field.
	 * @since 2018/04/28
	 */
	public static BasicField parse(Modifiers __mods,
		BufferedTokenSource __in)
		throws NullPointerException, StructureParseException
	{
		if (__mods == null || __in == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
}

