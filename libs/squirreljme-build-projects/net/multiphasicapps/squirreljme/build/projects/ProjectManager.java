// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.build.projects;

import java.io.InputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.multiphasicapps.collections.SortedTreeMap;
import net.multiphasicapps.collections.UnmodifiableMap;
import net.multiphasicapps.squirreljme.java.manifest.JavaManifest;
import net.multiphasicapps.squirreljme.java.manifest.JavaManifestAttributes;

/**
 * This class is used to manage projects which represent modules that
 * SquirrelJME contains. Each module could be part of the build system, an API
 * which is implemented by SquirrelJME, or a library/midlet which may be
 * included in a target environment.
 *
 * @since 2016/10/28
 */
public class ProjectManager
	implements Iterable<Project>
{
	/** The mapping of project names to projects. */
	protected final Map<ProjectName, Project> projects;
	
	/** The binary path. */
	protected final Path binarypath;
	
	/** The manager lock. */
	final Object _lock =
		new Object();
	
	/**
	 * Initializes the project manager.
	 *
	 * @param __bin The binary path.
	 * @param __src The root of the SquirrelJME tree.
	 * @throws IOException On read/write errors.
	 * @since 2016/10/28
	 */
	public ProjectManager(Path __bin, Path __src)
		throws IOException, NullPointerException
	{
		// Check
		if (__bin == null || __src == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.binarypath = __bin;
		
		// Scan source directory for project paths
		Map<NamespaceType, Set<Path>> sourcetree = __scanSources(null,
			__src);
		
		// Create projects associated with the namespaces
		Map<ProjectName, Project> projects = new SortedTreeMap<>();
		for (Map.Entry<NamespaceType, Set<Path>> ste : sourcetree.entrySet())
		{
			// Ignore the build namespace because it is only used by the
			// build system
			NamespaceType type = ste.getKey();
			if (type == NamespaceType.BUILD)
				continue;
			
			// Go through all project paths
			for (Path sp : ste.getValue())
				try
				{
					// Determine name
					ProjectName pn = new ProjectName(
						sp.getFileName().toString());
				
					// See if an existing project exists
					Project p = projects.get(pn);
					if (p != null)
					{
						// {@sqiuirreljme.error AT04 A project with the same
						// name as another project with a different type
						// already exists. (The project name; The current
						// project type; The previous project type)}
						NamespaceType was;
						if (type != (was = p.type()))
							throw new InvalidProjectException(
								String.format("AT04 %s %s %s", pn, type, was));
					}
				
					// Create if missing
					else
						projects.put(pn, (p = new Project(this, type, pn)));
				
					// Initialize source
					p.__initializeSource(sp);
				}
			
				// Invalid project, warn on it
				catch (InvalidProjectException e)
				{
					e.printStackTrace();
				}
		}
		
		// Store
		this.projects = UnmodifiableMap.<ProjectName, Project>of(projects);
	}
	
	/**
	 * Returns the path to the binary directory.
	 *
	 * @return The binary directory path.
	 * @since 2016/12/17
	 */
	public final Path binaryPath()
	{
		return this.binarypath;
	}
	
	/**
	 * Obtains the project which is represented by the given name.
	 *
	 * @param __s The name of the project to locate.
	 * @return The project representation or {@code null} if it does not
	 * exist.
	 * @since 2016/12/14
	 */
	public final Project get(String __s)
	{
		// If null, will not be found
		if (__s == null)
			return null;
		
		// Wrap
		return get(new ProjectName(__s));
	}
	
	/**
	 * Obtains the project which is represented by the given name.
	 *
	 * @param __s The name of the project to locate.
	 * @return The project representation or {@code null} if it does not
	 * exist.
	 * @since 2016/12/14
	 */
	public final Project get(ProjectName __s)
	{
		// Not found it null
		if (__s == null)
			return null;
		
		return this.projects.get(__s);
	}
	
	/**
	 * Obtains the binary project under the specified name.
	 *
	 * @param __s The project to get the binary for.
	 * @return The binary project or {@code null} if not found.
	 * @since 2017/05/31
	 */
	public final ProjectBinary getBinary(ProjectName __s)
	{
		Project p = get(__s);
		if (p == null)
			return null;
		
		return p.binary();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/12/17
	 */
	@Override
	public final Iterator<Project> iterator()
	{
		return this.projects.values().iterator();
	}
	
	/**
	 * Reads a manifest from the given path.
	 *
	 * @param __p The path to read from.
	 * @return The manifest that was read.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/11/24
	 */
	static final JavaManifest __readManifest(Path __p)
		throws IOException, NullPointerException
	{
		// Check
		if (__p == null)
			throw new NullPointerException("NARG");
		
		// Open the file and read it
		try (InputStream in = Channels.newInputStream(FileChannel.open(
			__p, StandardOpenOption.READ)))
		{
			return new JavaManifest(in);
		}
	}
	
	/**
	 * Scans the specified directory for source namespace layouts and returns
	 * the mapping of source projects.
	 *
	 * @param __dest The destination map to place values in, if the value is
	 * {@code null} then it is initialized.
	 * @param __root The root directory of the source tree to scan for
	 * namespaces and projects.
	 * @throws IOException On read errors.
	 * @throws NullPointerException If no root was specified.
	 * @since 2016/12/14
	 */
	static final Map<NamespaceType, Set<Path>> __scanSources(
		Map<NamespaceType, Set<Path>> __dest, Path __root)
		throws IOException, NullPointerException
	{
		// Check
		if (__root == null)
			throw new NullPointerException("NARG");
		
		// Create map as needed
		if (__dest == null)
			__dest = new LinkedHashMap<>();
		
		// Go through all source directories and determine which paths are
		// associated with namespaces.
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(__root))
		{
			for (Path p : ds)
			{
				// Ignore non-directories
				if (!Files.isDirectory(p))
					continue;
				
				// Load in manifest
				try
				{
					JavaManifest man = ProjectManager.__readManifest(
						p.resolve("NAMESPACE.MF"));
					
					// Get the type of namespace this is
					JavaManifestAttributes attr = man.getMainAttributes();
					String rtype = attr.getValue(
						"Namespace-Type");
					if (rtype == null)
					{
						// {@squirreljme.error AT0j There is a namespace
						// manifest, however it lacks a type and as such it
						// will ignored. (The path to the namespace)}
						System.err.printf("AT0j %s%n", p);
						
						// Ignore
						continue;
					}
					
					// Look it up
					NamespaceType type;
					try
					{
						type = NamespaceType.of(rtype.trim());
					}
					
					// Illegal namespace type
					catch (IllegalArgumentException e)
					{
						e.printStackTrace();
						continue;
					}
					
					// Go through this namespace and load sub-projects
					Set<Path> sub = __dest.get(type);
					try (DirectoryStream<Path> ss =
						Files.newDirectoryStream(p))
					{
						for (Path s : ss)
						{
							// Only access directories
							if (!Files.isDirectory(s) ||
								!Files.exists(s.resolve("META-INF").
									resolve("MANIFEST.MF")))
								continue;
							
							// Create entry?
							if (sub == null)
								__dest.put(type,
									(sub = new LinkedHashSet<Path>()));
							
							// Add project
							sub.add(s);
						}
					}
				}
				
				// Ignore
				catch (NoSuchFileException e)
				{
					continue;
				}
			}
		}
		
		return __dest;
	}
}

