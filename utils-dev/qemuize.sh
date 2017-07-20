#!/bin/sh
# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Steven Gawroriski <steven@multiphasicapps.net>
#     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: Likely a Linux only script, run the build to a given ZIP then
# extract it and try running it.

# Force C locale
export LC_ALL=C

# Directory of this script
__exedir="$(dirname -- "$0")"

# If no arguments were pass then pass no arguments so that the target list
# and help text is printed
if [ "$#" -eq "0" ]
then
	"$__exedir/../build.sh"
	exit $?
fi

# Perform the build and run it
rm -f "$$.zip"
__jo="$JAVA_OPTIONS"
if JAVA_OPTIONS="-Dnet.multiphasicapps.squirreljme.builder.dumptarget=true \
	-Dnet.multiphasicapps.squirreljme.builder.hexdump=true $__jo" \
	"$__exedir/../build.sh" "$@" "$$.zip"
then
	# Unzip then delete the ZIP
	unzip -o "$$.zip" squirreljme
	rm -f "$$.zip"
	
	# Run it
	ls -alh squirreljme
	chmod +x squirreljme
	./squirreljme
	exit $?
else
	# Failed, so fail
	rm -f "$$.zip"
	echo "Failed build..."
	exit 1
fi

