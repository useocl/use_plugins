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

package org.tzi.use.plugin.filmstrip.layout;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import org.tzi.use.gui.views.diagrams.DiagramViewWithObjectNode;
import org.tzi.use.gui.views.diagrams.ObjectNodeActivity;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkEnd;
import org.tzi.use.uml.sys.MLinkSet;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;

public class FilmstripObjectDiagramLayouter {

	private DiagramViewWithObjectNode diagram;
	private MSystem system;
	
	public FilmstripObjectDiagramLayouter(DiagramViewWithObjectNode diagram, MSystem system) {
		this.diagram = diagram;
		this.system = system;
	}

	public void groupObjects(boolean verticalGroup)
    {
    	Point2D.Double opcNodePosition = new Point2D.Double();
    	Point2D.Double snapshotNodePosition = new Point2D.Double();
    	Point2D.Double appNodePosition = new Point2D.Double();
    	double offset = getObjectOffset(verticalGroup);
    	int i=1;
    	MObject firstSnapshotObj = getFirstSnapshotObj();
    	if(firstSnapshotObj!=null)
    	{
	    	//Set new position for the first snapshot object
	    	snapshotNodePosition = getSnapshotNodePos(i, offset, verticalGroup);
	    	setPosition(firstSnapshotObj,snapshotNodePosition);
	    	//Set new random positions for all application objects belong to the first snapshot
	    	Set<MObject> appObjs = getAssociatedObjectsbySourceRole(firstSnapshotObj, FilmstripModelConstants.SNAPSHOT_ROLENAME);
	    	MObject nextAppObj = null;
	    	for(MObject appObj: appObjs)
	    	{
	    		appNodePosition = getRandomAppNodePos(offset, verticalGroup);
	    		setPosition(appObj,appNodePosition);
	    		nextAppObj = getSuccAppObj(appObj);
	    		while(nextAppObj !=null)
	    		{
	    			appNodePosition =getSuccAppNodePos(appNodePosition, offset, verticalGroup);
	    			setPosition(nextAppObj, appNodePosition);
	    			nextAppObj = getSuccAppObj(nextAppObj);
	    		}
	    	}
	    	//find and set new position for corresponding operationcall object and succ snapshot
	    	MObject opcObj = getOPCObj(firstSnapshotObj);
	    	MObject nextSnapshotObj = getSuccSnapshotObj(firstSnapshotObj);
	    	while(opcObj != null && nextSnapshotObj !=null)
	    	{
	    		opcNodePosition = getOpcNodePos(snapshotNodePosition, offset, verticalGroup);
	    		setPosition(opcObj,opcNodePosition);
    	    	snapshotNodePosition = getSnapshotNodePos(++i, offset, verticalGroup);
    	    	setPosition(nextSnapshotObj,snapshotNodePosition);
    	    	
    	    	//loop
	    		opcObj = getOPCObj(nextSnapshotObj);
	    		nextSnapshotObj = getSuccSnapshotObj(nextSnapshotObj);
	    	}
    	}
    	diagram.repaint();
    }
    
