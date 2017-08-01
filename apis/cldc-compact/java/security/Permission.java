// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.security;

public abstract class Permission
{
	public Permission(String __a)
	{
		super();
		throw new todo.TODO();
	}
	
	@Override
	public abstract boolean equals(Object __a);
	
	public abstract String getActions();
	
	@Override
	public abstract int hashCode();
	
	public abstract boolean implies(Permission __a);
	
	public final String getName()
	{
		throw new todo.TODO();
	}
	
	public PermissionCollection newPermissionCollection()
	{
		throw new todo.TODO();
	}
	
	@Override
	public String toString()
	{
		throw new todo.TODO();
	}
}

