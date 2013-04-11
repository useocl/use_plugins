#include "../Common/InfoBoard.h"

InfoBoard::InfoBoard() : 
  pDebugProcess(NULL),
  pDebugProcess5(NULL),
  appType(ADAPTER),
  loadedModules(ModuleSet()),
  loadedTypes(TypeMap()),
  typesOfInterest(CStringSet()),
  currentObjects(ObjectMap())
{ }

InfoBoard::~InfoBoard()
{
  if(pDebugProcess)
    pDebugProcess = NULL;

  if(pDebugProcess5)
    pDebugProcess5 = NULL;

  loadedModules.clear();
  loadedTypes.clear();
  currentObjects.clear();
  typesOfInterest.clear();
}

InfoBoard* InfoBoard::instance = 0;

InfoBoard* InfoBoard::theInstance()
{
  if(!InfoBoard::instance)
    InfoBoard::instance = new InfoBoard();
  return InfoBoard::instance;
}

void InfoBoard::release()
{
  this->~InfoBoard();
}

void InfoBoard::PrintLoadedModules()
{
  wprintf(L"Loaded modul count: %d", this->loadedModules.size());
  for(ModuleSet::const_iterator it = this->loadedModules.begin(); it != this->loadedModules.end(); ++it) 
  {
    DebugBuffer name(_MAX_PATH);
    (ICorDebugModule*)(*it)->GetName(name.size, (ULONG32*)&name.size, name.buffer);
    wprintf(L"LoadModule %s\n", name.buffer);
  }
}

void InfoBoard::PrintLoadedTypes(bool instances, bool fields)
{
  wprintf(L"Loaded type count: %d\n",this->loadedTypes.size());
  for(TypeMap::const_iterator it = this->loadedTypes.begin(); it != this->loadedTypes.end(); ++it) 
  {
    (*it).second->Print(instances, fields);
  }
}

void InfoBoard::PrintObjects(bool fields)
{
  wprintf(L"Loaded object count: %d\n",this->currentObjects.size());
  for(ObjectMap::const_iterator it = this->currentObjects.begin(); it != this->currentObjects.end(); ++it) 
  {
    (*it).second->Print(fields);
  }
}
