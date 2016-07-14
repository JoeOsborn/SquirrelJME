#!/bin/sh
# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
#     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3, or later.
# For more information see license.txt.
# ---------------------------------------------------------------------------
# DESCRIPTION: Creates files and adds them in one go, faster typing.
# Enhanced on 2015/06/23 to auto templatize files based on their name and
# extension.

# Force C locale
export LC_ALL=C

# Directory of this script
__exedir="$(dirname -- "$0")"

# Handle arguments
__cmd=""
__makex="0"
__noadd="0"
__notemplate="0"

# Get the current date in string form.
__nowyear="$(date +%Y)"
__nowmont="$(date +%m)"
__nowdayy="$(date +%d)"

__nowtime="$__nowyear$__nowmont$__nowdayy"
__htmtime="$__nowyear\/$__nowmont\/$__nowdayy"

# Help
print_help()
{
	echo "Usage: $0 [-b] [-x] [-n] [-c command] [files...]"
	echo ""
	echo " -b Do not use a template."
	echo " -x Make the files executable."
	echo " -n Do not add files to the fossil repository."
	echo " -c Run the specified command for each file."
}

# Handle arguments
while getopts "bnxc:" FOO
do
	case $FOO in
		c)
			__cmd="$OPTARG"
			;;
		
		x)
			__makex="1"
			;;
		
		b)
			__notemplate="1"
			;;
		
		n)
			__noadd="1"
			;;
		
		*)
			print_help
			exit 1
			;;
	esac 
done

# Down they go
shift $(($OPTIND - 1))

# All files
__files=""

# Returns the absolute oath of a file
__absolutepath()
{
	"$__exedir/absolute.sh" "$1"
}

# Find the name of the package (looks for the vmjar directory which contains
# REPOSITORY).
__findpkname()
{
	# Get directory file is in
	__indir="$(dirname "$(__absolutepath "$1")")"
	__want="$2"
	
	# Directory loop
	__pkout="unknown.package"
	__chop=""
	while [ "$__indir" != "/" ]
	do
		# Wanting 
		if [ "$__want" -eq "0" ]
		then
			# Has repository file right above this path
			if [ -f "$__indir/../steven-gawroriski.gpg" ] || \
				[ -f "$__indir/../../steven-gawroriski.gpg" ]
			then
				__pkout="$__chop"
				break
			fi
		
		# Want name of project this is
		elif [ "$__want" -eq "1" ]
		then
			# If repository file in same dir
			if [ -f "$__indir/steven-gawroriski.gpg" ]
			then
				echo "$__chop" | \
					sed 's/^\([a-zA-Z0-9-]\{1,\}\)[^a-zA-Z0-9-]*.*/\1/'
				return
			fi
		fi
		
		# Get base name and chop down
		if [ -z "$__chop" ]
		then
			__chop="$(basename "$__indir")"
		else
			__chop="$(basename "$__indir").$__chop"
		fi
		__indir="$(dirname "$__indir")"
	done
	
	# Want project directory, here it is unknown
	if [ "$__want" -eq "1" ]
	then
		echo "untitled-project"
	
	# Normal thing
	else
		# Use it directly
		echo "$__pkout"
	fi
}

