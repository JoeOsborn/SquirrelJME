// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.runtime.midlet.depends;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;
import net.multiphasicapps.squirreljme.runtime.midlet.id.SuiteName;
import net.multiphasicapps.squirreljme.runtime.midlet.id.SuiteVendor;
import net.multiphasicapps.squirreljme.runtime.midlet.id.SuiteVersion;
import net.multiphasicapps.squirreljme.runtime.midlet.InvalidSuiteException;

/**
 * This represents a standard which is provided by an API.
 *
 * @since 2017/12/05
 */
public final class Standard
	implements Comparable<Standard>
{
	/** The standard name. */
	protected final SuiteName name;
	
	/** The vendor of the standard. */
	protected final SuiteVendor vendor;
	
	/** The version of the standard. */
	protected final SuiteVersion version;
	
	/** String representation. */
	private volatile Reference<String> _string;
	
	/**
	 * Initializes the standard using the given fields.
	 *
	 * @param __name The name of the standard.
	 * @param __vend The vendor of the standard.
	 * @param __vers The version of the standard.
	 * @throws NullPointerException If no name was specified.
	 * @since 2017/12/30
	 */
	public Standard(SuiteName __name, SuiteVendor __vend, SuiteVersion __vers)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException("NARG");
		
		this.name = __name;
		this.vendor = __vend;
		this.version = __vers;
	}
	
	/**
	 * Decodes the standard from the specified string.
	 *
	 * @param __s The string to decode the standard from.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/30
	 */
	public Standard(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/30
	 */
	@Override
	public int compareTo(Standard __o)
	{
		int rv = this.name.compareTo(__o.name);
		if (rv != 0)
			return rv;
		
		SuiteVendor aven = this.vendor,
			bven = __o.vendor;
		if ((aven == null) != (bven == null))
			return (aven == null ? -1 : 1);
		else if (aven != null)
		{
			rv = aven.compareTo(bven);
			if (rv != 0)
				return rv;
		}
			
		SuiteVersion aver = this.version,
			bver = __o.version;
		if ((aver == null) != (bver == null))
			return (aver == null ? -1 : 1);
		else if (aver != null)
			return aver.compareTo(bver);
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/30
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof Standard))
			return false;
		
		Standard o = (Standard)__o;
		return this.name.equals(o.name) &&
			Objects.equals(this.vendor, o.vendor) &&
			Objects.equals(this.version, o.version);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/30
	 */
	@Override
	public int hashCode()
	{
		return this.name.hashCode() ^
			Objects.hashCode(this.vendor) ^
			Objects.hashCode(this.version);
	}
	
	/**
	 * Returns the name of the API which is defined.
	 *
	 * @return The defined API name.
	 * @since 2017/12/30
	 */
	public SuiteName name()
	{
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/30
	 */
	@Override
	public String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = this.name + ";" +
				Objects.toString(this.vendor, "") + ";" +
				Objects.toString(this.version, "")));
		
		return rv;
	}
	
	/**
	 * Returns the vendor of the standard.
	 *
	 * @return The vendor, may be {@code null} if no vendor was specified.
	 * @since 2017/12/30
	 */
	public SuiteVendor vendor()
	{
		return this.vendor;
	}
	
	/**
	 * Returns the version of the standard.
	 *
	 * @return The version, may be {@code null} if no version was specified.
	 * @since 2017/12/30
	 */
	public SuiteVersion version()
	{
		return this.version;
	}
}

