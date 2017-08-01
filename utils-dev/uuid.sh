#!/bin/sh
# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
#     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: Creates a UUID.

# Force C locale
export LC_ALL=C

# Directory of this script
__exedir="$(dirname -- "$0")"

# One UUID command?
if which uuidgen 2>&1 > /dev/null
then
	uuidgen

# Another command
elif which uuid 2>&1 > /dev/null
then
	uuid

# Otherwise make something up, use random data source
else
	__num="$("$__exedir/random.sh" 16)"
	echo "$__num" | \
		sed 's/\(.\{8\}\)\(....\)\(....\)\(....\)\(.\{12\}\)/\1-\2-\3-\4-\5/g'
fi
