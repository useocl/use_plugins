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

package org.tzi.use.kodkod.gui;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.tzi.use.kodkod.assl.GGeneratorKodkod;

/**
 * A Thread for the assl-kodkod translation
 * 
 * @author Juergen Widdermann
 */
public class AsslTranslationRunnable  extends SwingWorker<Void, Void> {
	private GGeneratorKodkod asslGen;
	private String filePath;
	private String procedureCall;
	private JFrame window;
	private AsslView asslView;
	
	public AsslTranslationRunnable(GGeneratorKodkod asslGen, String filePath, String procedureCall, JFrame window, AsslView asslView) {
		this.asslGen = asslGen;
		this.filePath = filePath;
		this.procedureCall = procedureCall;
		this.window = window;
		this.asslView = asslView;
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		asslGen.startProcedure(filePath, procedureCall, null, null, false, false, null, true);
		window.repaint();
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	protected void done() {
		asslView.getStartButton().setEnabled(true);
		asslView.setCursor(null); //turn off the wait cursor
		asslView.getProgressBar().setIndeterminate(false);
		super.done();
	}
}
