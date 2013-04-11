#pragma once

#include <metahost.h>
#include "../Common/CommonTypes.h"
#include "../Common/CLRDebugCallback.h"
#include "../Common/DebugBuffer.h"
#include "../Common/InfoBoard.h"

class CLRDebugCore
{
public:
  CLRDebugCore();
  CLRDebugCore(DWORD pid);
  virtual ~CLRDebugCore();

  void initializeProcessesByPid(DWORD pid);
private:
  DWORD pid;
  HANDLE hProcess;
  ICLRMetaHost* pMetaHost;
  ICorDebug* pCorDebug;
  ICLRRuntimeInfo* pRuntimeInfo;
  IEnumUnknown* pEnum;
  IUnknown* pUnk;
  ICorDebugProcess5* pDebugProcess5;
  ICorDebugProcess* pDebugProcess;

  void initializeProcessesByPid();
};
