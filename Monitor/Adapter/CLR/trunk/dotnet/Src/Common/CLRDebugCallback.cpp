#include "../Common/CLRDebugCallback.h"

CLRDebugCallback::CLRDebugCallback() : DefaultCallback() { }

COM_METHOD CLRDebugCallback::CreateAppDomain(ICorDebugProcess* pProcess, ICorDebugAppDomain* pAppDomain)
{
  pAppDomain->Attach();
  DebugBuffer name(255);
  pAppDomain->GetName(name.size, (ULONG32*)&name.size, name.buffer);

  if(InfoBoard::theInstance()->appType == DEBUGGER)
    wprintf(L"CreateAppDomain %s\n", name.buffer);

  pProcess->Continue(FALSE);
  return S_OK;
}

COM_METHOD CLRDebugCallback::LoadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule)
{
  DebugBuffer name(_MAX_PATH);
  pModule->GetName(name.size, (ULONG32*)&name.size, name.buffer);
  InfoBoard::theInstance()->loadedModules.insert(pModule);

  pModule->EnableClassLoadCallbacks(TRUE);
  pAppDomain->Continue(FALSE);
  return S_OK;
}

COM_METHOD CLRDebugCallback::UnloadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule)
{
  InfoBoard::theInstance()->loadedModules.erase(pModule);

  pAppDomain->Continue(FALSE);
  return S_OK;
}
