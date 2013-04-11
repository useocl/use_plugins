#include "../Common/HeapInfoHelper.h"

HeapInfoHelper::HeapInfoHelper() :
  pCoreDebugHeapEnum(NULL)
{
  getHeapInfo();
}

HeapInfoHelper::~HeapInfoHelper()
{
  if(pCoreDebugHeapEnum)
  {
    pCoreDebugHeapEnum->Release();
    pCoreDebugHeapEnum = NULL;
  }
}

void HeapInfoHelper::getHeapInfo()
{
  HRESULT hr = E_FAIL;

  hr = InfoBoard::theInstance()->pDebugProcess5->GetGCHeapInformation(&heapInfo);
  if(FAILED(hr))
    wprintf(L"GetGCHeapInformation failed w/hr 0x%08lx\n", hr);
  else
  {
    if(InfoBoard::theInstance()->appType == DEBUGGER)
    {
      std::wcout << L"GC Type: " << (heapInfo.gcType == CorDebugWorkstationGC ? L"Workstation" : L"Server") << std::endl;
      std::wcout << L"Heaps: " << heapInfo.numHeaps << std::endl;
    }
  }
}

void HeapInfoHelper::iterateOverHeap()
{
  HRESULT hr = E_FAIL;

  if(!heapInfo.areGCStructuresValid)
  {
    std::wcout << L"HeapStructure not valid." << std::endl;
    return;
  }

  hr =  InfoBoard::theInstance()->pDebugProcess5->EnumerateHeap(&pCoreDebugHeapEnum);
  if(FAILED(hr))
    wprintf(L"EnumerateHeap failed w/hr 0x%08lx\n", hr);

  COR_HEAPOBJECT obj;
  ObjectInfoHelper objectHelper;

  while(pCoreDebugHeapEnum->Next(1, &obj, NULL) == S_OK)
  {
    if(FAILED(hr))
      wprintf(L"Get next heap object failed w/hr 0x%08lx\n", hr);

    objectHelper.getCurrentObjectInfo(&obj);
  }
  if(InfoBoard::theInstance()->appType == DEBUGGER)
    std::wcout << L"Created instances: " << objectHelper.instances << std::endl;
}
