#pragma once

#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include "../Common/CLRDebugCore.h"
#include "../Common/HeapInfoHelper.h"
#include "../Common/CommonTypes.h"
#include "../Common/InfoBoard.h"
#include "../Common/CLRType.h"
#include "../Common/CLRMetaField.h"

int main(int argc, const char* argv[])
{
  if(!argv[1])
  {
    std::cout << "PID is missing!" << std::endl;
    return -1;
  }

  // set app type
  InfoBoard::theInstance()->appType = DEBUGGER;

  //set debuggee types of interest
  InfoBoard::theInstance()->typesOfInterest.insert(L"Debuggee.Cat");
  InfoBoard::theInstance()->typesOfInterest.insert(L"Debuggee.Dog");
  InfoBoard::theInstance()->typesOfInterest.insert(L"Debuggee.PetColor");

  long pid = std::atol(argv[1]);

  CLRDebugCore debug((DWORD)pid);

  system("PAUSE");

  InfoBoard::theInstance()->PrintLoadedModules();

  system("PAUSE");

  InfoBoard::theInstance()->pDebugProcess->Stop(0);

  HeapInfoHelper heapHelper;
  heapHelper.iterateOverHeap();

  system("PAUSE");

  InfoBoard::theInstance()->PrintLoadedTypes(false, true);

  system("PAUSE");

  InfoBoard::theInstance()->PrintObjects(true);

  system("PAUSE");

  // Test getting meta field information by name
  TypeMap::const_iterator gotType = InfoBoard::theInstance()->loadedTypes.find(_T("Debuggee.Dog"));
  if(gotType != InfoBoard::theInstance()->loadedTypes.end())
  {
    CLRMetaField* f = (*gotType).second->GetFieldByName(_T("Name"));
    if(!f)
      std::cout << "Can not find field!" << std::endl;
  }

  system("PAUSE");

  InfoBoard::theInstance()->pDebugProcess->Detach();
  InfoBoard::theInstance()->release();

  return 0;
}
