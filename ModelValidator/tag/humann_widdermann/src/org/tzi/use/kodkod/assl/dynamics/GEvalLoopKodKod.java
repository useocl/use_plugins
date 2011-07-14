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

import java.util.ListIterator;

import org.tzi.use.gen.assl.dynamics.GConfiguration;
import org.tzi.use.gen.assl.dynamics.GEvalInstruction;
import org.tzi.use.gen.assl.dynamics.GEvaluationException;
import org.tzi.use.gen.assl.dynamics.IGCaller;
import org.tzi.use.gen.assl.dynamics.IGCollector;
import org.tzi.use.gen.assl.statics.GLoop;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.Value;

/**
 * eval Loop
 * 
 * based on {@link GEvalLoopKodKod}
 * 
 * @author Juergen Widdermann
 */
public class GEvalLoopKodKod extends GEvalInstruction
    implements IGCaller {
    private GLoop fInstr;
    private IGCaller fCaller;
    private ListIterator<Value> fSeqIterator;

    public GEvalLoopKodKod(GLoop instr) {
        fInstr = instr;
    }

    public void eval(GConfiguration conf,
                     IGCaller caller,
                     IGCollector collector) throws GEvaluationException {
        collector.detailPrintWriter().println(new StringBuilder("evaluating `").append(fInstr).append("'").toString());
        fCaller = caller;
        fSeqIterator = null;
        GCreatorKodKod.createFor(fInstr.sequenceInstr()).eval( conf, this, collector );
    }

    public void feedback(GConfiguration conf,
                         Value value,
                         IGCollector collector ) throws GEvaluationException {
        if (fSeqIterator == null) {
            if (value.isUndefined())
                collector.invalid(
                                  buildCantExecuteMessage( fInstr, fInstr.sequenceInstr()) );
            else {
                fSeqIterator = ((SequenceValue)value).list().listIterator();
            }
        }
        if (fSeqIterator!=null) {
            if (fSeqIterator.hasNext()) {
                Value seqElem = (Value) fSeqIterator.next();
                collector.detailPrintWriter().println(fInstr.decl().name() + ":=" + seqElem );
                conf.varBindings().push(fInstr.decl().name(), seqElem);
                GCreatorKodKod.createFor(fInstr.instructionList())
                    .eval( conf, this, collector );
                conf.varBindings().pop();
                fSeqIterator.previous();
            } else {
                fCaller.feedback( conf, null, collector );
            }
        }
    }

    public String toString() {
        return "GEvalLoop";
    }

}
