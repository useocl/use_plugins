/** 
* @file CLRDebugger.cpp
* This file implements the main class of the CLRDebugger.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/
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
  std::wcout << L"\n" << std::endl;

  if(Settings::theInstance()->DebuggerPrintSettings)
  {
    std::wcout << L"Current Settings:" << std::endl;
    std::wcout << L"-------------------------------------------" << std::endl;
    std::wcout << L"Setting InMemoryInstanceMap: " << Settings::theInstance()->InMemoryInstanceMap << std::endl;
    std::wcout << L"Setting MinNumberOfModules: "  << Settings::theInstance()->MinNumberOfModules  << std::endl;
    std::wcout << L"Setting CacheAtStartUp: "  << Settings::theInstance()->CacheAtStartUp  << std::endl;
    system("PAUSE");
    std::wcout << L"\n" << std::endl;
  }

  if(Settings::theInstance()->DebuggerPrintLoadedModules)
  {
    std::wcout << L"Loaded Modules:" << std::endl;
    std::wcout << L"-------------------------------------------" << std::endl;
    typeInfoHelper.PrintLoadedModules();
    system("PAUSE");
    std::wcout << L"\n" << std::endl;
  }

  if(Settings::theInstance()->DebuggerPrintLoadedTypes)
  {
    std::wcout << L"Loaded Types:" << std::endl;
    std::wcout << L"-------------------------------------------" << std::endl;
    typeInfoHelper.PrintLoadedTypes(Settings::theInstance()->DebuggerPrintLoadedTypeFields);
    system("PAUSE");
    std::wcout << L"\n" << std::endl;
  }

  if(Settings::theInstance()->DebuggerPrintInheritance)
  {
    std::wcout << L"Inheritance information:" << std::endl;
    std::wcout << L"-------------------------------------------" << std::endl;
    typeInfoHelper.PrintSimpleInheritance();
    system("PAUSE");
    std::wcout << L"\n" << std::endl;
  }

  if(Settings::theInstance()->DebuggerDebugFamilyLines)
  {
    CLRDebugCore::theInstance()->pDebugProcess->Stop(0);
    ObjectInfoHelper o(typeInfoHelper);
    CLRType* tempType = typeInfoHelper.GetType(CString(_T("KBS.FamilyLinesLib.Person")));
    o.GetInstances(tempType);

    typeInfoHelper.GetTypeInfo(tempType->typeAttr);

    system("PAUSE");

    tempType->Print(true);
    CLRMetaField* fieldFName = tempType->GetField(L"firstName");
    wprintf(L"Find meta field: %s\n", fieldFName->name);
    CLRMetaField* fieldLName = tempType->GetField(L"lastName");
    wprintf(L"Find meta field: %s\n", fieldLName->name);

    std::vector<CORDB_ADDRESS> instances = tempType->instances;
    for(std::vector<CORDB_ADDRESS>::const_iterator it = instances.begin(); it != instances.end(); ++it)
    {
      CLRObject* res = o.GetCLRObject(*it);
      wprintf(L"Loaded instance of type: %s\n", res->name);
      wprintf(L"\t Address: %d\n", res->address);
      wprintf(L"\t Vorname: %s", ((CLRFieldValue*)o.GetField(tempType, *it, fieldFName->fieldDef))->valueAsString);
      wprintf(L"\t Nachname: %s\n", ((CLRFieldValue*)o.GetField(tempType, *it, fieldLName->fieldDef))->valueAsString);
    }
    o.Detach();
  }

  system("PAUSE");

  CLRDebugCore::theInstance()->pDebugProcess->Stop(0);
  CLRDebugCore::theInstance()->pDebugProcess->Detach();

  typeInfoHelper.Detach();

  CLRDebugCore::theInstance()->Release();

  return 0;
}
