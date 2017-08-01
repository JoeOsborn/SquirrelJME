// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.lcdui;

import java.lang.ref.Reference;
import javax.microedition.lcdui.Displayable;

/**
 * This is a native display which does not provide any capable displays, all
 * operations are essentially a no-operation.
 *
 * @since 2017/05/23
 */
public class NullNativeDisplay
	extends NativeDisplay
{
	/** The null head. */
	protected final NativeDisplay.Head head =
		new NullHead();

	/**
	 * Initializes the display which does nothing.
	 *
	 * @since 2017/05/24
	 */
	public NullNativeDisplay()
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/24
	 */
	@Override
	public NativeCanvas createCanvas(Reference<Displayable> __ref)
		throws NullPointerException
	{
		// Check
		if (__ref == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/25
	 */
	@Override
	public int fontPixelSize(int __sz)
	{
		// Always a fixed size
		return 8;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/25
	 */
	@Override
	public NativeFont[] fonts()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/24
	 */
	@Override
	public NativeDisplay.Head[] heads()
	{
		return new NativeDisplay.Head[]{this.head};
	}
	
	/**
	 * This is the null head which does not do anything.
	 *
	 * @since 2017/05/24
	 */
	public class NullHead
		extends NativeDisplay.Head
	{
		/** The state of the display. */
		protected volatile DisplayState _state =
			DisplayState.BACKGROUND;
		
		/**
		 * Initializes the null head.
		 *
		 * @since 2017/05/24
		 */
		protected NullHead()
		{
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/05/24
		 */
		@Override
		public int getContentHeight()
		{
			return 1;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/05/24
		 */
		@Override
		public int getMaximumHeight()
		{
			return 1;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/05/24
		 */
		@Override
		public int getMaximumWidth()
		{
			return 1;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/05/24
		 */
		@Override
		public int getContentWidth()
		{
			return 1;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/05/24
		 */
		public void setState(DisplayState __s)
			throws NullPointerException
		{
			// Check
			if (__s == null)
				throw new NullPointerException("NARG");
			
			this._state = __s;
		}
	}
}

