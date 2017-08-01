// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.build.projects;

import java.nio.file.Path;

/**
 * This class represents the name of a package, which is case insensitive.
 *
 * @since 2016/06/15
 */
public final class ProjectName
	implements Comparable<ProjectName>
{
	/** The name of the package. */
	protected final String name;
	
	/**
	 * Initializes the package name.
	 *
	 * @param __s The name of the package.
	 * @throws InvalidProjectException If the specified name is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/06/15
	 */
	public ProjectName(String __s)
		throws InvalidProjectException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Make more correct name
		StringBuilder sb = new StringBuilder();
		
		// Must consist of valid characters
		int n = __s.length();
		for (int i = 0; i < n; i++)
		{
			char c = __s.charAt(i);
			
			// {@squirreljme.error AT03 The specified string is not a valid
			// name for a package because it contains illegal characters.
			// (The illegal package name)}
			boolean upper = (c >= 'A' && c <= 'Z');
			if (!((c >= 'a' && c <= 'z') || upper ||
				(c >= '0' && c <= '9') || c == '-'))
				throw new InvalidProjectException(String.format("AT03 %s",
					__s));
			
			// Lower?
			if (upper)
				sb.append(((char)(c - 'A') + 'a'));
			
			// Keep
			else
				sb.append(c);
		}
		
		// Set
		this.name = sb.toString();
	}
	
	/**
	 * {@inheritDoc]}
	 * @since 2016/06/15
	 */
	@Override
	public int compareTo(ProjectName __pn)
	{
		return __compareTo(__pn);
	}
	
	/**
	 * {@inheritDoc]}
	 * @since 2016/06/15
	 */
	@Override
	public boolean equals(Object __o)
	{
		// If another package, use normal comparison
		if (__o instanceof ProjectName)
			return 0 == __compareTo((ProjectName)__o);
		
		// If a string, compare as if it were a package name
		else if (__o instanceof String)
			return 0 == __compareTo((String)__o);
		
		// Will never match
		return false;
	}
	
	/**
	 * {@inheritDoc]}
	 * @since 2016/06/15
	 */
	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
	
	/**
	 * {@inheritDoc]}
	 * @since 2016/06/15
	 */
	@Override
	public String toString()
	{
		return this.name;
	}
	
	/**
	 * Compares this package name against another.
	 *
	 * @param __pn The name to compare against.
	 * @return The result of the comparison.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/06/15
	 */
	private final int __compareTo(ProjectName __pn)
		throws NullPointerException
	{
		// Check
		if (__pn == null)
			throw new NullPointerException("NARG");
		
		// Compare it
		return __compareTo(__pn.name);
	}
	
	/**
	 * Compares the name of this package against another.
	 *
	 * @param __pn The other name to compare against.
	 * @return The result of the comparison.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/06/15
	 */
	private final int __compareTo(String __pn)
		throws NullPointerException
	{
		// Check
		if (__pn == null)
			throw new NullPointerException("NARG");
		
		// Get both
		String a = this.name,
			b = __pn;
		
		// Get lengths
		int na = a.length(),
			nb = b.length();
		int lim = Math.min(na, nb);
		
		// Compare
		for (int i = 0; i < lim; i++)
		{
			// Get
			char ca = a.charAt(i),
				cb = b.charAt(i);
			
			// Can lowercase?
			if (ca >= 'A' && ca <= 'Z')
				ca = (char)((ca - 'A') + 'a');
			if (cb >= 'A' && cb <= 'Z')
				cb = (char)((cb - 'A') + 'a');
			
			// Lower
			if (ca < cb)
				return -1;
			
			// Higher?
			else if (ca > cb)
				return 1;
		}
		
		// Shorter?
		if (na < nb)
			return -1;
		
		// Longer?
		else if (na > nb)
			return 1;
		
		// Strings are equal
		return 0;
	}
}

