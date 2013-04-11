#define COM_METHOD  HRESULT STDMETHODCALLTYPE

#pragma once

#pragma warning(disable: 4100)  /* unreferenced formal parameter */

#include "../Common/CommonTypes.h"

class DefaultCallback : public ICorDebugManagedCallback, public ICorDebugManagedCallback2
{
private:
  ULONG refCount;

public:
  DefaultCallback() : refCount(0) { }

  virtual ~DefaultCallback() { }

  // 
  // IUnknown
  //
  ULONG STDMETHODCALLTYPE AddRef() 
  {
    return (InterlockedIncrement((long*) &refCount));
  }

  ULONG STDMETHODCALLTYPE Release() 
  {
    long refCount = InterlockedDecrement(&refCount);
    if(refCount == 0)
      delete this;

    return (refCount);
  }

  COM_METHOD QueryInterface(REFIID riid, void** ppInterface)
  {
    if(riid == IID_IUnknown)
      *ppInterface = (IUnknown*)(ICorDebugManagedCallback*) this;
    else if(riid == IID_ICorDebugManagedCallback)
      *ppInterface = (ICorDebugManagedCallback*) this;
    else if(riid == IID_ICorDebugManagedCallback2)
      *ppInterface = (ICorDebugManagedCallback2*) this;
    else
      return (E_NOINTERFACE);

    this->AddRef();
    return (S_OK);
  }

  //
  // Implementation of ICorDebugManagedCallback
  //
  COM_METHOD Breakpoint(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugBreakpoint* pBreakpoint)
  {
    return E_NOTIMPL;
  }

  COM_METHOD StepComplete(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugStepper* pStepper,
    CorDebugStepReason reason)
  {
    return E_NOTIMPL;
  }
  COM_METHOD Break(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* thread)
  {
    return E_NOTIMPL;
  }

  COM_METHOD Exception(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    BOOL unhandled)
  {
    return E_NOTIMPL;
  }

  COM_METHOD EvalComplete(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugEval* pEval)
  {
    return E_NOTIMPL;
  }

  COM_METHOD EvalException(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugEval* pEval)
  {
    return E_NOTIMPL;
  }

  COM_METHOD CreateProcess(ICorDebugProcess* pProcess)
  {
    return E_NOTIMPL;
  }

  COM_METHOD ExitProcess(ICorDebugProcess* pProcess)
  {
    return E_NOTIMPL;
  }

  COM_METHOD CreateThread(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* thread)
  {
    return E_NOTIMPL;
  }

  COM_METHOD ExitThread(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* thread)
  {
    return E_NOTIMPL;
  }

  COM_METHOD LoadModule( ICorDebugAppDomain* pAppDomain,
    ICorDebugModule* pModule)
  {
    return E_NOTIMPL;
  }

  COM_METHOD UnloadModule(ICorDebugAppDomain* pAppDomain,
    ICorDebugModule* pModule)
  {
    return E_NOTIMPL;
  }

  COM_METHOD LoadClass(ICorDebugAppDomain* pAppDomain,
    ICorDebugClass* c)  
  {
    return E_NOTIMPL;
  }

  COM_METHOD UnloadClass(ICorDebugAppDomain* pAppDomain,
    ICorDebugClass* c)
  {
    return E_NOTIMPL;
  }

  COM_METHOD DebuggerError(ICorDebugProcess* pProcess,
    HRESULT errorHR,
    DWORD errorCode)
  {
    return E_NOTIMPL;
  }

  COM_METHOD LogMessage(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    LONG lLevel,
    WCHAR* pLogSwitchName,
    WCHAR* pMessage)
  {
    return E_NOTIMPL;
  }

  COM_METHOD LogSwitch(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    LONG lLevel,
    ULONG ulReason,
    WCHAR* pLogSwitchName,
    WCHAR* pParentName)
  {
    return E_NOTIMPL;
  }

  COM_METHOD CreateAppDomain(ICorDebugProcess* pProcess,
    ICorDebugAppDomain* pAppDomain)
  {
    return E_NOTIMPL;
  }

  COM_METHOD ExitAppDomain(ICorDebugProcess* pProcess,
    ICorDebugAppDomain* pAppDomain)
  {
    return E_NOTIMPL;
  }

  COM_METHOD LoadAssembly(ICorDebugAppDomain* pAppDomain,
    ICorDebugAssembly* pAssembly)
  {
    return E_NOTIMPL;
  }

  COM_METHOD UnloadAssembly(ICorDebugAppDomain* pAppDomain,
    ICorDebugAssembly* pAssembly)
  {
    return E_NOTIMPL;
  }

  COM_METHOD ControlCTrap(ICorDebugProcess* pProcess)
  {
    return E_NOTIMPL;
  }

  COM_METHOD NameChange(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread)
  {
    return E_NOTIMPL;
  }

  COM_METHOD UpdateModuleSymbols(ICorDebugAppDomain* pAppDomain,
    ICorDebugModule* pModule,
    IStream* pSymbolStream)
  {
    return E_NOTIMPL;
  }

  COM_METHOD EditAndContinueRemap(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugFunction* pFunction,
    BOOL fAccurate)
  {
    return E_NOTIMPL;
  }

  COM_METHOD BreakpointSetError(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugBreakpoint* pBreakpoint,
    DWORD dwError)
  {
    return E_NOTIMPL;
  }

  ///
  /// Implementation of ICorDebugManagedCallback2
  ///
  COM_METHOD FunctionRemapOpportunity(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugFunction* pOldFunction,
    ICorDebugFunction* pNewFunction,
    ULONG32 oldILOffset)
  {
    return E_NOTIMPL;
  }

  COM_METHOD CreateConnection(ICorDebugProcess* pProcess,
    CONNID dwConnectionId,
    WCHAR* pConnName)
  {
    return E_NOTIMPL;
  }

  COM_METHOD ChangeConnection(ICorDebugProcess* pProcess,
    CONNID dwConnectionId)
  {
    return E_NOTIMPL;
  }

  COM_METHOD DestroyConnection(ICorDebugProcess* pProcess,
    CONNID dwConnectionId)
  {
    return E_NOTIMPL;
  }

  COM_METHOD Exception(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugFrame* pFrame,
    ULONG32 nOffset,
    CorDebugExceptionCallbackType dwEventType,
    DWORD dwFlags)
  {
    return E_NOTIMPL;
  }

  COM_METHOD ExceptionUnwind(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    CorDebugExceptionUnwindCallbackType dwEventType,
    DWORD dwFlags)
  {
    return E_NOTIMPL;
  }

  COM_METHOD FunctionRemapComplete(ICorDebugAppDomain* pAppDomain,
    ICorDebugThread* pThread,
    ICorDebugFunction* pFunction)
  {
    return E_NOTIMPL;
  }

  COM_METHOD MDANotification(
    ICorDebugController*  pController,
    ICorDebugThread* pThread,
    ICorDebugMDA*  pMDA
    )
  {
    return E_NOTIMPL;
  }
};
