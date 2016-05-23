package org.tzi.use.plugin.filmstrip.logic;

import java.util.ArrayList;
import java.util.List;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;

public class TransformationInputException extends Exception {
	
	public static class ModelElements {
		private List<MClass> classes = new ArrayList<MClass>();
		private List<MAssociation> assocs = new ArrayList<MAssociation>();
		private List<MAssociationEnd> roles = new ArrayList<MAssociationEnd>();
		private List<MClassInvariant> invs = new ArrayList<MClassInvariant>();
		
		public ModelElements(){
		}
		
		public boolean hasElements() {
			return !classes.isEmpty() || !assocs.isEmpty() || !roles.isEmpty()
					|| !invs.isEmpty();
		}
		
		public List<MClass> getClasses() {
			return classes;
		}
		
		public List<MAssociation> getAssociations() {
			return assocs;
		}
		
		public List<MAssociationEnd> getRoles() {
			return roles;
		}
		
		public List<MClassInvariant> getClassInvariants(){
			return invs;
		}
		
		public void addClass(MClass c){
			classes.add(c);
		}
		
		public void addAssociation(MAssociation assoc){
			assocs.add(assoc);
		}
		
		public void addRole(MAssociationEnd role){
			roles.add(role);
		}
		
		public void addClassInvariant(MClassInvariant invariant){
			invs.add(invariant);
		}
	}
	
	private static final long serialVersionUID = 1L;

	private ModelElements errors;
	
	public TransformationInputException(ModelElements errors) {
		super();
		this.errors = errors;
	}

	public TransformationInputException(String message, ModelElements errors) {
		super(message);
		this.errors = errors;
	}

	public TransformationInputException(Throwable cause, ModelElements errors) {
		super(cause);
		this.errors = errors;
	}

	public TransformationInputException(String message, Throwable cause, ModelElements errors) {
		super(message, cause);
		this.errors = errors;
	}
	
	public ModelElements getErrors() {
		return errors;
	}

}
