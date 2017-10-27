// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit.cff;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import net.multiphasicapps.io.SizeLimitedInputStream;
import net.multiphasicapps.squirreljme.jit.Groupable;

/**
 * This represents a single class file.
 *
 * @since 2017/09/26
 */
public final class ClassFile
	implements Groupable
{
	/** The magic number of the class file. */
	private static final int _MAGIC_NUMBER =
		0xCAFEBABE;
	
	/** The group this class is within. */
	protected final String group;
	
	/** The version of this class. */
	protected final ClassVersion version;
	
	/** The flags for this class. */
	protected final ClassFlags classflags;
	
	/** The name of this class. */
	protected final ClassName thisname;
	
	/** The class this extends. */
	protected final ClassName supername;
	
	/** The interfaces this class implements. */
	private final ClassName[] _interfaces;
	
	/** The fields within this class. */
	private final Field[] _fields;
	
	/** The methods within this class. */
	private final Method[] _methods;
	
	/**
	 * Initializes the class file.
	 *
	 * @param __g The group this class is within.
	 * @param __ver The version of the class.
	 * @param __cf The flags for this class.
	 * @param __tn The name of this class.
	 * @param __sn The class this class extends, may be {@code null}.
	 * @param __in The interfaces this class implements.
	 * @param __fs The fields in this class.
	 * @param __ms The methods in this class.
	 * @throws InvalidClassFormatException If the class is not valid.
	 * @throws NullPointerException On null arguments, except for {@code __sn}.
	 * @since 2017/09/26
	 */
	ClassFile(String __g, ClassVersion __ver, ClassFlags __cf, ClassName __tn,
		ClassName __sn, ClassName[] __in, Field[] __fs, Method[] __ms)
		throws InvalidClassFormatException, NullPointerException
	{
		if (__g == null || __ver == null || __cf == null || __tn == null ||
			__in == null || __fs == null || __ms == null)
			throw new NullPointerException("NARG");
		
		// Set initial group
		this.group = __g;
		
		// Check sub-arrays for null
		for (Object[] foo : new Object[][]{__in, __fs, __ms})
			for (Object f : foo)
				if (f == null)
					throw new NullPointerException("NARG");
		
		// {@squirreljme.error JI0s Either Object has a superclass which it
		// cannot extend any class or any other class does not have a super
		// class. (The current class name; The super class name)}
		if (__tn.equals(new ClassName("java/lang/Object")) != (__sn == null))
			throw new InvalidClassFormatException(String.format("JI0s %s %s",
				__tn, __sn));
		
		// Set
		this.version = __ver;
		this.classflags = __cf;
		this.thisname = __tn;
		this.supername = __sn;
		this._interfaces = __in;
		this._fields = __fs;
		this._methods = __ms;
	}
	
	/**
	 * Returns the fields within this class.
	 *
	 * @return The class fields.
	 * @since 2017/10/12
	 */
	public final Field[] fields()
	{
		return this._fields.clone();
	}
	
	/**
	 * Returns the flags for this class.
	 *
	 * @return The class flags.
	 * @since 2017/10/09
	 */
	public final ClassFlags flags()
	{
		return this.classflags;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/10/09
	 */
	@Override
	public String group()
	{
		return this.group;
	}
	
	/**
	 * Returns the names of implemented interfaces.
	 *
	 * @return The implemented interface names.
	 * @since 2017/10/09
	 */
	public final ClassName[] interfaceNames()
	{
		return this._interfaces.clone();
	}
	
	/**
	 * Methods which exist within this class.
	 *
	 * @return The class methods.
	 * @since 2017/10/09
	 */
	public final Method[] methods()
	{
		return this._methods.clone();
	}
	
	/**
	 * Returns the name of the super class.
	 *
	 * @return The name of the super class.
	 * @since 2017/10/09
	 */
	public final ClassName superName()
	{
		return this.supername;
	}
	
	/**
	 * Returns the name of the current class.
	 *
	 * @return The current class name.
	 * @since 2017/10/02
	 */
	public final ClassName thisName()
	{
		return this.thisname;
	}
	
	/**
	 * Initializes a class file which is a special representation of the
	 * following field descriptor which is either an array or a primitive
	 * type.
	 *
	 * @param __d The descriptor to create a special class for.
	 * @return The generated class file from the specified descriptor.
	 * @throws IllegalArgumentException If the descriptor is not an array
	 * or a pritimive type.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/10/09
	 */
	public static ClassFile special(FieldDescriptor __d)
		throws IllegalArgumentException, NullPointerException
	{
		if (__d == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JI0t Cannot create a special class because it
		// is not an array or primitive type. (The descriptor)}
		if (!__d.isArray() && !__d.isPrimitive())
			throw new IllegalArgumentException(String.format("JI0t %s", __d));
		
		// Build
		return new ClassFile("", ClassVersion.MAX_VERSION,
			new ClassFlags(ClassFlag.PUBLIC, ClassFlag.FINAL, ClassFlag.SUPER,
			ClassFlag.SYNTHETIC), new ClassName(__d.toString()),
			new ClassName("java/lang/Object"), new ClassName[0], new Field[0],
			new Method[0]);
	}
	
	/**
	 * This parses the input stream as a class file and returns the
	 * representation of that class file.
	 *
	 * @param __g The group this class is within.
	 * @param __is The input stream to source classes from.
	 * @return The decoded class file.
	 * @throws InvalidClassFormatException If the class file is not formatted
	 * correctly.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/09/26
	 */
	public static ClassFile decode(String __g, InputStream __is)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		// Check
		if (__g == null || __is == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JI0u The magic number for the class is not
		// valid. (The read magic number; The expected magic number)}
		DataInputStream in = new DataInputStream(__is);
		int magic = in.readInt();
		if (magic != _MAGIC_NUMBER)
			throw new InvalidClassFormatException(String.format(
				"JI0u %08x %08x", magic, _MAGIC_NUMBER));
		
		// {@squirreljme.error JI0v The version number of the input class
		// file is not valid. (The version number)}
		int cver = in.readShort() | (in.readShort() << 16);
		ClassVersion version = ClassVersion.findVersion(cver);
		if (version == null)
			throw new InvalidClassFormatException(String.format("JI0v %d.%d",
				cver >>> 16, (cver & 0xFFFF)));
		
		// Decode the constant pool
		Pool pool = Pool.decode(in);
		
		// Decode flags
		ClassFlags classflags = new ClassFlags(in.readUnsignedShort());
		
		// Name of the current class
		ClassName thisname = pool.<ClassName>require(ClassName.class,
			in.readUnsignedShort());
		
		// Read super class
		ClassName supername = pool.<ClassName>get(ClassName.class,
			in.readUnsignedShort());
		
		// Read interfaces
		int icount = in.readUnsignedShort();
		ClassName[] interfaces = new ClassName[icount];
		for (int i = 0; i < icount; i++)
			interfaces[i] = pool.<ClassName>require(ClassName.class,
				in.readUnsignedShort());
		
		// Read fields
		Field[] fields = Field.decode(version, thisname, classflags, pool, in);
		
		// Read methods
		Method[] methods = Method.decode(version, thisname, classflags, pool,
			in);
		
		// Handle attributes
		int na = in.readUnsignedShort();
		String[] attr = new String[1];
		int[] alen = new int[1];
		for (int j = 0; j < na; j++)
			try (DataInputStream ai = __nextAttribute(in, pool, attr, alen))
			{
				// Just do nothing with any attribute because to the VM none
				// of the information is really that important anyway
			}
		
		// {@squirreljme.error JI0w Expected end of the class to follow the
		// attributes in the class. (The name of this class)}
		if (in.read() >= 0)
			throw new InvalidClassFormatException(
				String.format("JI0w %s", thisname));
		
		// Build
		return new ClassFile(__g, version, classflags, thisname, supername,
			interfaces, fields, methods);
	}
	
	/**
	 * Reads the next attribute from the class.
	 *
	 * @param __in The input stream where bytes come from.
	 * @param __pool The constant pool.
	 * @param __aname The output name of the attribute which was just read.
	 * @param __alens The length of the attribute.
	 * @return The stream to the attribute which just has been read.
	 * @throws IOException On read errors.
	 * @throws InvalidClassFormatException If the attribute is not correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/09
	 */
	static DataInputStream __nextAttribute(DataInputStream __in,
		Pool __pool, String[] __aname, int[] __alens)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		// Check
		if (__aname == null || __alens == null)
			throw new NullPointerException("NARG");
		
		// The name is not parsed here
		__aname[0] = __pool.<UTFConstantEntry>require(UTFConstantEntry.class,
			__in.readUnsignedShort()).toString();
		
		// {@squirreljme.error JI0x Attribute exceeds 2GiB in length. (The
		// size of the attribute)}
		int len = __in.readInt();
		if (len < 0)
			throw new InvalidClassFormatException(String.format("JI0x %d",
				len & 0xFFFFFFFFL));
		
		// Used as a hint
		__alens[0] = len;
		
		// Setup reader
		return new DataInputStream(new SizeLimitedInputStream(__in, len, true,
			false));
	}
}

