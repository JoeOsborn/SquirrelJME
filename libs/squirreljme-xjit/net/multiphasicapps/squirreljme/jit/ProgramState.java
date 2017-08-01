// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import net.multiphasicapps.util.sorted.SortedTreeMap;

/**
 * This contains the entire state of the program. The program consists of
 * multiple {@link BasicBlockZone}s. Each basic block as an entry state and
 * encompasses a set of instructions. Control flows from one basic block to
 * another depending on the output and input alias state. The program is parsed
 * in a queue order generating virtual instructions as needed for each region
 * of basic blocks for differing aliasing types.
 *
 * @since 2017/05/13
 */
public class ProgramState
{
	/** The byte code for the method. */
	protected final ByteCode code;
	
	/** The JIT configuration. */
	protected final JITConfig config;
	
	/** The link table for imports. */
	protected final CompiledClass linktable;
	
	/** The queue of basic blocks to be processed. */
	private final Deque<BasicBlock> _queue =
		new ArrayDeque<>();
	
	/** Mapping of zone keys to state maps for basic blocks. */
	private final Map<ZoneKey, BasicBlockStateMap> _zonestatemaps =
		new SortedTreeMap<>();
	
	/**
	 * Initializes the program state.
	 *
	 * @param __code The method byte code.
	 * @param __smtdata The stack map data.
	 * @param __smtmodern Is the stack map table a modern one?
	 * @param __em The method this program is for.
	 * @param __conf The JIT configuration.
	 * @param __lt The link table used for imports and exports.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/05/14
	 */
	ProgramState(ByteCode __code, byte[] __smtdata, boolean __smtmodern,
		ExportedMethod __em, JITConfig __conf, CompiledClass __lt)
		throws IOException, NullPointerException
	{
		// Check
		if (__code == null || __smtdata == null || __em == null ||
			__conf == null || __lt == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.config = __conf;
		this.code = __code;
		this.linktable = __lt;
		int codelen = __code.length();
		
		// Parse the stack map to determine the starting state of each
		// basic block zone
		__StackMapParser__ smp = new __StackMapParser__(__code, __smtdata,
			__smtmodern, __em);
		
		// The reference to self is used by the zone keys
		Reference<ProgramState> prs = new WeakReference<>(this);
		
		throw new todo.TODO();
		/*
		// Initialize the basic block zones which determines which sections
		// of the program will be parsed as a single unit
		List<BasicBlockZone> zones = new ArrayList<>();
		int baseat = 0;
		JumpTarget[] jumptargets = __code.jumpTargets();
		for (int i = 0, n = jumptargets.length; i <= n; i++)
		{
			// Ignore this address if it matches the jump target
			int jumpat = (i == n ? codelen : jumptargets[i].address());
			if (jumpat == baseat)
				continue;
			
			// Debug
			System.err.printf("DEBUG -- BBZ %d - %d%n", baseat, jumpat);
			
			// Add new zone
			zones.add(new BasicBlockZone(__code, baseat, jumpat,
				smp.get(baseat), prs));
			
			// New base address
			baseat = jumpat;
		}
		this._zones = zones.<BasicBlockZone>toArray(
			new BasicBlockZone[zones.size()]);
		*/
	}
	
	/**
	 * Initialize the initial basic block entry point.
	 *
	 * @return The initial entry basic block.
	 * @since 2017/05/28
	 */
	private BasicBlock __initialBlock()
	{
		// Associate that state with the initial entry point
		if (true)
			throw new todo.TODO();
		
		// Push it to the queue
		if (true)
			throw new todo.TODO();
		
		throw new todo.TODO();
	}
	
	/**
	 * Runs the program recompilation process.
	 *
	 * @since 2017/05/20
	 */
	void __run()
	{
		// Setup initial method allocation on entry, allocate registers and
		// bind
		Deque<BasicBlock> queue = this._queue;
		queue.offerLast(__initialBlock());
		if (true)
			throw new todo.TODO();
		
		// Process any blocks waiting in the queue
		while (!queue.isEmpty())
		{
			throw new todo.TODO();
		}
		
		throw new todo.TODO();
	}
}

