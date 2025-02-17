// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the Mozilla Public License Version 2.0.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jdwp;

import cc.squirreljme.jdwp.views.JDWPViewObject;
import cc.squirreljme.jdwp.views.JDWPViewType;

/**
 * Handles class object references.
 *
 * @since 2021/04/30
 */
public enum CommandSetClassObjectReference
	implements JDWPCommand
{
	/** The actual type of the class object. */
	REFLECTED_TYPE(1)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/04/30
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			Object object = __packet.readObject(__controller, false);
			
			// This must be Class<?>...
			JDWPViewType viewType = __controller.viewType();
			JDWPViewObject viewObject = __controller.viewObject();
			if (viewObject == null || !viewObject.isValid(viewObject))
			{
				Object objectType = __controller.viewObject().type(object);
				
				// Ensure this is correct
				if (objectType == null || !viewType.isValid(objectType) ||
					!"Ljava/lang/Class;"
						.equals(viewType.signature(objectType)))
					throw ErrorType.INVALID_OBJECT.toss(object,
						System.identityHashCode(object),
						new Throwable("Not a Class object."));
			}
			
			// Ask the viewer to get the type for this Class reference
			Object found = viewType.typeOfClassInstance(object);
			if (found != null && viewType.isValid(found))
				__controller.state.items.put(found);
			
			// Could not directly get, so search through all types to find
			if (found == null || !viewType.isValid(found))
				for (Object type : __controller.__allTypes(false))
				{
					// Is this a match?
					Object maybe = viewType.instance(type);
					if (maybe == object)
					{
						found = type;
						break; 
					}
				}
			
			// Not found?
			if (found == null || !viewType.isValid(found))
				throw ErrorType.INVALID_OBJECT.toss(object,
					System.identityHashCode(object),
					new Throwable("No matching type found."));
					
			// Make sure it is known
			__controller.state.items.put(found);
			
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			// Write the class type and reference to that type
			rv.writeTaggedId(__controller, found);
				
			return rv;
		}
	},
	
	/* End. */
	;
	
	/** The ID of the packet. */
	public final int id;
	
	/**
	 * Returns the ID used.
	 * 
	 * @param __id The ID used.
	 * @since 2021/04/30
	 */
	CommandSetClassObjectReference(int __id)
	{
		this.id = __id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/30
	 */
	@Override
	public final int debuggerId()
	{
		return this.id;
	}
}
