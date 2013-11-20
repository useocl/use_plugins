package org.tzi.kodkod.helper;

import java.io.File;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;

import kodkod.engine.Statistics;

/**
 * Contains the different log messages.
 * 
 * @author Hendrik Reitmann
 * 
 */
public final class LogMessages {

	public static final String modelConfigurationSuccessful = "Model configuration successful";

	public static String letNotReachableWarning = "This state should not be reachable!";

	public static String invTransformSuccessful = "Invariant transformation successful";

	public static String modelTransformError = "Model transformation was not successful.";

	public static String modelTransformSuccessful = "Model transformation successful";

	public static String solverConfigBitwidthError = "Bitwidth has to be an int value.";

	public static String configDiagramExtractionError = "Possible values for AutomaticObjectDiagramExtraction are only 'on' or 'off'.";

	public static String objDiagramCreation = "Create object diagram";

	public static String objDiagramCreationError = "Error during object diagram creation";

	public static String propertiesConfigurationReadError = "Error while reading .properties file";

	public static String propertiesConfigurationCreateError = "Error while creating .properties file";

	public static String propertiesConfigurationWriteError = "Error while writing .properties file";

	public static String propertiesConfigurationCloseError = "Error while closing .properties file";

	public static String className$Error = "Class names starting with a '$' are not allowed!";

	public static String solverConfigSaveError = "Error while saving KodkodSolver configuration";

	public static String solverConfigSaved = "Saved KodkodSolver configuration";

	public static String modelResetSuccessful = "Reset successful";

	public static String validationException = "Error while model validation! Please check your configuration.";

	public static String valueConversionNestedCollections = "Nested collections not supported!";

	public static String objDiagramExtractionError = "Error while extracting information from the object diagram.";

	public static String objDiagramExtractionSuccessful = "Object diagram extraction successful.";

	public static String pagingNext = "'next' to search a new solution";

	public static String pagingPrevious = "'previous' to get the last solution";

	public static String pagingFirst = "First solution reached";

	public static String pagingCmdError = "Set a configuration file or enter next for a new solution respectively previous for the last solution.";

	public static String pagingCmdFileFirst = "Set a configuration file first!";

	public static String enrichWithLoadedInvariants = "Enrich the model with loaded invariants";

	public static String errorWithLoadedInvariants = "Error while enriching the model with loaded invariants";

	public static String inactiveInvariantEval = "Evaluation of inactive invariants";

	public static String noSuchMethodError = "Please read the Readme file";

	public static String solutionForbidError = "Error while creating the formula to forbid the last solution!";

	public static final String showSolutionIndexToSmall = "Please enter an index greater 0.";

	public static final String queryEvaluationError = "The query can not be evaluated. Please check if the query functionality is enabled! After the activation re-run the validation.";

	public static final String closureObjectMessage = "Closure is only available for collections with objects.";

	public static final String closureCollectionMessage = "Source type is not a collection.";

	public static String startModelTransform(String name) {
		return "Start model transformation for '" + name + "'";
	}

	public static String constRealWarning(Double value) {
		return "No support for real values. " + value + " becomes " + value.intValue();
	}

	public static String invTransformError(String name) {
		return "Cannot transform invariant '" + name + "'.";
	}

	public static String invTransformSuccessful(String name) {
		return "Transformation of invariant '" + name + "' successful";
	}

	public static String solverConfigWrongArgumentError(String name) {
		return name + " is not configurable. Only SatSolver and bitwidth.";
	}

	public static String solverConfigSyntaxError(String argument) {
		return argument + " has not the correct syntax. Use 'name' := 'value'.";
	}

	public static String invIndepSyntaxError(String argument) {
		return argument + " has not the correct syntax. Use 'class' - 'invariant'.";
	}

	public static String unsupportedCollectionWarning(String collectionName) {
		return "This approach supports no " + collectionName + ". All collections will be handled like sets.";
	}

	public static String differentSymbolOperationMappingsError(String operator, String mapping) {
		return "Different operationnames for one operator are not possible. Mapping of operator " + operator + "is " + mapping + ".";
	}

