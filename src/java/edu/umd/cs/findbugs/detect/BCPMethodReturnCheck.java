/*
 * FindBugs - Find bugs in Java programs
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

package edu.umd.cs.findbugs.detect;

import java.util.*;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import edu.umd.cs.findbugs.*;
import edu.umd.cs.daveho.ba.*;
import edu.umd.cs.daveho.ba.bcp.*;

/**
 * This detector looks for places where the return value of a method
 * is suspiciously ignored.  Ignoring the return values from immutable
 * objects such as java.lang.String are a common and easily found type of bug.
 *
 * @author David Hovemeyer
 * @author Bill Pugh
 */
public class BCPMethodReturnCheck extends ByteCodePatternDetector {
	private final BugReporter bugReporter;
	private final ByteCodePattern pattern;

	/**
	 * Constructor.
	 * @param bugReporter the BugReporter to report bug instances with
	 */
	public BCPMethodReturnCheck(BugReporter bugReporter) {
		this.bugReporter = bugReporter;

		// The ByteCodePattern which specifies the kind of code pattern
		// we're looking for.  We want to match the invocation of certain methods
		// followed by a POP or POP2 instruction.
		this.pattern = new ByteCodePattern()
			.add(new MatchAny(new PatternElement[] {
				/*
				new Invoke("java.lang.String", "/.*", 
					"/\\(.*\\)Ljava\\.lang\\.String;", 
					Invoke.INSTANCE, bugReporter),
				*/
				new Invoke("java.lang.String", "/.*", 
					"/\\(.*\\)Ljava/lang/String;", 
					Invoke.INSTANCE, bugReporter),
				new Invoke("java.lang.StringBuffer", "toString", 
					"()Ljava/lang/String;", 
					Invoke.INSTANCE, 
					bugReporter),
				new Invoke("+java.lang.Thread", "<init>", 
					"/.*", 
					Invoke.CONSTRUCTOR, 
					bugReporter),
				/*
				new Invoke("java.lang.Thread", "currentThread", 
					"()Ljava/lang/Thread;", 
					Invoke.STATIC, 
					bugReporter),
				*/
				new Invoke("java.security.MessageDigest", 
					"digest", "([B)[B", 
					Invoke.INSTANCE, bugReporter),
				new Invoke("+java.net.InetAddress", "/.*", "/.*", 
					Invoke.INSTANCE, bugReporter),
				new Invoke("java.math.BigDecimal", "/.*", "/.*", 
					Invoke.INSTANCE, bugReporter),
				new Invoke("java.math.BigInteger", "/.*", "/.*", 
					Invoke.INSTANCE, bugReporter),
				new Invoke("java.io.File", "createNewFile", "()Z", Invoke.INSTANCE, bugReporter),
			}).label("call").setAllowTrailingEdges(false))
			.add(new MatchAny(new PatternElement[] {new Opcode(Constants.POP), new Opcode(Constants.POP2)}));
	}

	public ByteCodePattern getPattern() { return pattern; }

	public boolean prescreen(Method method, ClassContext classContext) {
		// Pre-screen for methods with POP or POP2 bytecodes.
		// This gives us a speedup close to 5X.
		BitSet bytecodeSet = classContext.getBytecodeSet(method);
		return bytecodeSet.get(Constants.POP) || bytecodeSet.get(Constants.POP2);
	}

	public void reportMatch(JavaClass javaClass, MethodGen methodGen, ByteCodePatternMatch match) {
		InstructionHandle call = match.getLabeledInstruction("call");

		// Ignore inner-class access methods
		InvokeInstruction inv = (InvokeInstruction) call.getInstruction();
		String calledMethodName = inv.getMethodName(methodGen.getConstantPool());
		if (calledMethodName.startsWith("access$"))
			return;

		String sourceFile = javaClass.getSourceFileName();
		bugReporter.reportBug(new BugInstance("RV_RETURN_VALUE_IGNORED", NORMAL_PRIORITY)
			.addClassAndMethod(methodGen, sourceFile)
			.addCalledMethod(methodGen, inv)
			.addSourceLine(methodGen, sourceFile, call));
	}

}

// vim:ts=4