    private MObject getFirstSnapshotObj()
    {
    	//get all Snapshot objects
    	MClass cls = system.model().getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME);
    	Set<MObject> snapshotObjs = getVisibleObjects(cls);
    	//Get set of first snapshot object - 
    	MAssociation assocFilmstrip = system.model().getAssociation(FilmstripModelConstants.FILMSTRIP_ASSOCNAME);
    	for(MObject obj: snapshotObjs)
    	{	
    		if(getAssociatedObjectsbyAssoc_TargetRole(obj,assocFilmstrip,FilmstripModelConstants.PRED_ROLENAME).isEmpty())
    			return obj;
    	}
    	return null;
    }
    
	private MObject getSuccSnapshotObj(MObject snapshotObj)
    {
    	MAssociation assocFilmstrip = system.model().getAssociation(FilmstripModelConstants.FILMSTRIP_ASSOCNAME);
    	Set<MObject> snapshotObjs= getAssociatedObjectsbyAssoc_TargetRole(snapshotObj,assocFilmstrip,FilmstripModelConstants.SUCC_ROLENAME);
    	if(snapshotObjs.iterator().hasNext())
    	{
    		return snapshotObjs.iterator().next();
    	}
    	return null;
    }
    private MObject getOPCObj(MObject snapshotObj)
    {
    	MAssociation assocFilmstrip = system.model().getAssociation(FilmstripModelConstants.FILMSTRIP_ASSOCNAME);
    	Set<MObject> opcObjs= getAssociatedObjectsbyAssoc_TargetRole(snapshotObj,assocFilmstrip,FilmstripModelConstants.makeRoleName(FilmstripModelConstants.OPC_CLASSNAME));
    	for(MObject obj: opcObjs)
    	{	
    		if(getAssociatedObjectsbyAssoc_TargetRole(obj,assocFilmstrip,FilmstripModelConstants.PRED_ROLENAME).contains(snapshotObj))
    			return obj;
    	}
    	return null;
    }
    private MObject getSuccAppObj(MObject appObj)
    {
    	MAssociation assocApplicationCls = system.model().getAssociation(FilmstripModelConstants.ORDERABLE_ASSOCNAME + appObj.cls().name());
    	Set<MObject> snapshotObjs= getAssociatedObjectsbyAssoc_TargetRole(appObj,assocApplicationCls,FilmstripModelConstants.SUCC_ROLENAME);
    	if(snapshotObjs.iterator().hasNext())
    	{
    		return snapshotObjs.iterator().next();
    	}
    	return null;
    }
    private void setPosition(MObject obj, Point2D.Double pos)
    {
    	diagram.moveObjectNode(obj, (int) pos.getX(), (int) pos.getY());
    }
    //get the position offset for the next object in filmstrip object diagram
    private double getObjectOffset(boolean b)
    {
    	//get all Snapshot objects
    	MClass cls = system.model().getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME);
    	Set<MObject> snapshotObjs = getVisibleObjects(cls);
    	return b? diagram.getHeight()/(snapshotObjs.size()+1):diagram.getWidth()/(snapshotObjs.size()+1) ;
    }

    private Point2D.Double getSnapshotNodePos(int i, double offset, boolean b)
    {
    	//b = true: vertical group; b=false: horizontal group
    	Point2D.Double snapshotNodePosition =new Point2D.Double();
		snapshotNodePosition.x = b? diagram.getWidth()/4 : offset*i;
    	snapshotNodePosition.y = b? offset*i: diagram.getHeight()/4;
    	return snapshotNodePosition;
    }
    private Point2D.Double getOpcNodePos(Point2D.Double snapshotNodePos, double offset, boolean b)
    {
    	//b = true: vertical group; b=false: horizontal group
    	Point2D.Double opcNodePosition =new Point2D.Double();
    	opcNodePosition.x = b?0: snapshotNodePos.x + offset/2;
    	opcNodePosition.y = b?snapshotNodePos.y + offset/2: 0;
    	return opcNodePosition;
    }
    private Point2D.Double getRandomAppNodePos(double offset, boolean b)
    {
    	//Get random position for application object in the FIRST snapshot 
    	//b = true: vertical group; b=false: horizontal group
    	Point2D.Double appNodePosition = new Point2D.Double();
    	appNodePosition.x =  b? diagram.getWidth()/2 + (diagram.getWidth()/2 - 100)* Math.random() : offset*Math.random() + offset/2;
		appNodePosition.y =  b? offset*Math.random() + offset/2 : diagram.getHeight()/2 + (diagram.getHeight()/2 -100)* Math.random();
		return appNodePosition;
    }
    private Point2D.Double getSuccAppNodePos(Point2D.Double predAppNodePos, double offset, boolean b)
    {
    	Point2D.Double appNodePosition = new Point2D.Double();
    	appNodePosition.x = b?predAppNodePos.x: predAppNodePos.x + offset;
		appNodePosition.y = b?predAppNodePos.y + offset : predAppNodePos.y;
		return appNodePosition;
    }
    
    /**
     * Get all objects that have association with an object by association instance and associated object's rolename
     */
	public Set<MObject> getAssociatedObjectsbyAssoc_TargetRole(MObject obj, MAssociation assoc, String targetRoleName)
    {
    	Set<MObject> result = new HashSet<>();
    	MLinkSet links = system.state().linksOfAssociation(assoc);
		for (MLink link : links.links())
			if (link.linkedObjects().contains(obj))
			{
				Set<MLinkEnd> linkEnds = link.linkEnds();
				for (MLinkEnd linkEnd : linkEnds)
				{
					String sRoleName = linkEnd.associationEnd().nameAsRolename();
					if(sRoleName.equals(targetRoleName) && !linkEnd.object().equals(obj))
						result.add(linkEnd.object());
				}
			}
		return result;
    }
    /**
     * Get all objects that have association with an object by the object's rolename
     */
    public Set<MObject> getAssociatedObjectsbySourceRole(MObject obj, String srcRoleName)
    {
    	Set<MObject> result = new HashSet<>();
    	Set<MLink> links = system.state().allLinks();
    	Boolean ck;
		for (MLink link : links)
			if (link.linkedObjects().contains(obj))
			{
				ck = false;
				Set<MLinkEnd> linkEnds = link.linkEnds();
				for (MLinkEnd linkEnd : linkEnds)
				{
					String sRoleName = linkEnd.associationEnd().nameAsRolename();
					if(sRoleName.equals(srcRoleName) && linkEnd.object().equals(obj))
						ck=true;
				}
				if(ck)
				{
					for (MLinkEnd linkEnd : linkEnds)
					{
						if(!linkEnd.object().equals(obj))
							result.add(linkEnd.object());
					}
				}
			}
		return result;
    }
    
    /*
     * FIXME code duplication from ObjectSelection.java, which is a bad place for this functionality
     * -> make code available in diagram?
     */
    private Set<MObject> getVisibleObjects(MClass cls) {
		Set<MObject> objects = new HashSet<MObject>();

		for (PlaceableNode node : diagram.getVisibleData().getNodes()) {
			if (node instanceof ObjectNodeActivity) {
				MObject mobj = ((ObjectNodeActivity) node).object();
				if (cls.equals(mobj.cls())) {
					objects.add(mobj);
				}
			}
		}

		return objects;
	}
}
