#include "../Common/CLRDebugCore.h"

CLRDebugCore::CLRDebugCore() : pid(0),
  hProcess(NULL), 
  pMetaHost(NULL), 
  pCorDebug(NULL), 
  pRuntimeInfo(NULL), 
  pDebugProcess(NULL), 
  pDebugProcess5(NULL),
  pEnum(NULL),
  pUnk(NULL),
  callback(NULL)
{ }

CLRDebugCore* CLRDebugCore::instance = 0;

CLRDebugCore* CLRDebugCore::theInstance()
{
  if(!CLRDebugCore::instance)
    CLRDebugCore::instance = new CLRDebugCore();
  return CLRDebugCore::instance;
}

CLRDebugCore::~CLRDebugCore()
{
  if(pEnum)
  {
    pEnum->Release();
    pEnum = NULL;
  }
  if(pUnk)
  {
    pUnk->Release();
    pUnk = NULL;
  }
  if(pDebugProcess5)
  {
    pDebugProcess5->Release();
    pDebugProcess5 = NULL;
  }
  if(pDebugProcess)
  {
    pDebugProcess->Release();
    pDebugProcess = NULL;
  }
  if(pRuntimeInfo)
  {
    pRuntimeInfo->Release();
    pRuntimeInfo = NULL;
  }
  if(pCorDebug)
  {
    pCorDebug->Release();
    pCorDebug = NULL;
  }
  if(pMetaHost)
  {
    pMetaHost->Release();
    pMetaHost = NULL;
  }
  if(hProcess)
    hProcess = NULL;
}

void CLRDebugCore::InitializeProcessesByPid(DWORD pid, DefaultCallback* callback)
{
  this->pid = pid;
  this->callback = callback;
  this->initializeProcessesByPid();
}

void CLRDebugCore::initializeProcessesByPid()
{
  HRESULT hr = E_FAIL;

  hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);
  if(!hProcess)
  {
    std::wcerr << L"OpenProcess failed!." << std::endl;
    return;
  }

  hr = CLRCreateInstance(CLSID_CLRMetaHost, IID_ICLRMetaHost, (LPVOID*)&pMetaHost);
  if(FAILED(hr))
    wprintf(L"CLRCreateInstance failed w/hr 0x%08lx\n", hr);

  hr = pMetaHost->EnumerateLoadedRuntimes(hProcess, &pEnum);

  if(hr == E_POINTER)
  {
    wprintf(L"No running CLR found! w/hr 0x%08lx\n", hr);
    return;
  }

  if(hr == HRESULT_FROM_WIN32(ERROR_PARTIAL_COPY))
  {
    wprintf(L"No running 32-Bit CLR found! w/hr 0x%08lx\n", hr);
    return;
  }

  if(FAILED(hr))
    wprintf(L"EnumerateLoadedRuntimes failed w/hr 0x%08lx\n", hr);

  while(pEnum->Next(1, &pUnk, NULL) == S_OK)
  {
    hr = pUnk->QueryInterface(IID_ICLRRuntimeInfo, (LPVOID*) &pRuntimeInfo);
    if(FAILED(hr))
    {
      pUnk->Release();
      pUnk = NULL;
      continue;
    }

    DebugBuffer version(30);
    hr = pRuntimeInfo->GetVersionString(version.buffer, &version.size);
    if(SUCCEEDED(hr) &&
      (*version.buffer >= 3) && 
      ((version.buffer[0] == L'v') || (version.buffer[0] == L'V')) &&
      ((version.buffer[1] >= L'4') || (version.buffer[2] != L'.')))
    {
      if(InfoBoard::theInstance()->AppType == DEBUGGER)
        wprintf(L"Debuggee CLR Version %s\n", version.buffer);

      hr = pRuntimeInfo->GetInterface(CLSID_CLRDebuggingLegacy, IID_ICorDebug, (LPVOID*)&pCorDebug);
      if(FAILED(hr))
        wprintf(L"ICorDebug GetInterface failed w/hr 0x%08lx\n", hr);

      hr = pCorDebug->Initialize();
      if(FAILED(hr))
        wprintf(L"ICorDebug initialize failed w/hr 0x%08lx\n", hr);

      hr = pCorDebug->SetManagedHandler(callback);
      if(FAILED(hr))
        wprintf(L"SetManagedHandler failed w/hr 0x%08lx\n", hr);

      hr = pCorDebug->DebugActiveProcess(pid, FALSE, &pDebugProcess);
      if(FAILED(hr))
        wprintf(L"DebugActiveProcess failed w/hr 0x%08lx\n", hr);

      hr = pDebugProcess->QueryInterface(IID_ICorDebugProcess5, (LPVOID*)&pDebugProcess5);
      if(FAILED(hr))
        wprintf(L"DebugActiveProcess5 failed w/hr 0x%08lx\n", hr);

      break;
    }
  }
}

void CLRDebugCore::Release()
{
  this->~CLRDebugCore();
}
