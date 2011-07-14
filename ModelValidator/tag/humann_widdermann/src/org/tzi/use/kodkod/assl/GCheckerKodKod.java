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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.tzi.use.gen.model.GModel;
import org.tzi.use.gen.tool.GChecker;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.main.OCLInvar;
import org.tzi.use.kodkod.main.SetKodkodStruc;
import org.tzi.use.kodkod.main.UMLAssociation;
import org.tzi.use.kodkod.main.UMLAssociationClass;
import org.tzi.use.kodkod.main.UMLClass;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;

/**
 * Class checks after every procedure run the state
 * 
 * this file is based on GChecker
 * 
 * @author Juergen Widdermann
 */
public class GCheckerKodKod extends GChecker {
	
	private MainWindow curMainWin;
	private MSystem curSys;
	private HashMap<String, UMLClass> cla;
	private HashMap<String, UMLAssociation> ass;
	private HashMap<String, UMLAssociationClass> assCla;
	private ArrayList<OCLInvar> inv;
	private PrintWriter myLW;
	private AsslTranslation asslTranslation;
	private SetKodkodStruc setKodkod;
	private int checkCount;

	/**
	 * Constructor needs the context
	 * @param model
	 * @param check
	 * @param curMainWin
	 * @param curSys
	 * @param cla
	 * @param ass
	 * @param assCla
	 * @param inv
	 * @param myLW
	 * @param asslTranslation
	 */
	public GCheckerKodKod(GModel model, boolean check, MainWindow curMainWin, MSystem curSys, HashMap<String, UMLClass> cla, HashMap<String, UMLAssociation> ass, HashMap<String, UMLAssociationClass> assCla, ArrayList<OCLInvar> inv, PrintWriter myLW, AsslTranslation asslTranslation) {
		super(model, check);
		this.curMainWin = curMainWin;
		this.curSys = curSys;
		this.cla = cla;
		this.ass = ass;
		this.assCla = assCla;
		this.inv = inv;
		this.myLW = myLW;
		this.asslTranslation = asslTranslation;
		this.checkCount = 0;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.gen.assl.dynamics.IGChecker#check(org.tzi.use.uml.sys.MSystemState, java.io.PrintWriter)
	 */
	@Override
	public boolean check(MSystemState state, PrintWriter pw) {
		this.checkCount++;
		asslTranslation.resetLowerBounds();
		asslTranslation.readSystemState(state);
		setKodkod = new SetKodkodStruc(curMainWin, curSys, cla, ass, assCla, inv, myLW);
		setKodkod.setASSLTranslation(asslTranslation);
		setKodkod.runKodkod(false, 2, false);
		return setKodkod.hasASolution();
	}
	
	/**
	 * Returns the last Kodkod instance
	 * @return
	 */
	public SetKodkodStruc getLastKodKodStruc() {
		return setKodkod;
	}
	
	/**
	 * Returns the number of the checked states
	 * @return
	 */
	public int getNumberOfCheckedStates() {
		return this.checkCount;
	}
}
