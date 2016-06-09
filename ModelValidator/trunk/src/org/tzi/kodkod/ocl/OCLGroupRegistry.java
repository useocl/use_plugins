package org.tzi.kodkod.ocl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;

/**
 * Central registry for all classes with methods to transform an ocl operation
 * in the relation logic.
 * 
 * @author Hendrik Reitmann
 * 
 */
public enum OCLGroupRegistry {

	INSTANCE;

	private static final Logger LOG = Logger.getLogger(OCLGroupRegistry.class);

	private Set<OCLOperationGroup> operationGroups;
	private Map<String, String> symbolOperationMapping;

	private OCLGroupRegistry() {
		operationGroups = new HashSet<OCLOperationGroup>();
		symbolOperationMapping = new HashMap<String, String>();
	}

	/**
	 * Register a new group with transformation methods.
	 * 
	 * @param operationGroup
	 */
	public void registerOperationGroup(OCLOperationGroup operationGroup) {
		operationGroups.add(operationGroup);
		
		Map<String, String> groupMapping = operationGroup.getSymbolOperationMapping();
		for (String operator : groupMapping.keySet()) {
			if (!symbolOperationMapping.containsKey(operator)) {
				symbolOperationMapping.put(operator, groupMapping.get(operator));
			} else {
				if (symbolOperationMapping.get(operator).equals(groupMapping.get(operator))) {
					LOG.warn(LogMessages.doubleSymbolOperationMappingWarning(operator));
				} else {
					LOG.error(LogMessages.differentSymbolOperationMappingsError(operator, symbolOperationMapping.get(operator)));
				}
			}
		}
	}

	/**
	 * Unregister a group.
	 * 
	 * @param operationGroup
	 */
	public void unregisterOperationGroup(OCLOperationGroup operationGroup) {
		operationGroups.remove(operationGroup);
	}

	/**
	 * Unregister all registered groups.
	 */
	public void unregisterAll() {
		operationGroups.clear();
		symbolOperationMapping.clear();
	}

	/**
	 * Returns the mapping of symbol to the names of the transformation methods.
	 * 
	 * @return
	 */
	public Map<String, String> getSymbolOperationMapping() {
		return symbolOperationMapping;
	}

	/**
	 * Returns all registered groups.
	 * 
	 * @return
	 */
	public Set<OCLOperationGroup> getOperationGroups() {
		return operationGroups;
	}
}
