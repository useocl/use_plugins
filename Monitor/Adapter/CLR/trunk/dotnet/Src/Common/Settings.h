/** 
* @file Settings.h
* This file declares the singleton class Settings. It reads the configuration file and
* provides its data as global attributes.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/
#pragma once

#include "../Common/InfoBoard.h"
#include "../Tools/pugixml/pugixml.hpp"

class Settings
{
public:
  /** 
  * The static method returns the singleton instance of Settings.
  * @returns The singleton instance of Settings.
  */
  static Settings* theInstance();

  /**
  * The method clears used data structures.
  */
  void release();

  bool InMemoryInstanceMap;               /**< Should CLR heap objects be cached? */
  bool CacheAtStartUp;                    /**< Should all desired instances be cached at startup?  */
  unsigned int MinNumberOfModules;        /**< Number of modules that declares the adapter as initialized. */

  bool DebuggerPrintSettings;             /**< Should the debugger print the settings? */
  bool DebuggerPrintAllModules;           /**< Should the debugger print all modules? */
  bool DebuggerPrintLoadedModules;        /**< Should the debugger print the loaded modules? */
  bool DebuggerPrintLoadedTypes;          /**< Should the debugger print the loaded types? */
  bool DebuggerPrintLoadedTypeFields;     /**< Should the debugger print the fields of the loaded types? */
  bool DebuggerPrintInheritance;          /**< Should the debugger print the inheritance hierarchy? */
  bool DebuggerDebugFamilyLines;          /**< Should the debugger print debug information about the tool family lines? */

  CStringSet TypesOfInterest;             /**< The configured typs of interest. */
  CStringSet ModulesToIgnore;             /**< The configured modules to ignore. */

private:
  static Settings* instance;              /**< The singleton instance of Settings. */

  /** 
  * The private default constructor.
  */
  Settings();

  /** 
  * The private destructor.
  */
  virtual ~Settings();

  /** 
  * The method reads the configuration file.
  */
  void readSettings();
};
