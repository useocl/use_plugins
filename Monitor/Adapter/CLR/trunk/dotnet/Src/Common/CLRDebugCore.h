#pragma once

#include <metahost.h>
#include "../Common/CommonTypes.h"
#include "../Common/DefaultCallback.h"
#include "../Common/DebugBuffer.h"
#include "../Common/InfoBoard.h"

class CLRDebugCore
{
public:
  static CLRDebugCore* theInstance();

  ICorDebugProcess5* pDebugProcess5;
  ICorDebugProcess* pDebugProcess;

  void InitializeProcessesByPid(DWORD pid, DefaultCallback* callback);
  void Release();

private:
  static CLRDebugCore* instance;

  CLRDebugCore();
  virtual ~CLRDebugCore();

  DWORD pid;
  DefaultCallback* callback;
  HANDLE hProcess;
  ICLRMetaHost* pMetaHost;
  ICorDebug* pCorDebug;
  ICLRRuntimeInfo* pRuntimeInfo;
  IEnumUnknown* pEnum;
  IUnknown* pUnk;

  void initializeProcessesByPid();
};
