// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package com.nttdocomo.ui;

import cc.squirreljme.runtime.cldc.debug.Debugging;

public abstract class IApplication
{
	/** The last application created. */
	static volatile IApplication _lastApp;
	
	/** Application args, these are injected within. */
	static volatile String[] _appArgs;
	
	{
		synchronized (IApplication.class)
		{
			IApplication._lastApp = this;
		}
	}
	
	public abstract void start();
	
	public String[] getArgs()
	{
		synchronized (IApplication.class)
		{
			return IApplication._appArgs.clone();
		}
	}
	
	/**
	 * Returns the value of a parameter that was used to launch the
	 * application either from a web browser or another application.
	 * 
	 * @param __name The parameter name.
	 * @return The value of the parameter or {@code null}.
	 * @since 2022/10/07
	 */
	public String getParameter(String __name)
	{
		Debugging.todoNote("getParameter(%s)", __name);
		
		return null;
	}
	
	/**
	 * Returns the location of where the application was downloaded from, this
	 * may be used to access additional resources.
	 * 
	 * If the application was downloaded from
	 * {@code https://squirreljme.cc/apps/example.jar} the return value will
	 * be {@code https://squirreljme.cc/apps/}.
	 * 
	 * @return The URL where the application was downloaded.
	 * @since 2022/10/07
	 */
	public final String getSourceURL()
	{
		// Our webroot is always non-networked, so we handle and potentially
		// proxy all the various HTTP calls accordingly.
		return "squirreljme+webroot://localhost/";
	}
	
	public void resume()
	{
		throw Debugging.todo();
	}
	
	public final void terminate()
	{
		// Do nothing here as the application handler will handle our exit
		// status accordingly.
	}
	
	public static IApplication getCurrentApp()
	{
		synchronized (IApplication.class)
		{
			return IApplication._lastApp;
		}
	}
}
