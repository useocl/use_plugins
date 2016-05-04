package org.tzi.kodkod.helper;

import java.util.Iterator;

import kodkod.engine.Proof;
import kodkod.engine.fol2sat.TranslationRecord;
import kodkod.engine.ucore.HybridStrategy;

public class ProofHelper {

	private ProofHelper(){}
	
	public static String buildProofString(Proof proof, boolean minimize){
		StringBuilder sb = new StringBuilder();
		
		if(minimize){
			proof.minimize(new HybridStrategy(proof.log()));
		}
		
		sb.append("Unsatisfiable proof:\n");
		
		Iterator<TranslationRecord> iter = proof.core(); 
		while (iter.hasNext()) {
			TranslationRecord rec = iter.next();
			sb.append(rec.toString() + "\n"); 
		}
		
		return sb.toString();
	}
	
}
