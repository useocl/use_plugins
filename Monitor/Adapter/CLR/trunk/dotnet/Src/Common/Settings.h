#pragma once

#include "../Common/InfoBoard.h"
#include "../Tools/pugixml/pugixml.hpp"

class Settings
{
public:
  static Settings* theInstance();
  void release();

  bool InMemoryInstanceMap;
  bool CompareTypeNames;

  CStringSet typesOfInterest;
  CStringSet modulesToIgnore;

private:
  static Settings* instance;

  Settings();
  virtual ~Settings();

  void readSettings();
};
