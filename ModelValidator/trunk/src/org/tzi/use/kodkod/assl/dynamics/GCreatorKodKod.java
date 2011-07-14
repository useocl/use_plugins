/*
 * This is source code of the Snapshot Generator, an extension for USE
 * to generate (valid) system states of UML models.
 * Copyright (C) 2001 Joern Bohling, University of Bremen
 *
 * About USE:
 *   USE - UML based specification environment
 *   Copyright (C) 1999,2000,2001 Mark Richters, University of Bremen
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

/**
 * March 22th 2001 
 * @author  Joern Bohling
 */

package org.tzi.use.kodkod.assl.dynamics;

import java.util.HashMap;
import java.util.Map;

import org.tzi.use.gen.assl.dynamics.GEvalInstruction;
import org.tzi.use.gen.assl.dynamics.GEvaluationException;
import org.tzi.use.gen.assl.statics.GAttributeAssignment;
import org.tzi.use.gen.assl.statics.GIfThenElse;
import org.tzi.use.gen.assl.statics.GInstrAny_Seq;
import org.tzi.use.gen.assl.statics.GInstrCreateN_C_Integer;
import org.tzi.use.gen.assl.statics.GInstrCreate_C;
import org.tzi.use.gen.assl.statics.GInstrDelete_Assoc_Linkends;
import org.tzi.use.gen.assl.statics.GInstrDelete_Object;
import org.tzi.use.gen.assl.statics.GInstrInsert_Assoc_Linkends;
import org.tzi.use.gen.assl.statics.GInstrSub_Seq;
import org.tzi.use.gen.assl.statics.GInstrSub_Seq_Integer;
import org.tzi.use.gen.assl.statics.GInstrTry_Assoc_LinkendSeqs;
import org.tzi.use.gen.assl.statics.GInstrTry_Seq;
import org.tzi.use.gen.assl.statics.GInstruction;
import org.tzi.use.gen.assl.statics.GInstructionList;
import org.tzi.use.gen.assl.statics.GLoop;
import org.tzi.use.gen.assl.statics.GOCLExpression;
import org.tzi.use.gen.assl.statics.GVariableAssignment;
import org.tzi.use.kodkod.assl.AsslTranslation;

public class GCreatorKodKod {

    // utility class
    private GCreatorKodKod() {}
    
    private static AsslTranslation asslTranslation;
    
    /**
	 * @return the asslTranslation
	 */
	public static AsslTranslation getAsslTranslation() {
		return asslTranslation;
	}

	/**
	 * @param asslTranslation the asslTranslation to set
	 */
	public static void setAsslTranslation(AsslTranslation asslTranslation) {
		GCreatorKodKod.asslTranslation = asslTranslation;
	}

	private static Map<Class<? extends GInstruction>, IInstCreator> createMap = new HashMap<Class<? extends GInstruction>, IInstCreator>(15);
    
    public static GEvalInstructionListKodKod createFor(GInstructionList instrlist) {
        return new GEvalInstructionListKodKod(instrlist);
    }
     
    static {
    	createMap.put(GInstrTry_Seq.class, new CreateGEvalInstrTry_Seq());
    	createMap.put(GOCLExpression.class, new CreateGEvalOCLExpression());
    	createMap.put(GVariableAssignment.class, new CreateGEvalVariableAssignment());
    	createMap.put(GLoop.class, new CreateGEvalLoop());
    	createMap.put(GIfThenElse.class, new CreateGEvalIfThenElse());
    	createMap.put(GInstrCreate_C.class, new CreateGEvalInstrCreate_C());
    	createMap.put(GInstrCreateN_C_Integer.class, new CreateGEvalInstrCreateN_C_Integer());
    	createMap.put(GInstrInsert_Assoc_Linkends.class, new CreateGEvalInstrInsert_Assoc_Linkends());
    	createMap.put(GInstrDelete_Assoc_Linkends.class, new CreateGEvalInstrDelete_Assoc_Linkends());
    	createMap.put(GInstrAny_Seq.class, new CreateGEvalInstrAny_Seq());
    	createMap.put(GInstrSub_Seq.class, new CreateGEvalInstrSub_Seq());
    	createMap.put(GInstrSub_Seq_Integer.class, new CreateGEvalInstrSub_Seq_Integer());
    	createMap.put(GAttributeAssignment.class, new CreateGEvalAttributeAssignment());
    	createMap.put(GInstrTry_Assoc_LinkendSeqs.class, new CreateGEvalInstrTry_Assoc_LinkendSeqs());
    	createMap.put(GInstrDelete_Object.class, new CreateGEvalInstrDelete_Object());
    }
    
