// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.jdwp.JDWPState;
import cc.squirreljme.jdwp.views.JDWPViewFrame;
import java.lang.ref.Reference;

/**
 * Viewer for frames.
 *
 * @since 2021/04/11
 */
public class DebugViewFrame
	implements JDWPViewFrame
{
	/** The state of the debugger. */
	protected final Reference<JDWPState> state;
	
	/**
	 * Initializes the frame viewer.
	 * 
	 * @param __state The state.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/11
	 */
	public DebugViewFrame(Reference<JDWPState> __state)
	{
		if (__state == null)
			throw new NullPointerException("NARG");
		
		this.state = __state;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/11
	 */
	@Override
	public Object atClass(Object __which)
	{
		return ((SpringThread.Frame)__which).springClass;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/11
	 */
	@Override
	public int atCodeIndex(Object __which)
	{
		return ((SpringThread.Frame)__which).pcIndex();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/11
	 */
	@Override
	public int atMethodIndex(Object __which)
	{
		SpringThread.Frame which = (SpringThread.Frame)__which;
		return which.method().methodIndex;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/11
	 */
	@Override
	public boolean isValid(Object __which)
	{
		return (__which instanceof SpringThread.Frame);
	}
}
