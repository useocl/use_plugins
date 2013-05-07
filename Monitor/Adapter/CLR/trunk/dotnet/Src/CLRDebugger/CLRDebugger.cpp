#pragma once

#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include "../Common/CLRDebugCore.h"
#include "../Common/CommonTypes.h"
#include "../Common/InfoBoard.h"
#include "../Common/CLRType.h"
#include "../Common/CLRMetaField.h"
#include "../Common/Settings.h"
#include "../Common/TypeInfoHelper.h"
#include "../Common/ObjectInfoHelper.h"
#include "../Common/CLRDebugCallback.h"


int main(int argc, const char* argv[])
{
  if(!argv[1])
  {
    std::cout << "PID is missing!" << std::endl;
    return -1;
  }

  // set app type
  InfoBoard::theInstance()->AppType = DEBUGGER;

  long pid = std::atol(argv[1]);

  TypeInfoHelper typeInfoHelper;

  CLRDebugCore::theInstance()->InitializeProcessesByPid((DWORD)pid, new CLRDebugCallback(typeInfoHelper));

  system("PAUSE");

  std::wcout << L"Setting InMemoryInstanceMap: " << Settings::theInstance()->InMemoryInstanceMap << std::endl;
  std::wcout << L"Setting MinNumberOfModules: "  << Settings::theInstance()->MinNumberOfModules  << std::endl;

  system("PAUSE");

  typeInfoHelper.PrintLoadedModules();

  system("PAUSE");

  typeInfoHelper.PrintLoadedTypes(true);

  system("PAUSE");

  typeInfoHelper.PrintSimpleInheritance();

  system("PAUSE");

  CLRDebugCore::theInstance()->pDebugProcess->Stop(0);
  ObjectInfoHelper o;
  o.GetInstances(typeInfoHelper.GetType(CString(_T("Debuggee.Cat"))));
  o.GetInstances(typeInfoHelper.GetType(CString(_T("Debuggee.Dog"))));
  o.GetInstances(typeInfoHelper.GetType(CString(_T("Debuggee.PetColor"))));
  o.GetInstances(typeInfoHelper.GetType(CString(_T("Debuggee.Cat"))));

  typeInfoHelper.GetType(CString(_T("Debuggee.Cat")))->PrintInstances();
  typeInfoHelper.GetType(CString(_T("Debuggee.Dog")))->PrintInstances();
  typeInfoHelper.GetType(CString(_T("Debuggee.PetColor")))->PrintInstances();

  system("PAUSE");

  const CLRType* testType = typeInfoHelper.GetType(CString(_T("Debuggee.Dog")));
  const ObjectVector instances =  testType->instances;
  for(ObjectVector::const_iterator it = instances.begin(); it != instances.end(); ++it)
  {
    CLRObject * res = o.GetCLRObject((*it)->address);
    wprintf(L"Loaded instance of type: %s\n", res->name);
    wprintf(L"\t Addresses: %d", res->address);
    wprintf(L" : %d\n", (*it)->address);
  }

  system("PAUSE");

  for(ObjectVector::const_iterator it = instances.begin(); it != instances.end(); ++it)
  {
    CLRMetaField* mField = testType->GetField(_T("ArrayChildren"));
    if(mField)
    {
      CLRFieldBase* res = o.GetField(testType, (*it)->address, mField->fieldDef);
      if(res)
        wprintf(L"Array done!\n");
    }
  }

  system("PAUSE");

  //InfoBoard::theInstance()->pDebugProcess->Stop(0);

  //HeapInfoHelper heapHelper;
  //heapHelper.iterateOverHeap();

  //system("PAUSE");

  //InfoBoard::theInstance()->PrintLoadedTypes(false, true);

  //system("PAUSE");

  //InfoBoard::theInstance()->PrintObjects(true);

  //system("PAUSE");

  //// Test getting meta field information by name
  //TypeMap::const_iterator gotType = InfoBoard::theInstance()->loadedTypes.find(_T("Debuggee.Dog"));
  //if(gotType != InfoBoard::theInstance()->loadedTypes.end())
  //{
  //  CLRMetaField* f = (*gotType).second->GetFieldByName(_T("Name"));
  //  if(!f)
  //    std::cout << "Can not find field!" << std::endl;
  //}

  //system("PAUSE");
  CLRDebugCore::theInstance()->pDebugProcess->Stop(0);
  CLRDebugCore::theInstance()->pDebugProcess->Detach();
  CLRDebugCore::theInstance()->Release();

  return 0;
}
