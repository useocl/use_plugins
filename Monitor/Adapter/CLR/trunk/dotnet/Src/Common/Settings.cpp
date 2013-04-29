#include "../Common/Settings.h"

Settings::Settings() :
  InMemoryInstanceMap(false),
  CompareTypeNames(false),
  typesOfInterest(CStringSet()),
  modulesToIgnore(CStringSet())
{
  readSettings();
}

Settings::~Settings()
{
  typesOfInterest.clear();
  modulesToIgnore.clear();
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
    std::cerr << "Settings: " <<  res.description() << std::endl;

  InMemoryInstanceMap = doc.child("Settings").child("ObjectInfoHelper").attribute("InMemoryInstanceMap").as_bool();
  CompareTypeNames    = doc.child("Settings").child("ObjectInfoHelper").attribute("CompareTypeNames").as_bool();

  pugi::xml_node types = doc.child("Settings").child("TypesOfInterest");
  for (pugi::xml_node type = types.child("TypeOfInterest"); type; type = type.next_sibling("TypeOfInterest"))
  {
    typesOfInterest.insert(type.attribute("Name").as_string());
  }

  pugi::xml_node modules = doc.child("Settings").child("ModulesToIgnore");
  for (pugi::xml_node module = modules.child("ModuleToIgnore"); module; module = module.next_sibling("ModuleToIgnore"))
  {
    modulesToIgnore.insert(module.attribute("Name").as_string());
  }
}
