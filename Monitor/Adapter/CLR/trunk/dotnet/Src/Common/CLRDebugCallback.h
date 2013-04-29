#pragma once

#include <iostream>
#include "../Common/DefaultCallback.h"
#include "../Common/DebugBuffer.h"
#include "../Common/Settings.h"
#include "../Common/TypeInfoHelper.h"

class CLRDebugCallback : public DefaultCallback
{
public:
  CLRDebugCallback(TypeInfoHelper& typeInfoHelper);

  COM_METHOD CreateAppDomain(ICorDebugProcess* pProcess, ICorDebugAppDomain* pAppDomain);
  COM_METHOD LoadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule);
  COM_METHOD UnloadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule);

private:
  TypeInfoHelper& typeInfoHelper;
};
