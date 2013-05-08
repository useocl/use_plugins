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

  CLRDebugCore::theInstance()->pDebugProcess->Stop(0);
  CLRDebugCore::theInstance()->pDebugProcess->Detach();
  CLRDebugCore::theInstance()->Release();

  return 0;
}