    public static GEvalInstruction createFor(GInstruction instr)
        throws GEvaluationException {
            	
    	if (instr == null) {
    		return null;
    	} else {
    		IInstCreator creator = createMap.get(instr.getClass());
    	
    		if (creator == null) {
    			throw new GEvaluationException("The execution of the instruction `"
    										   + instr + "' is not implemented.");
    		}
    		
    		return creator.create(instr);
    	}
    }
}

interface IInstCreator {
	GEvalInstruction create(GInstruction instr);
}

final class CreateGEvalInstrTry_Seq implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrTry_SeqKodKod( (GInstrTry_Seq) instr, GCreatorKodKod.getAsslTranslation().isTryInstructionValueUsed(instr), GCreatorKodKod.getAsslTranslation());
	}
}

final class CreateGEvalOCLExpression implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalOCLExpressionKodKod( (GOCLExpression) instr );
	}
}

final class CreateGEvalVariableAssignment implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalVariableAssignmentKodKod( (GVariableAssignment) instr, GCreatorKodKod.getAsslTranslation() );
	}
}

final class CreateGEvalLoop implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalLoopKodKod( (GLoop) instr );
	}
}

final class CreateGEvalIfThenElse implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalIfThenElseKodKod( (GIfThenElse) instr );
	}
}

final class CreateGEvalInstrCreate_C implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrCreate_CKodKod( (GInstrCreate_C) instr, GCreatorKodKod.getAsslTranslation() );
	}
}

final class CreateGEvalInstrCreateN_C_Integer implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrCreateN_C_IntegerKodKod( (GInstrCreateN_C_Integer) instr, GCreatorKodKod.getAsslTranslation() );
	}
}

final class CreateGEvalInstrInsert_Assoc_Linkends implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrInsert_Assoc_LinkendsKodKod( (GInstrInsert_Assoc_Linkends) instr, GCreatorKodKod.getAsslTranslation() );
	}
}

final class CreateGEvalInstrDelete_Assoc_Linkends implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrDelete_Assoc_LinkendsKodKod( (GInstrDelete_Assoc_Linkends) instr );
	}
}

final class CreateGEvalInstrAny_Seq implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrAny_SeqKodKod( (GInstrAny_Seq) instr );
	}
}

final class CreateGEvalInstrSub_Seq implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrSub_SeqKodKod( (GInstrSub_Seq) instr );
	}
}

final class CreateGEvalInstrSub_Seq_Integer implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrSub_Seq_IntegerKodKod( (GInstrSub_Seq_Integer) instr );
	}
}

final class CreateGEvalAttributeAssignment implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalAttributeAssignmentKodKod( (GAttributeAssignment) instr, GCreatorKodKod.getAsslTranslation() );
	}
}

final class CreateGEvalInstrTry_Assoc_LinkendSeqs implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrTry_Assoc_LinkendSeqsKodKod( (GInstrTry_Assoc_LinkendSeqs) instr, GCreatorKodKod.getAsslTranslation(), GCreatorKodKod.getAsslTranslation().isTryInstructionValueUsed(instr));
	}
}

final class CreateGEvalInstrDelete_Object implements IInstCreator {
	public GEvalInstruction create(GInstruction instr) {
            return new GEvalInstrDelete_ObjectKodKod( (GInstrDelete_Object) instr );
    }
}