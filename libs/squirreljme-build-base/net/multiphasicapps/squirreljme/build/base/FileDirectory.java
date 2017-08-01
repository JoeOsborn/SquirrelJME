// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.build.base;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * A file directory represents the contents of a binary JAR file or a tree of
 * source code within a directory.
 *
 * Entries are named according to how they would appear in ZIP files with
 * no distinction of directories being made (so a file would be called for
 * example {@code META-INF/MANIFEST.MF} regardless of the host system).
 *
 * Iteration goes over the contents of the directory.
 *
 * @since 2016/12/21
 */
public interface FileDirectory
	extends Closeable, Iterable<String>
{
	/**
	 * Checks whether the given directory contains the given file.
	 *
	 * @param __fn The file to check for containing.
	 * @return {@code true} if the directory contains this file.
	 * @throws If whether the file exists could not be determined.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/12/26
	 */
	public abstract boolean contains(String __fn)
		throws IOException, NullPointerException;
	
	/**
	 * Opens the specified file and returns the stream to the file data.
	 *
	 * @param __fn The file name to open.
	 * @return The stream of the input file.
	 * @throws IOException If the file does not exist or it could not be
	 * opened.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/12/21
	 */
	public abstract InputStream open(String __fn)
		throws IOException, NullPointerException;
}

