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
import org.tzi.use.gen.assl.dynamics.GEvaluationException;
import org.tzi.use.gen.assl.dynamics.IGCaller;
import org.tzi.use.gen.assl.dynamics.IGCollector;
import org.tzi.use.gen.assl.statics.GInstruction;
import org.tzi.use.gen.assl.statics.GInstructionList;
import org.tzi.use.uml.ocl.value.Value;

/**
 * eval instructionlist
 * 
 * based on {@link GEvalInstructionList}
 * 
 * @author Juergen Widdermann
 */
public class GEvalInstructionListKodKod implements IGCaller {
    private GInstructionList fInstr;
    private IGCaller fCaller;
    private ListIterator<GInstruction> fIterator;

    private static int fIdentcounter =0;

    public GEvalInstructionListKodKod( GInstructionList instr ) {
        fInstr = instr;
        fIdentcounter++;
    }

    public void eval(GConfiguration conf,
                     IGCaller caller,
                     IGCollector collector) throws GEvaluationException {
        fCaller = caller;
        fIterator = fInstr.instructions().listIterator();
        proceed(conf,collector);
    }

    public void feedback(GConfiguration conf,
                         Value value,
                         IGCollector collector) throws GEvaluationException {
        proceed(conf,collector);
    }

    protected void proceed(GConfiguration conf,
                         IGCollector collector) throws GEvaluationException {
        if (fIterator.hasNext() ) {
        	GInstruction instr = (GInstruction) fIterator.next();
            GCreatorKodKod.createFor(instr)
                .eval(conf,this,collector);
            fIterator.previous();
        }
        else
            fCaller.feedback( conf, null, collector );
    }

    public String toString() {
        return "GEvalInstructionList";
    }
    
}