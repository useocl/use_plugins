/** 
* @file CLRDebugCallback.cpp
* This file implements the CLRDebugCallback, which is derived from DefaultCallback.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/CLRDebugCallback.h"

CLRDebugCallback::CLRDebugCallback(TypeInfoHelper& typeInfoHelper) : DefaultCallback(),
  typeInfoHelper(typeInfoHelper)
{ }

COM_METHOD CLRDebugCallback::CreateAppDomain(ICorDebugProcess* pProcess, ICorDebugAppDomain* pAppDomain)
{
  pAppDomain->Attach();
  DebugBuffer name(_MAX_PATH);
  pAppDomain->GetName(name.size, (ULONG32*)&name.size, name.buffer);

  if(InfoBoard::theInstance()->AppType == DEBUGGER)
    wprintf(L"CreateAppDomain %s\n", name.buffer);

  pProcess->Continue(FALSE);
  return S_OK;
}

COM_METHOD CLRDebugCallback::LoadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule)
{
  DebugBuffer name(_MAX_PATH);
  pModule->GetName(name.size, (ULONG32*)&name.size, name.buffer);

  bool ignore = false;

  for(CStringSet::const_iterator it = Settings::theInstance()->ModulesToIgnore.begin(); it != Settings::theInstance()->ModulesToIgnore.end(); ++it) 
  {
    if(name.ToCString().Right((*it).GetLength()) == *it)
    {
      ignore = true;
      break;
    }
  }

  if(!ignore)
    typeInfoHelper.AddModule(pModule);

  if(InfoBoard::theInstance()->AppType == DEBUGGER && Settings::theInstance()->DebuggerPrintAllModules)
    wprintf(L"LoadModule %s. Interesting: %s\n", name.buffer, ignore ? L"false" : L"true");

  pModule->EnableClassLoadCallbacks(TRUE);
  pAppDomain->Continue(FALSE);
  return S_OK;
}

COM_METHOD CLRDebugCallback::UnloadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule)
{
  typeInfoHelper.RemoveModule(pModule);

  pAppDomain->Continue(FALSE);
  return S_OK;
}
