package org.tzi.use.kodkod.transform;

import org.tzi.kodkod.model.impl.Multiplicity;
import org.tzi.kodkod.model.impl.Range;
import org.tzi.use.uml.mm.MMultiplicity;

/**
 * Class to transform a multiplicity of the use model in a multiplicity for the
 * model of the model validator.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class MultiplicityTransformator {

	public Multiplicity transform(MMultiplicity mMultiplicity) {
		String[] parts = mMultiplicity.toString().split(",");

		Multiplicity multiplicity = new Multiplicity();
		for (String part : parts) {
			if (part.length() == 1) {
				int value = singleValue(part);
				if (value != Multiplicity.MANY) {
					multiplicity.addRange(new Range(value, value));
				} else {
					multiplicity.addRange(new Range(0, value));
				}
			} else {
				String[] singleValues = part.split("\\.\\.");
				int min = singleValue(singleValues[0]);
				int max = singleValue(singleValues[1]);
				multiplicity.addRange(new Range(min, max));
			}
		}

		return multiplicity;
	}

	private int singleValue(String part) {
		if (part.charAt(0) == '*') {
			return Multiplicity.MANY;
		} else {
			return Integer.parseInt(part);
		}
	}
}
