/*
 * Bytecode Analysis Framework
 * Copyright (C) 2003, University of Maryland
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.daveho.ba;

import java.util.*;

// We require BCEL 5.0 or later.
import org.apache.bcel.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

/**
 * Simple basic block abstraction for BCEL.
 * Does not take exception control edges into account.
 * @see CFG
 */
public class BasicBlock implements Comparable {
    private int id;
    private LinkedList<InstructionHandle> instructionList;
    private boolean isExceptionThrower;
    private CodeExceptionGen exceptionGen; // set if this block is the entry point of an exception handler

    /**
     * Constructor.
     */
    public BasicBlock(int id) {
	this.id = id;
	instructionList = new LinkedList<InstructionHandle>();
	isExceptionThrower = false;
	exceptionGen = null;
    }

    /**
     * Get this BasicBlock's unique identifier.
     */
    public int getId() {
	return id;
    }

    /**
     * Set whether or not this block is an exception thrower.
     */
    public void setExceptionThrower(boolean isExceptionThrower) {
	this.isExceptionThrower = isExceptionThrower;
    }

    /**
     * Return whether or not this block is an exception thrower.
     */
    public boolean isExceptionThrower() {
	return isExceptionThrower;
    }

    /** Get the first instruction in the basic block. */
    public InstructionHandle getFirstInstruction() {
	return instructionList.isEmpty() ? null : instructionList.getFirst();
    }

    /** Get the last instruction in the basic block. */
    public InstructionHandle getLastInstruction() {
	return instructionList.isEmpty() ? null : instructionList.getLast();
    }

    /**
     * Add an InstructionHandle to the basic block.
     * @param handle the InstructionHandle
     */
    public void addInstruction(InstructionHandle handle) {
	// Only add the instruction if it hasn't already been added.
	if (instructionList.isEmpty() || instructionList.getLast() != handle)
	    instructionList.addLast(handle);
    }

    /**
     * Get an Iterator over the instructions in the basic block.
     */
    public Iterator<InstructionHandle> instructionIterator() {
	return instructionList.iterator();
    }

    /**
     * Get an Iterator over the instructions in the basic block in reverse order.
     * This is useful for backwards dataflow analyses.
     */
    public Iterator<InstructionHandle> instructionReverseIterator() {
	return new Iterator<InstructionHandle>() {
	    private ListIterator<InstructionHandle> realIter = instructionList.listIterator(instructionList.size());

	    public boolean hasNext() {
		return realIter.hasPrevious();
	    }

	    public InstructionHandle next() throws NoSuchElementException {
		return realIter.previous();
	    }

	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    private static final PCRange[] EMPTY_RANGE_LIST = new PCRange[0];

    /**
     * Get the ranges of instructions constituting this basic block.
     * @return array of PCRange objects representing all instruction ranges
     *   in this basic block (because of JSR/RET, there may be multiple
     *   discontiguous ranges)
     */
    public PCRange[] getRangeList() {
	ArrayList<PCRange> rangeList = new ArrayList<PCRange>();

	Iterator<InstructionHandle> iter = instructionIterator();
	if (!iter.hasNext())
	    return EMPTY_RANGE_LIST;

	InstructionHandle first = iter.next();
	InstructionHandle current = first, prev = null;

	while (true) {
	    // Start of new range?
	    if (prev != null && current != prev.getNext()) {
		rangeList.add(new PCRange(first, prev));
		first = current;
	    }

	    // End of list?
	    if (!iter.hasNext()) {
		rangeList.add(new PCRange(first, current));
		break;
	    }

	    // Continuation of current range
	    prev = current;

	    // Advance to next instruction in block
	    current = iter.next();
	}

	return (PCRange[]) rangeList.toArray(EMPTY_RANGE_LIST);
    }

    /**
     * Return true if there are no Instructions in this basic block.
     */
    public boolean isEmpty() {
	return instructionList.isEmpty();
    }

    /**
     * For implementation of Comparable interface.
     * Basic blocks are ordered by their unique id.
     */
    public int compareTo(Object o) {
	BasicBlock other = (BasicBlock) o;
	return this.id - other.id;
    }

    /** Is this block an exception handler? */
    public boolean isExceptionHandler() { return exceptionGen != null; }

    /** Get CodeExceptionGen object; returns null if this basic block is
        not the entry point of an exception handler. */
    public CodeExceptionGen getExceptionGen() {
	return exceptionGen;
    }

    /** Set the CodeExceptionGen object.  Marks this basic block as
       the entry point of an exception handler. */
    public void setExceptionGen(CodeExceptionGen exceptionGen) {
	this.exceptionGen = exceptionGen;
    }

}
