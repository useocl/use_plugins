#pragma once

#include "../Common/CommonTypes.h"
#include "../Common/DebugBuffer.h"
#include "../Common/CLRType.h"
#include "../Common/CLRObject.h"

class InfoBoard
{
public:
  static InfoBoard* theInstance();
  void release();

  ICorDebugProcess* pDebugProcess;
  ICorDebugProcess5* pDebugProcess5;
  
  CStringSet typesOfInterest;
  ModuleSet loadedModules;
  TypeMap loadedTypes;
  ObjectMap currentObjects;

  AppType appType;


  void PrintLoadedTypes(bool instances, bool fields);
  void PrintLoadedModules();
  void PrintObjects(bool fields);

private:
  static InfoBoard* instance;

  InfoBoard();
  virtual ~InfoBoard();
};
