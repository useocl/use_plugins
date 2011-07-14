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

package org.tzi.use.kodkod.assl;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.tzi.use.gen.assl.dynamics.GEvaluationException;
import org.tzi.use.gen.assl.statics.GProcedure;
import org.tzi.use.gen.tool.GCollectorImpl;
import org.tzi.use.gen.tool.GGenerator;
import org.tzi.use.gen.tool.GProcedureCall;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.main.OCLInvar;
import org.tzi.use.kodkod.main.UMLAssociation;
import org.tzi.use.kodkod.main.UMLAssociationClass;
import org.tzi.use.kodkod.main.UMLClass;
import org.tzi.use.parser.generator.ASSLCompiler;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.Log;

/**
 * This class contains the assl-kodkod-translation
 * 
 * @author JuRi
 */
public class GGeneratorKodkod extends GGenerator {

	private List<GProcedure> procedures;
	private MainWindow curMainWin;
	private HashMap<String, UMLClass> cla;
	private HashMap<String, UMLAssociation> ass;
	private HashMap<String, UMLAssociationClass> assCla;
	private ArrayList<OCLInvar> inv;
	private PrintWriter myLW;
	private AsslTranslation asslTranslation;
	
	public GGeneratorKodkod(MSystem system, MainWindow curMainWin) {
		super(system);
		// Get the KodKod-Formula
		this.procedures = null;
		this.curMainWin = curMainWin;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.gen.tool.GGenerator#startProcedure(java.lang.String, java.lang.String, java.lang.Long, java.lang.String, boolean, boolean, java.lang.Long, boolean)
	 */
	@Override
	public void startProcedure(String filename, String callstr, Long limit,
			String printFilename, boolean printBasics, boolean printDetails,
			Long randomNr, boolean checkStructure) {
		fLastResult = null;
		
        Log.setShowWarnings(false);
        
        if (randomNr == null)
            randomNr = new Long( (new Random()).nextInt(10000) );
        if (limit == null)
            limit = new Long( Long.MAX_VALUE );

        
        GProcedureCall call = null;
        PrintWriter pw = null;
        PrintWriter resultPw = null;

        try {
            
            if (procedures!=null) {
                Log.verbose("Compiling `" + callstr + "'.");
                call = ASSLCompiler.compileProcedureCall(fSystem.model(),
                                                        fSystem.state(),
                                                        callstr,
                                                        "<input>",
                                                        new PrintWriter(System.err)
                                                        );
            }
            if (call != null && procedures != null) {
                GProcedure proc = call.findMatching( procedures );
                if (proc == null)
                    Log.error( call.signatureString()
                               + " not found in " + filename );
                else {
                    resultPw = new PrintWriter(System.out);
                    if (printFilename==null)
                        pw = resultPw;
                    else
                        pw = new PrintWriter(
                                             new BufferedWriter(new FileWriter(printFilename)));

                    GCollectorImpl collector = new GCollectorImpl();
                    collector.setLimit(limit.longValue());
                    if (printBasics || printDetails)
                        collector.setBasicPrintWriter(pw);
                    if (printDetails)
                        collector.setDetailPrintWriter(pw);

                    GCheckerKodKod checker = new GCheckerKodKod(fGModel, checkStructure, curMainWin, fSystem, cla, ass, assCla, inv, myLW, asslTranslation);
                    Log.verbose(proc.toString() + " started...");
                    
                    try {
                    	// check time
                    	long startTime = System.currentTimeMillis();
                        GEvalProcedureKodKod evalproc = new GEvalProcedureKodKod( proc, asslTranslation );
                        evalproc.eval(call.evaluateParams(fSystem.state()),
                                      fSystem.state(),
                                      collector,
                                      checker,
                                      randomNr.longValue());
                        
                        if(checker.getLastKodKodStruc() != null) {
                        	checker.getLastKodKodStruc().useSolution();
                        }
                        long endTime = System.currentTimeMillis();
                        curMainWin.logWriter().println("Used Time: " + (endTime - startTime) + " ms");
                        curMainWin.logWriter().println("Checked States: " + checker.getNumberOfCheckedStates()); 
                    } catch (GEvaluationException e) {
                        internalError(e, randomNr.longValue());
                        Log.error("The system state may be changed in use.");
                    } catch (StackOverflowError ex) {
                        Log.error("Evaluation aborted because of a stack " +
                                  "overflow error. Maybe there were too many "+
                                  "elements in a sequence of a for-loop.");
                        Log.error("The system state may be changed in use.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.error( e.getMessage() );
        } catch (IOException e) {
            Log.error( e.getMessage() );
        } finally {
            if (pw != null ) {
                pw.flush();
                if (printFilename != null )
                    pw.close();
            }
            if (resultPw != null )
                resultPw.flush();
            
            //Log.setShowWarnings(didShowWarnigs);
        }
	}

	public List<GProcedure> loadAsslFile(String filename) {
		Log.verbose("Compiling procedures from " + filename + ".");
        try {
			procedures = ASSLCompiler.compileProcedures(
			                                         fSystem.model(),
			                                         new FileInputStream(filename),
			                                         filename,
			                                         new PrintWriter(System.err) );
			return procedures;
		} catch (FileNotFoundException e) {
			Log.error( e.getMessage() );
		}
		return null;
	}

	/**
	 * Sets the context needed for kodkod
	 * @param classes
	 * @param associations
	 * @param associationClasses
	 * @param classInvariants
	 * @param myLogWriter
	 * @param object
	 */
	public void setKodKodContext(HashMap<String, UMLClass> cla, HashMap<String, UMLAssociation> ass, HashMap<String, UMLAssociationClass> assCla, ArrayList<OCLInvar> inv, PrintWriter myLW, AsslTranslation asslTranslation) {
		this.cla = cla;
		this.ass = ass;
		this.assCla = assCla;
		this.inv = inv;
		this.myLW = myLW;
		this.asslTranslation = asslTranslation;
	}
}
