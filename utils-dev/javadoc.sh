#!/bin/sh
# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
#     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: Build and update the JavaDoc.

# Force C locale
export LC_ALL=C

# Directory of this script
__exedir="$(dirname -- "$0")"

# Build the markdown doclet
if ! "$__exedir/../build.sh" build-doclet
then
	echo "Failed to build the doclet." 1>&2
	exit 1
fi

# The project directory
__pdir="$__exedir/../src"

# Clear javadoc dir
rm -rf -- "javadoc"

# Go through all namespaces
for __ns in $("$__exedir/lsnamespaces.sh")
do
	# Generate documentation for all projects
	for __dir in "$__exedir/../$__ns/"*
	do
		# If not a directory and does not contain a manifest, ignore
		if [ ! -d "$__dir" ] || [ ! -f "$__dir/META-INF/MANIFEST.MF" ]
		then
			continue
		fi
	
		# Note
		__base="$(basename "$__dir")"
		echo "Running for $__base..." 1>&2
	
		# Build binaries for project
		if ! "$__exedir/../build.sh" build "$__base"
		then
			echo "Failed to build project, ignoring." 1>&2
		fi
	
		# No source code will cause it to fail
		__sf="$(find "$__dir" -type f | grep '\.java$')"
		if [ -z "$__sf" ]
		then
			echo "No sources." 1>&2
			continue
		fi
	
		# Get dependencies
		__deps="$("$__exedir/depends.sh" "$__base")"
		__depscom="$(echo "$__deps" | tr '\n' ':')"
		__dpath="$("$__exedir/dependspath.sh" "$__base")"
	
		# Run JavaDoc
		javadoc \
			-locale "en_US" \
			-encoding "utf-8" \
			-doclet net.multiphasicapps.doclet.markdown.MarkdocMain \
			-d "javadoc/$__base" \
			-J-Dnet.multiphasicapps.doclet.markdown.debug=true \
			-squirreljme-project "$__base" \
			-squirreljme-projectsdir "$__pdir" \
			-squirreljme-depends "$__depscom" \
			-private \
			-source 1.7 \
			-docletpath "sjmedoclet.jar:." \
			-classpath "$__dpath" \
			-bootclasspath "$__dpath" \
			-sourcepath "$__dir" \
			$__sf
	done
done

# Generate Project Table of Contents
(echo "# By Project"
echo

# Echo projects file
cat "javadoc/.projects" | while read __line
do
	echo " * [$__line]($__line/classes.mkd)"
done) > javadoc/by-project.mkd

# And every single class that exists
(echo "# By Class"
echo

# Echo all class
cat "javadoc/.classes" | while read __line
do
	# Name and markdown file
	__name="$(echo "$__line" | cut -d ' ' -f 1 | sed 's/_/\\_/g')"
	__file="$(echo "$__line" | cut -d ' ' -f 2)"
	
	# Print class list
	echo " * [$__name]($__file)"
done) > javadoc/by-class.mkd

