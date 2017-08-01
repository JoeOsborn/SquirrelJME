// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.io;

public interface DataOutput
{
	public abstract void write(int __a)
		throws IOException;
	
	public abstract void write(byte[] __a)
		throws IOException;
	
	public abstract void write(byte[] __a, int __b, int __c)
		throws IOException;
	
	public abstract void writeBoolean(boolean __a)
		throws IOException;
	
	public abstract void writeByte(int __a)
		throws IOException;
	
	public abstract void writeBytes(String __a)
		throws IOException;
	
	public abstract void writeChar(int __a)
		throws IOException;
	
	public abstract void writeChars(String __a)
		throws IOException;
	
	public abstract void writeDouble(double __a)
		throws IOException;
	
	public abstract void writeFloat(float __a)
		throws IOException;
	
	public abstract void writeInt(int __a)
		throws IOException;
	
	public abstract void writeLong(long __a)
		throws IOException;
	
	public abstract void writeShort(int __a)
		throws IOException;
	
	public abstract void writeUTF(String __a)
		throws IOException;
}

