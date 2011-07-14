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
import org.tzi.use.gen.assl.statics.GAttributeAssignment;
import org.tzi.use.gen.assl.statics.GValueInstruction;
import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.StatementEvaluationResult;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MStatement;

/**
 * eval attribute assigment
 * 
 * based on {@link GEvalAttributeAssignment}
 * 
 * @author Juergen Widdermann
 * 
 */
public class GEvalAttributeAssignmentKodKod extends GEvalInstruction implements
		IGCaller {
	private GAttributeAssignment fInstr;
	private IGCaller fCaller;
	private String fObjectName;
	// additional KODKOD
	private AsslTranslation asslTranslation;

	/**
	 * @param instr
	 */
	public GEvalAttributeAssignmentKodKod(GAttributeAssignment instr,
			AsslTranslation asslTranslation) {
		fInstr = instr;
		// additional KODKOD
		this.asslTranslation = asslTranslation;
	}

	public void eval(GConfiguration conf, IGCaller caller, IGCollector collector)
			throws GEvaluationException {

		collector.detailPrintWriter().println(
				new StringBuilder("evaluating `").append(fInstr).append("'")
						.toString());
		fCaller = caller;
		fObjectName = null;

		GCreatorKodKod.createFor(fInstr.targetObjectInstr()).eval(conf, this,
				collector);
	}

	public void feedback(GConfiguration conf, Value value, IGCollector collector)
			throws GEvaluationException {

		if (fObjectName == null) {
			if (value.isUndefined()) {
				GValueInstruction culprit = fInstr.targetObjectInstr();
				collector.invalid(buildCantExecuteMessage(fInstr, culprit));
			} else {
				fObjectName = ((ObjectValue) value).value().name();
				GEvalInstruction instruction = GCreatorKodKod.createFor(fInstr
						.sourceInstr());

				instruction.eval(conf, this, collector);
			}

			return;
		}

		MSystemState state = conf.systemState();
		MSystem system = state.system();
		PrintWriter basicOutput = collector.basicPrintWriter();

		MObject object = state.objectByName(fObjectName);

		Expression objectExpression = new ExpressionWithValue(object.value());
		//new ExpVariable(fObjectName, object.type());
		MAttribute attribute = fInstr.targetAttribute();
		Expression valueExpression = new ExpressionWithValue(value);

		if(value instanceof CollectionValue) {
			// add every value to upper KodKod bound
			boolean reset = true;
			for(Value elem : (CollectionValue)value){
				asslTranslation.addAttributeToObjectUpperBound(object, attribute.name(), elem, reset);
				reset = false;
			}
			fCaller.feedback(conf, null, collector);
		} else {
			//Backtracking
			
			//Reset Upper-Bound for the attribute
			asslTranslation.addAttributeToObjectUpperBound(object, attribute.name(), null, true);
			
			MStatement statement = new MAttributeAssignmentStatement(
					objectExpression, attribute, valueExpression);
	
			MStatement inverseStatement;
	
			basicOutput.println(statement.getShellCommand());
			try {
				StatementEvaluationResult evaluationResult = system
						.evaluateStatement(statement, true, false, false);
	
				inverseStatement = evaluationResult.getInverseStatement();
	
			} catch (MSystemException e) {
				throw new GEvaluationException(e);
			}
			
			fCaller.feedback(conf, null, collector);
			if (collector.expectSubsequentReporting()) {
				collector.subsequentlyPrependStatement(statement);
			}
	
			basicOutput.println("undo: " + statement.getShellCommand());
			try {
				system.evaluateStatement(inverseStatement, true, false, false);
			} catch (MSystemException e) {
				throw new GEvaluationException(e);
			}
		}
	}

	public String toString() {
		return "GEvalAttributeAssignment";
	}

}
