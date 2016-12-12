package org.tzi.kodkod.helper;

import java.io.File;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.use.util.StringUtil;

import kodkod.engine.Statistics;
import static org.tzi.use.util.StringUtil.inQuotes;

/**
 * Contains the different log messages.
 * 
 * @author Hendrik Reitmann
 * 
 */
public final class LogMessages {

	public static final String modelConfigurationSuccessful = "Model configuration successful";

	public static final String letNotReachableWarning = "This state should not be reachable!";

	public static final String invTransformSuccessful = "Invariant transformation successful";

	public static final String modelTransformError = "Model transformation was not successful.";

	public static final String modelTransformSuccessful = "Model transformation successful";

	public static final String solverConfigBitwidthError = "Bitwidth has to be an int value.";

	public static final String configDiagramExtractionError = "Possible values for AutomaticObjectDiagramExtraction are only " + inQuotes("on") + " or " + inQuotes("off") + ".";

	public static final String objDiagramCreation = "Create object diagram";

	public static final String objDiagramCreationError = "Error during object diagram creation";
	
	public static final String PROPERTIES_NO_CONFIGURATION_WARNING = "No configuration name given. Using first configuration of the file.";

	public static final String propertiesConfigurationReadError = "Error while reading .properties file";

	public static final String propertiesConfigurationCreateError = "Error while creating .properties file";

	public static final String propertiesConfigurationWriteError = "Error while writing .properties file";

	public static final String configurationError = "Error during model configuration. Please check the log for concrete hints.";
	
	public static final String className$Error = "Class names starting with a " + inQuotes("$") + " are not allowed!";

	public static final String solverConfigSaveError = "Error while saving KodkodSolver configuration";

	public static final String solverConfigSaved = "Saved KodkodSolver configuration";
	
	public static final String PLINGELING_NOT_EXECUTABLE = "Solver file for plingeling is not executable and could not be made executable. The file has to be executable in order to use the solver.";

	public static final String modelResetSuccessful = "Reset successful";

	public static final String validationException = "Error while model validation! Please check your configuration.";

	public static final String valueConversionNestedCollections = "Nested collections not supported!";

	public static final String objDiagramExtractionError = "Error while extracting information from the object diagram.";

	public static final String objDiagramExtractionSuccessful = "Object diagram extraction successful.";

	public static final String pagingNext = inQuotes("next") + " to search a new solution";

	public static final String pagingPrevious = inQuotes("previous") + " to get the last solution";

	public static final String pagingFirst = "First solution reached";

	public static final String pagingCmdError = "Set a configuration file or enter next for a new solution respectively previous for the last solution.";

	public static final String pagingCmdFileFirst = "Set a configuration file first!";
	
	public static final String enrichWithLoadedInvariants = "Enrich the model with loaded invariants";

	public static final String errorWithLoadedInvariants = "Error while enriching the model with loaded invariants";

	public static final String inactiveInvariantEval = "Evaluation of inactive invariants";

	public static final String noSuchMethodError = "Please read the Readme file";

	public static final String solutionForbidError = "Error while creating the formula to forbid the last solution!";

	public static final String showSolutionIndexToSmall = "Please enter an index greater 0.";

	public static final String queryEvaluatorNotFound = "No evaluator found. Make sure the evaluator feature is enabled and run a validation first.";
	
	public static final String queryEvaluationError = "The query can not be evaluated.";
	
	public static final String unsupportedQueryType = "The type of the transformed query is not supported.";

	public static final String closureObjectMessage = "Closure is only available for collections with objects.";

	public static final String closureCollectionMessage = "Source type is not a collection.";
	
	public static String startModelTransform(String name) {
		return "Start model transformation for " + inQuotes(name);
	}

	public static String constRealWarning(Double value) {
		return "No support for real values. " + value + " becomes " + value.intValue();
	}

	public static String invTransformError(String name) {
		return "Cannot transform invariant " + inQuotes(name) + ".";
	}

	public static String invTransformSuccessful(String name) {
		return "Transformation of invariant " + inQuotes(name) + " successful";
	}

	public static String solverConfigWrongArgumentError(String name) {
		return name + " is not configurable. Only SatSolver and bitwidth.";
	}

	public static String solverConfigSyntaxError(String argument) {
		return argument + " has not the correct syntax. Use " + inQuotes("name") + " := " + inQuotes("value") + ".";
	}

	public static String invIndepSyntaxError(String argument) {
		return argument + " does not have the correct syntax. Use " + inQuotes("class") + " - " + inQuotes("invariant") + ".";
	}

	public static String unsupportedCollectionWarning(String collectionName) {
		return "This approach does not support " + collectionName + ". All collections will be handled like sets.";
	}

	public static String differentSymbolOperationMappingsError(String operator, String mapping) {
		return "Different operation names for one operator are not possible. Mapping of operator " + operator + " is " + mapping + ".";
	}

	public static String doubleSymbolOperationMappingWarning(String operator) {
		return "For operator " + operator + " exists more than one operation. Please make sure, that the implementations have different parameters!";
	}