# Go through all arguments
while [ "$#" -gt "0" ]
do
	# Create upper directoroies
	mkdir -p "$(dirname -- "$1")"
	
	# Extra file
	__extra="$1"
	
	# Until there is no extra
	while [ -n "$__extra" ]
	do
		# Get the absolute path of the current file
		__tabsf="$(__absolutepath "$__extra")"
	
		# Get location and name of file
		__tfile="$(basename -- "$__tabsf")"
		__tidir="$(dirname -- "$__tabsf")"
	
		# Get extension of file
		__tbase="$(echo -n "$__tfile" | sed 's/\(.*\)\..*$/\1/')"
		if echo "$__tfile" | grep '\.' > /dev/null
		then
			__tfext="$(echo -n "$__tfile" | sed 's/.*\.\(.*\)$/\1/')"
		else
			__tfext=""
		fi
	
		# If the extension contains a plus, then more stuff can be created
		# This is so instead of "Foo.java Foo.properties" one can just do
		# "Foo.java+properties" to save on typing.
		if echo "$__tfext" | grep '\+' > /dev/null
		then
			# Before and after the plus
			__befp="$(echo "$__tfext" | sed 's/^\([^\+]*\)+\(.*\)$/\1/')"
			__aftp="$(echo "$__tfext" | sed 's/^\([^\+]*\)+\(.*\)$/\2/')"
		
			# Extra is after the plus
			__extra="$__tidir/$__tbase.$__aftp"
			__tfext="$__befp"
			
			# Need to use an alternate output file
			__afil="$__tidir/$__tbase.$__tfext"
		
		# Nothing extra to do
		else
			# Keep actual file as is
			__afil="$__extra"
			
			# No file
			__extra=""
		fi
		
		# Actual file to create
		if [ -n "$__tfext" ]
		then
			__afile="$__tidir/$__tbase.$__tfext"
		else
			__afile="$__tidir/$__tbase"
		fi
		
		# Add to all files
		__files="$__files $__afile"
	
		# Create file
		if [ ! -f "$1" ]
		then
			# From a template?
			if [ "$__notemplate" -eq "0" ]
			then
				# Name of the package
				__tpack="$(__findpkname "$__tabsf" 0)"
			
				# Name of the project
				__tproj="$(__findpkname "$__tabsf" 1)"
			
				# Name of class
				__tclas="$__tbase"
				
				# C header clipped
				__cheadname="$__tbase.h"
				__cheadclip="$(echo "$__tpack/$__cheadname" | \
					sed 's/[^a-zA-Z0-9_]//g' | \
			sed 'y/qwertyuiopasdfghjklzxcvbnm/QWERTYUIOPASDFGHJKLZXCVBNM/' |
				sed 's/^\(.\{1,13\}\).*\(.\{11\}\)$/\1\2/')"
			
				# C stuff
				__cheadgard="SJME_hG${__cheadclip}"
				__ccxxisbad="SJME_cX${__cheadclip}"
			
				# Java source code
				(if [ "$__tfext" = "java" ]
				then
					# Package file
					if [ "$__tbase" = "package-info" ]
					then
						cat "$__exedir/crtmpl/package-info" 
					else
						cat "$__exedir/crtmpl/java"
					fi
				
				# Other template
				elif [ -f "$__exedir/crtmpl/$__tfext" ]
				then
					cat "$__exedir/crtmpl/$__tfext"
				
				# Unknown, blank file
				else
					echo
				fi) | sed 's/ZZZCLASSZZZ/'"$__tclas"'/g' \
					| sed 's/ZZZPACKAGEZZZ/'"$__tpack"'/g' \
					| sed 's/ZZZSINCEZZZ/'"$__htmtime"'/g' \
					| sed 's/ZZZPROJECTZZZ/'"$__tproj"'/g' \
					| sed 's/ZZZSDATEZZZ/'"$__nowtime"'/g' \
					| sed 's/ZZZCHEADERNAMEZZZ/'"$__cheadname"'/g' \
					| sed 's/ZZZCHEADERCLIPZZZ/'"$__cheadclip"'/g' \
					| sed 's/ZZZCHEADERGUARDZZZ/'"$__cheadgard"'/g' \
					| sed 's/ZZZCXXISBADZZZ/'"$__ccxxisbad"'/g' > "$__afil"
		
			# Not wanting a template
			else
				# Touch base file so it exists
				touch "$__afil"
			fi
		fi
	
		# Make it executable
		if [ "$__makex" -ne "0" ]
		then
			chmod +x "$__afil"
		fi
	
		# Add to repo
		if [ "$__noadd" -eq "0" ]
		then
			fossil add "$__afil"
		fi
	done
	
	# Down one
	shift 1
done

# Run command on it
if [ -n "$__cmd" ] && [ -n "$__files" ]
then
	"$__cmd" $__files
fi

