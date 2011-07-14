/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2010 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// $Id$

package org.tzi.use.kodkod.assl.dynamics;

import java.io.PrintWriter;

import org.tzi.use.gen.assl.dynamics.GConfiguration;
import org.tzi.use.gen.assl.dynamics.GEvalInstruction;
import org.tzi.use.gen.assl.dynamics.GEvaluationException;
import org.tzi.use.gen.assl.dynamics.IGCaller;
import org.tzi.use.gen.assl.dynamics.IGCollector;
import org.tzi.use.gen.assl.statics.GInstrDelete_Object;
import org.tzi.use.gen.assl.statics.GValueInstruction;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.StatementEvaluationResult;
import org.tzi.use.uml.sys.soil.MObjectDestructionStatement;
import org.tzi.use.uml.sys.soil.MStatement;

/**
 * eval delete Object
 * 
 * based on {@link GEvalInstrDelete_Object}
 * 
 * @author Juergen Widdermann
 */
public class GEvalInstrDelete_ObjectKodKod extends GEvalInstruction implements IGCaller {
    private GInstrDelete_Object fInstr;
    private IGCaller fCaller;

    public GEvalInstrDelete_ObjectKodKod(GInstrDelete_Object instr ) {
        fInstr = instr;
    }

    public void eval(GConfiguration conf,
                     IGCaller caller,
                     IGCollector collector) throws GEvaluationException {
        collector.detailPrintWriter().println(new StringBuilder("evaluating `").append(fInstr).append("'").toString());
        fCaller = caller;
        GCreatorKodKod.createFor(fInstr.objectInstr()).eval(conf,this,collector );
    }

    public void feedback(
    		GConfiguration conf, 
    		Value value, 
    		IGCollector collector) throws GEvaluationException {
    	
    	if (value.isUndefined()) {
    		GValueInstruction culprit = fInstr.objectInstr();
            collector.invalid(buildCantExecuteMessage(fInstr, culprit));
            return;
        }
    	
    	MSystemState state = conf.systemState();
    	MSystem system = state.system();
    	PrintWriter basicOutput = collector.basicPrintWriter();
    	
    	MStatement statement = new MObjectDestructionStatement(value);
    	MStatement inverseStatement;
    	
    	basicOutput.println(statement.getShellCommand());
    	
    	try {
    		StatementEvaluationResult evaluationResult = 
    			system.evaluateStatement(statement, true, false, false);
    		inverseStatement = evaluationResult.getInverseStatement();
    		
		} catch (MSystemException e) {
			collector.invalid(e);
			return;
		}
		
		//conf.varBindings().push(fInstr.target(), value);
		
		fCaller.feedback(conf, null, collector);
		if (collector.expectSubsequentReporting()) {
			collector.subsequentlyPrependStatement(statement);
		}
		
		basicOutput.println("undo: " + statement.getShellCommand());
		
		try {
			system.evaluateStatement(inverseStatement, true, false);
		} catch (MSystemException e) {
			collector.invalid(e);
		}
    }

    public String toString() {
        return "GEvalInstrDelete_Object";
    }
}