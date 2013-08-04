/** 
* @file CLRDebugCallback.h
* This file declares the CLRDebugCallback, which is derived from DefaultCallback.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include <iostream>
#include "../Common/DefaultCallback.h"
#include "../Common/DebugBuffer.h"
#include "../Common/Settings.h"
#include "../Common/TypeInfoHelper.h"

class CLRDebugCallback : public DefaultCallback
{
public:
  /** 
  * Constructor.
  * @param typeInfoHelper The instance of the TypeInfoHelper to update the currently loaded modules.
  */
  CLRDebugCallback(TypeInfoHelper& typeInfoHelper);

  /** 
  * The callback method that notifies about loaded AppDomains.
  */
  COM_METHOD CreateAppDomain(ICorDebugProcess* pProcess, ICorDebugAppDomain* pAppDomain);

  /** 
  * The callback method that notifies about loaded Modules.
  * If a new module is loaded the TypeInfoHelper will be updated.
  */
  COM_METHOD LoadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule);

  /** 
  * The callback method that notifies about unloaded Modules.
  * If a module is unloaded the TypeInfoHelper will be updated.
  */
  COM_METHOD UnloadModule(ICorDebugAppDomain* pAppDomain, ICorDebugModule* pModule);

private:
  TypeInfoHelper& typeInfoHelper; /**< The instance of the TypeInfoHelper to update the currently loaded modules. */
};
