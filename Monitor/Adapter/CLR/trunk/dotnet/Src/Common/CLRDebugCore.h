/** 
* @file CLRDebugCore.h
* This file declares the singleton class CLRDebugCore. It is responsible for the connection to the 
* debuggee, to control it, and to get the CLR interfaces used to analyze the debuggee.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include <metahost.h>
#include "../Common/CommonTypes.h"
#include "../Common/DefaultCallback.h"
#include "../Common/DebugBuffer.h"
#include "../Common/InfoBoard.h"

class CLRDebugCore
{
public:
  /** 
  * The static method returns the singleton instance of CLRDebugCore.
  * @returns The singleton instance of CLRDebugCore.
  */
  static CLRDebugCore* theInstance();

  ICorDebugProcess5* pDebugProcess5; /**< An interface to get heap information. */
  ICorDebugProcess* pDebugProcess;   /**< An interface to control the debuggee. */

  /** 
  * The method connects this instance with the debugge and initializes the CLR interfaces.
  * @param pid The process ID of the desired debuggee process.
  * @param callback A callback handler to process the notifications.
  */
  void InitializeProcessesByPid(DWORD pid, DefaultCallback* callback);

  /** 
  * The method releases the CLR interfaces.
  * Should be called after detaching from debuggee is completed.
  */
  void Release();

private:
  static CLRDebugCore* instance;  /**< The singleton instance of CLRDebugCore. */

  HANDLE hProcess;                /**< The process handle of the debuggee. */
  ICLRMetaHost* pMetaHost;        /**< An interface to get the enumerator of loaded runtimes. */
  ICorDebug* pCorDebug;           /**< An interface to get the specialized CLR interfaces. */
  ICLRRuntimeInfo* pRuntimeInfo;  /**< An interfacce to get the core debug interface. */
  IEnumUnknown* pEnum;            /**< An enumerator. */
  IUnknown* pUnk;                 /**< An interface to get the runtime information. */

  /** 
  * The private default constructor.
  */
  CLRDebugCore();

  /** 
  * The virtual destructor.
  */
  virtual ~CLRDebugCore();

};
