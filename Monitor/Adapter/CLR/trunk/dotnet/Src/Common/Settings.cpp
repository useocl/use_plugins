/** 
* @file Settings.cpp
* This file impements the singleton class Settings. It reads the configuration file and
* provides its data as global attributes.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/Settings.h"

Settings::Settings() :
  InMemoryInstanceMap(false),
  CacheAtStartUp(false),
  MinNumberOfModules(0),
  DebuggerPrintSettings(false),
  DebuggerPrintAllModules(false),
  DebuggerPrintLoadedModules(false),
  DebuggerPrintLoadedTypes(false),
  DebuggerPrintLoadedTypeFields(false),
  DebuggerPrintInheritance(false),
  DebuggerDebugFamilyLines(false),
  TypesOfInterest(CStringSet()),
  ModulesToIgnore(CStringSet())
{
  readSettings();
}

Settings::~Settings()
{
  TypesOfInterest.clear();
  ModulesToIgnore.clear();
}

Settings* Settings::instance = 0;

Settings* Settings::theInstance()
{
  if(!Settings::instance)
    Settings::instance = new Settings();
  return Settings::instance;
}

void Settings::release()
{
  this->~Settings();
}

void Settings::readSettings()
{
  pugi::xml_document doc;
  pugi::xml_parse_result res;
  
  if(InfoBoard::theInstance()->AppType == ADAPTER)
    res = doc.load_file(_T("lib/plugins/monitor_adapter/clr_adapter_settings.xml"));
  else
    res = doc.load_file(_T("../../Doc/clr_adapter_settings.xml"));

  if(!res)
    std::cerr << _T("Settings: ") <<  res.description() << std::endl;

  MinNumberOfModules   = doc.child("Settings").child("TypeInfoHelper").attribute("MinNumberOfModules").as_uint();
  InMemoryInstanceMap  = doc.child("Settings").child("ObjectInfoHelper").attribute("InMemoryInstanceMap").as_bool();
  CacheAtStartUp       = doc.child("Settings").child("ObjectInfoHelper").attribute("CacheAtStartUp").as_bool();

  pugi::xml_node types = doc.child("Settings").child("TypesOfInterest");
  for (pugi::xml_node type = types.child("TypeOfInterest"); type; type = type.next_sibling("TypeOfInterest"))
  {
    TypesOfInterest.insert(type.attribute("Name").as_string());
  }

  pugi::xml_node modules = doc.child("Settings").child("ModulesToIgnore");
  for (pugi::xml_node module = modules.child("ModuleToIgnore"); module; module = module.next_sibling("ModuleToIgnore"))
  {
    ModulesToIgnore.insert(module.attribute("Name").as_string());
  }

  DebuggerPrintSettings         = doc.child("Settings").child("Debugger").attribute("PrintSettings").as_bool();
  DebuggerPrintAllModules       = doc.child("Settings").child("Debugger").attribute("PrintAllModules").as_bool();
  DebuggerPrintLoadedModules    = doc.child("Settings").child("Debugger").attribute("PrintLoadedModules").as_bool();
  DebuggerPrintLoadedTypes      = doc.child("Settings").child("Debugger").attribute("PrintLoadedTypes").as_bool();
  DebuggerPrintLoadedTypeFields = doc.child("Settings").child("Debugger").attribute("PrintLoadedTypesFields").as_bool();
  DebuggerPrintInheritance      = doc.child("Settings").child("Debugger").attribute("PrintInheritance").as_bool();
  DebuggerDebugFamilyLines      = doc.child("Settings").child("Debugger").attribute("DebugFamilyLines").as_bool();

  // some verification
  if(CacheAtStartUp && !InMemoryInstanceMap)
  {
    InMemoryInstanceMap = true;
    std::cerr << _T("Settings: if CacheAtStartUp is true InMemoryInstanceMap should be true, too.") << std::endl;
  }
}
