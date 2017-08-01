#!/bin/sh
# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
#     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: This scans all source files for special JavaDoc tags which
# describe what the error codes mean.

# Force C locale
export LC_ALL=C

# Directory of this script
__exedir="$(dirname -- "$0")"

# Allow a custom directory to be specified
if [ "$#" -ge "1" ]
then
	__dir="$1"
else
	__dir="$(pwd)"
fi

# Get the directory of the project
__base="$("$__exedir/projectbase.sh" "$__dir")"

# Print error code, potentially
echo "******** LIST OF ERRORS *********" 1>&2
if [ -f "$__base/META-INF/MANIFEST.MF" ]
then
	__code="$(grep -i 'x-squirreljme-error' < "$__base/META-INF/MANIFEST.MF" |
		cut -d ':' -f 2 | tr -d ' ')"
	
	echo "Project Error Code: $__code" 1>&2
fi

# List errors
(grep -rl '{@squirreljme\.error[ \t]\{1,\}....' "$__base" | \
	grep -e '\.java$' -e '\.c$' -e '\.h$' | while read __file
do
	tr '\n' '\f' < "$__file" | sed 's/{@code[ \t]\{1,\}\([^}]*\)}/\1/g' |
		sed 's/{@squirreljme\.error[ \t]\{1,\}\([^}]*\)}/\v##ER \1 ##FI\v/g' |
		sed 's/\/\///g' | sed 's/\/\*//g' | tr '\v' '\n' |
		grep '##ER' | sed 's/^##ER[ \t]*//g' |
		sed 's/##FI/<'"$(basename $__file)"'>/g' |
		sed 's/\f[ \t]*\*[ \t]*/ /g' |
		tr '\f' ' '| sed 's/[ \t\f]\{2,\}/ /g'
done) | sort | uniq
echo "*********************************" 1>&2

