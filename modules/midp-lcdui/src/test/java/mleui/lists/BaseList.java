// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package mleui.lists;

import cc.squirreljme.jvm.mle.brackets.UIDisplayBracket;
import cc.squirreljme.jvm.mle.brackets.UIFormBracket;
import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.constants.UIItemPosition;
import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;
import mleui.forms.BaseUIFormTest;

/**
 * This is the base class for any tests on lists.
 *
 * @since 2020/10/18
 */
public abstract class BaseList
	extends BaseUIFormTest
{
	/**
	 * Tests the list.
	 * 
	 * @param __backend The backend used.
	 * @param __display The display used.
	 * @param __form The form used.
	 * @param __list The list used.
	 * @since 2020/10/18
	 */
	public abstract void test(UIBackend __backend, UIDisplayBracket __display,
		UIFormBracket __form, UIItemBracket __list);
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/18
	 */
	@Override
	protected void test(UIBackend __backend, UIDisplayBracket __display,
		UIFormBracket __form)
		throws Throwable
	{
		UIItemBracket list = __backend.itemNew(UIItemType.LIST);
		try
		{
			// Add to the form
			__backend.formItemPosition(__form, list, UIItemPosition.BODY);
			
			// Forward to the test
			this.test(__backend, __display, __form, list);
		}
		
		// Try deleting the item and freeing it up
		finally
		{
			try
			{
				__backend.formItemRemove(__form, UIItemPosition.BODY);
				__backend.itemDelete(list);
			}
			
			// Ignored otherwise
			catch (MLECallError e)
			{
				e.printStackTrace();
			}
		}
	}
}
