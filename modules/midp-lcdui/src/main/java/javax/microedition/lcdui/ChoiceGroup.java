// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the Mozilla Public License Version 2.0.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.lcdui;

import cc.squirreljme.runtime.cldc.annotation.Api;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.lcdui.mle.DisplayWidget;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;
import java.util.ArrayList;
import java.util.List;

/**
 * A choice group represents a selectable group of elements which may be
 * added to a {@link Form}. The number of selected choices may be limited to
 * one or multiple choices may be available.
 *
 * @since 2017/08/20
 */
@Api
public class ChoiceGroup
	extends Item
	implements Choice
{
	/** The minimum permitted type. */
	static final int _MIN_TYPE =
		Choice.EXCLUSIVE;
	
	/** The maximum permitted type. */
	static final int _MAX_TYPE =
		Choice.POPUP;
	
	/** Entries which are available in the group. */
	private final List<__ChoiceEntry__> _entries =
		new ArrayList<>();
	
	/** The valid choice selection type. */
	private final int _type;
	
	/**
	 * Initializes an empty choice group.
	 *
	 * @param __l The label for this group.
	 * @param __ct The type of choice selection to use.
	 * @throws IllegalArgumentException If the choice type is not valid or
	 * if {@link Choice#IMPLICIT} was specified.
	 * @since 2017/08/20
	 */
	@Api
	public ChoiceGroup(String __l, int __ct)
		throws IllegalArgumentException
	{
		this(__l, __ct, new String[0], null);
	}
	
	/**
	 * Initializes an empty choice group.
	 *
	 * @param __l The label for this group.
	 * @param __ct The type of choice selection to use.
	 * @param __se The , this cannot be {@code null}
	 * @param __ie The images for each choice, this must either be {@code null}
	 * or be the exact same length as the input {@code __se}.
	 * @throws IllegalArgumentException If the choice type is not valid; 
	 * if {@link Choice#IMPLICIT} was specified; If the image array is not
	 * null and is not the same length as the string array.
	 * @throws NullPointerException If {@code __se} is {@code null} or it
	 * contains {@code null} elements; or if the string array contains null
	 * elements.
	 * @since 2017/08/20
	 */
	@Api
	public ChoiceGroup(String __l, int __ct, String[] __se, Image[] __ie)
		throws IllegalArgumentException, NullPointerException
	{
		// Check
		if (__se == null)
			throw new NullPointerException("NARG");
		
		/* {@squirreljme.error EB1b The image array does not have the same
		length as the string array.} */
		int n = __se.length;
		if (__ie != null && __ie.length != n)
			throw new IllegalArgumentException("EB1b");
		
		/* {@squirreljme.error EB1c Invalid choice type specified for a
		choice group. (The choice type)} */
		if (__ct < ChoiceGroup._MIN_TYPE || __ct > ChoiceGroup._MAX_TYPE ||
			__ct == Choice.IMPLICIT)
			throw new IllegalArgumentException(String.format("EB1c %d", __ct));
		
		// Set
		this.setLabel(__l);
		this._type = __ct;
		
		// Append all elements
		for (int i = 0; i < n; i++)
		{
			/* {@squirreljme.error EB1d A string element contains a null
			entry.} */
			String s = __se[i];
			if (s == null)
				throw new NullPointerException("EB1d");
			
			// Add it
			this.append(s, (__ie != null ? __ie[i] : null));
		}
	}
	
	/**
	 * This appends a single choice.
	 *
	 * @param __s The string to display.
	 * @param __i The image to display, may be {@code null}.
	 * @return The index the element was added at.
	 * @throws NullPointerException If no string was specified.
	 * @since 2017/08/20
	 */
	@Override
	public int append(String __s, Image __i)
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Just insert at the end
		int rv;
		this.insert((rv = this.size()), __s, __i);
		return rv;
	}
	
	@Override
	public void delete(int __dx)
	{
		throw Debugging.todo();
	}
	
	@Override
	public void deleteAll()
	{
		throw Debugging.todo();
	}
	
	@Override
	public int getFitPolicy()
	{
		throw Debugging.todo();
	}
	
	@Override
	public Font getFont(int __a)
	{
		throw Debugging.todo();
	}
	
	@Override
	public Image getImage(int __a)
	{
		throw Debugging.todo();
	}
	
	@Override
	public int getSelectedFlags(boolean[] __result)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/31
	 */
	@Override
	public int getSelectedIndex()
	{
		return __Utils__.__getSelectedIndex(this, this._type);
	}
	
	@Override
	public String getString(int __a)
	{
		throw Debugging.todo();
	}
	
	/**
	 * This inserts the specified choice at the given index.
	 *
	 * Note that the documentation erroneously specifies that an exception
	 * will be thrown if {@code __v} is in the range of {@code [0, size()-1]},
	 * this is not the case and {@code size()} is a valid index.
	 *
	 * @param __v The index to insert the choice at.
	 * @param __s The string to display.
	 * @param __i The image to display, may be {@code null}.
	 * @throws IndexOutOfBoundsException If the index exceeds the bounds.
	 * @throws NullPointerException If no string was specified.
	 * @since 2017/08/21
	 */
	@Override
	public void insert(int __v, String __s, Image __i)
		throws IndexOutOfBoundsException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		/* {@squirreljme.error EB1e Cannot insert choice at the specified
		index because it is not within bounds. (The index to add at)} */
		List<__ChoiceEntry__> entries = this._entries;
		if (__v < 0 || __v > entries.size())
			throw new IndexOutOfBoundsException(String.format("EB1e %d",
				__v));
		
		// Insert
		entries.add(__v, new __ChoiceEntry__(__s, __i));
	}
	
	@Override
	public boolean isEnabled(int __i)
	{
		throw Debugging.todo();
	}
	
	@Override
	public boolean isSelected(int __a)
	{
		throw Debugging.todo();
	}
	
	@Override
	public void set(int __i, String __label, Image __icon)
	{
		throw Debugging.todo();
	}
	
	@Override
	public void setEnabled(int __i, boolean __e)
	{
		throw Debugging.todo();
	}
	
	@Override
	public void setFitPolicy(int __a)
	{
		throw Debugging.todo();
	}
	
	@Override
	public void setFont(int __a, Font __b)
	{
		throw Debugging.todo();
	}
	
	@Override
	public void setSelectedFlags(boolean[] __flags)
	{
		throw Debugging.todo();
	}
	
	@Override
	public void setSelectedIndex(int __a, boolean __b)
	{
		throw Debugging.todo();
	}
	
	/**
	 * Returns the number of choices which are in this group.
	 *
	 * @return The number of choices in this group.
	 * @since 2017/08/20
	 */
	@Override
	public int size()
	{
		return this._entries.size();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2023/01/14
	 */
	@Override
	__CommonState__ __stateInit(UIBackend __backend)
		throws NullPointerException
	{
		return new __ChoiceGroupState__(__backend, this);
	}
	
	/**
	 * Choice group state.
	 * 
	 * @since 2023/01/14
	 */
	static class __ChoiceGroupState__
		extends Item.__ItemState__
	{
		/**
		 * Initializes the backend state.
		 *
		 * @param __backend The backend used.
		 * @param __self Self widget.
		 * @since 2023/01/14
		 */
		__ChoiceGroupState__(UIBackend __backend, DisplayWidget __self)
		{
			super(__backend, __self);
		}
	}
}