	public static String kodkodStatistics(Statistics statistics) {
		return "Translation time (Kodkod to SAT): " + statistics.translationTime() + " ms; Solving time: " + statistics.solvingTime() + " ms";
	}

	public static String wrongBitwidthWarning(int defaultBitwidth) {
		return "The bitwidth has to be a value between 1 and 32. Default bitwidth " + defaultBitwidth + " will be used!";
	}

	public static String newBitwidth(int newBitwidth) {
		return "Set bitwidth to " + newBitwidth + ".";
	}

	public static String newSatSolver(String satFactory) {
		return "Set SatSolver to " + inQuotes(satFactory) + ".";
	}

	public static String newAutomaticDiagramExtraction(boolean automaticDiagramExtraction) {
		String mode = automaticDiagramExtraction ? "Enable" : "Disable";
		return mode + " automatic extraction of an available object diagram.";
	}

	public static String noSatSolverLibraryError(String satFactory, String defaultSatFacotry) {
		return "Could not load the library for SatSolver " + inQuotes(satFactory) + ". Using default SatSolver " + inQuotes(defaultSatFacotry) + ".";
	}

	public static String noSatSolverWarning(String solverName, String defaultSatFacotry) {
		return "No solver " + inQuotes(solverName) + " available. Using default SatSolver " + inQuotes(defaultSatFacotry) + ".";
	}
	
	public static String availableSatSolvers(String[] solvers){
		return "Detected instantiable SAT solvers are: " + StringUtil.fmtSeq(solvers, ", ");
	}

	public static String fileCmdError(File file) {
		return "Cannot read file " + file.getAbsolutePath() + "!";
	}

	public static String typeConvertError(String name) {
		return "Cannot convert type " + name;
	}

	public static String noEnumTypeError(String name) {
		return "No enum type " + name;
	}

	public static String modelTransformTime(long timeMs) {
		return "Translation time (USE to Kodkod): " + timeMs + " ms";
	}

	public static String solverConfigReadWarning(String defaultSatFactory, int defaultBitwidth) {
		return "Error while reading KodkodSolver configuration. Using default SatSolver " + inQuotes(defaultSatFactory) + " and default bitwidth "
				+ defaultBitwidth + ".";
	}

	public static String noValueCreation(String type) {
		return "Creation of values for " + type + " not implemented!";
	}

	public static Object searchSolution(String satFactory, int bitwidth) {
		return "Searching solution with SatSolver " + inQuotes(satFactory) + " and bitwidth " + bitwidth + "...";
	}

	public static String extractSatSolverWarning(String defaultSatFactory) {
		return "Extraction of satsolver libraries failed. Only default SatSolver " + inQuotes(defaultSatFactory) + " can be used! Is USE open twice?";
	}

	public static String libraryPathWarning(String defaultSatFactory, String exceptionMsg) {
		return "Only default SatSolver " + inQuotes(defaultSatFactory) + " can be used! " + exceptionMsg;
	}

	public static String noClassInvariantError(String className, String invariantName) {
		return "Class " + inQuotes(className) + " has no invariant " + inQuotes(invariantName);
	}

	public static String noClassError(String className) {
		return "Class " + inQuotes(className) + " does not exist";
	}

	public static String aggregationcyclefreenessInfo() {
		String status = DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS ? "on" : "off";
		return "Only on and off are possible values of aggregationcyclefreeness! Using default " + inQuotes(status) + ".";
	}

	public static String forbiddensharingInfo() {
		String status = DefaultConfigurationValues.FORBIDDENSHARING ? "on" : "off";
		return "Only on and off are possible values of forbiddensharing! Using default " + inQuotes(status) + ".";
	}
	
	public static String invariantConfigWarning(String state) {
		return state + " is not possible for an invariant. Possible states for an invariant are "
				+ inQuotes("active") + ", " + inQuotes("inactive") + " and "
				+ inQuotes("negate") + ". Using default " + inQuotes("active") + ".";
	}

	public static String showSolution(int index) {
		return "Show solution " + index;
	}

	public static String showSolutionIndexToBig(int index) {
		return "There are only " + index + " solutions.";
	}

	public static String sizeConfigWarning(String name) {
		return "The value for " + name + " is not a number! The default value will be used.";
	}

	public static String setSyntaxConfigError(String syntax) {
		return "Not the correct syntax. Use the syntax " + syntax + ".";
	}

	public static String complexElementConfigError(String className) {
		return "Only defined objects of class " + className
				+ " can be used. Use the specific class objects or the objects defined by the minimum object size.";
	}

	public static String unexpectedInvariantResult(IInvariant invariant) {
		String message;
		if(invariant.isNegated()){
			message = "Negated invariant %s is not fulfilled in generated system state.";
		}
		else {
			message = "Invariant %s is not fulfilled in generated system state.";
		}
		return String.format(message, inQuotes(invariant.qualifiedName()));
	}

	public static String flagChangeInfo(IInvariant inv, boolean didDisable) {
		String message;
		if(didDisable){
			message = "Overwrite property configuration with generator configuration disabling invariant %s.";
		}
		else {
			message = "Overwrite property configuration with generator configuration negating invariant %s.";
		}
		return String.format(message, inQuotes(inv.qualifiedName()));
	}

}