	public static String doubleSymbolOperationMappingWarning(String operator) {
		return "For operator " + operator + " exists more than one operation. Please make sure, that the implementations have different parameters!";
	}

	public static String kodkodStatistics(Statistics statistics) {
		return "Translation time: " + statistics.translationTime() + " ms; Solving time: " + statistics.solvingTime() + " ms";
	}

	public static String wrongBitwidthWarning(int defaultBitwidth, int newBitwidth) {
		return "The bitwidth has to be a value between 1 and 32. Default bitwidth " + defaultBitwidth + " will be used!";
	}

	public static String newBitwidth(int newBitwidth) {
		return "Set bitwidth to " + newBitwidth + ".";
	}

	public static String newSatSolver(String satFactory) {
		return "Set SatSolver to '" + satFactory + "'.";
	}

	public static String newAutomaticDiagramExtraction(boolean automaticDiagramExtraction) {
		String mode = automaticDiagramExtraction ? "Enable" : "Disable";
		return mode + " automatic extraction of an available object diagram.";
	}

	public static String noSatSolverLibraryError(String satFactory, String defaultSatFacotry) {
		return "Could not load the library for SatSolver '" + satFactory + "'. Using default SatSolver '" + defaultSatFacotry + "'.";
	}

	public static String noSatSolverWarning(String solverName, String defaultSatFacotry) {
		return "No solver '" + solverName + "' available. Using default SatSolver '" + defaultSatFacotry + "'.";
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
		return "Translation time: " + timeMs + " ms";
	}

	public static String solverConfigReadWarning(String defaultSatFactory, int defaultBitwidth) {
		return "Error while reading KodkodSolver configuration. Using default SatSolver '" + defaultSatFactory + "' and default bitwidth "
				+ defaultBitwidth + ".";
	}

	public static String noValueCreation(String type) {
		return "Creation of values for " + type + " not implemented!";
	}

	public static Object searchSolution(String satFactory, int bitwidth) {
		return "Searching solution with SatSolver '" + satFactory + "' and bitwidth " + bitwidth + "...";
	}

	public static String extractSatSolverWarning(String defaultSatFactory) {
		return "Extraction of satsolver libraries failed. Only default SatSolver '" + defaultSatFactory + "' can be used!";
	}

	public static String libraryPathWarning(String defaultSatFactory, String exceptionMsg) {
		return "Only default SatSolver '" + defaultSatFactory + "' can be used! " + exceptionMsg;
	}

	public static String noClassInvariantError(String className, String invariantName) {
		return "Class '" + className + "' has no invariant '" + invariantName + "'";
	}

	public static String noClassError(String className) {
		return "Class '" + className + "' does not exist";
	}

	public static String aggregationcyclefreenessInfo() {
		String status = DefaultConfigurationValues.aggregationcyclefreeness ? "on" : "off";
		return "Only on and off are possible values of aggregationcyclefreeness! Use default: " + status;
	}

	public static String forbiddensharingInfo() {
		String status = DefaultConfigurationValues.forbiddensharing ? "on" : "off";
		return "Only on and off are possible values of forbiddensharing! Use default: " + status;
	}
	
	public static String invariantConfigWarning(String state) {
		return state
				+ " is not possible for an invariant. Possible states for an invariant are 'active', 'inactive' and 'negate'. Use 'active' as default.";
	}

	public static String showSolution(int index) {
		return "Show solution " + index;
	}

	public static String showSolutionIndexToBig(int index) {
		return "There are only " + index + " solutions.";
	}

	public static String sizeConfigWarning(String name, int defaultValue) {
		return "The value for " + name + " is not a number! The default value " + defaultValue + " will be used.";
	}

	public static String setSyntaxConfigError(String syntax) {
		return "Not the correct syntax. Use the syntax " + syntax + ".";
	}

	public static String complexElementConfigError(String className) {
		return "Only defined objects of class " + className
				+ " can be used. Use the specific class objects or the objects defined by the minimum object size.";
	}

}
