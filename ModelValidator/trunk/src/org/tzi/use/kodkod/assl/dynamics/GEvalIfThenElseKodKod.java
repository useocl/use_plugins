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

import org.tzi.use.gen.assl.dynamics.GConfiguration;
import org.tzi.use.gen.assl.dynamics.GEvalInstruction;
import org.tzi.use.gen.assl.dynamics.GEvaluationException;
import org.tzi.use.gen.assl.dynamics.IGCaller;
import org.tzi.use.gen.assl.dynamics.IGCollector;
import org.tzi.use.gen.assl.statics.GIfThenElse;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.Value;

/**
 * eval If-then-else
 * 
 * based on {@link GEvalIfThenElse}
 * 
 * @author Juergen Widdermann
 */
public class GEvalIfThenElseKodKod extends GEvalInstruction implements IGCaller {

	private GIfThenElse fInstr;
    private IGCaller fCaller;

    public GEvalIfThenElseKodKod(GIfThenElse instr) {
        fInstr = instr;
    }

    public void eval(GConfiguration conf,
                     IGCaller caller,
                     IGCollector collector) throws GEvaluationException {
        collector.detailPrintWriter().println(new StringBuilder("evaluating `").append(fInstr).append("'").toString());
        fCaller = caller;
        GCreatorKodKod.createFor(fInstr.conditionInstr()).eval( conf, this, collector );
    }

    public void feedback(GConfiguration conf,
                         Value value,
                         IGCollector collector ) throws GEvaluationException {
        if (value != null) {
            if (value.isUndefined()) {
                collector.invalid(
                                  buildCantExecuteMessage( fInstr, fInstr.conditionInstr()) );
                return;
            } else
                if (((BooleanValue)value).value() ) {
                    GCreatorKodKod.createFor(fInstr.thenInstructionList())
                        .eval( conf, this, collector );
                } 
                else {
                    GCreatorKodKod.createFor(fInstr.elseInstructionList())
                        .eval( conf, this, collector );
                }
        } else {
            fCaller.feedback( conf, null, collector );
        }
    
    }

    public String toString() {
        return "GEvalIfThenElse";
    }

}
