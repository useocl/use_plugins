#pragma once

#include "../Common/InfoBoard.h"
#include "../Tools/pugixml/pugixml.hpp"

class Settings
{
public:
  static Settings* theInstance();
  void release();

  bool InMemoryInstanceMap;
  bool CacheAtStartUp;
  unsigned int MinNumberOfModules;

  bool DebuggerPrintSettings;
  bool DebuggerPrintAllModules;
  bool DebuggerPrintLoadedModules;
  bool DebuggerPrintLoadedTypes;
  bool DebuggerPrintLoadedTypeFields;
  bool DebuggerPrintInheritance;

  CStringSet TypesOfInterest;
  CStringSet ModulesToIgnore;

private:
  static Settings* instance;

  Settings();
  virtual ~Settings();

  void readSettings();
};
