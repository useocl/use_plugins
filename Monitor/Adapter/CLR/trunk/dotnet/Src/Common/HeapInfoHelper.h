#pragma once

#include "../Common/ObjectInfoHelper.h"
#include "../Common/InfoBoard.h"

class HeapInfoHelper
{
public:
  HeapInfoHelper();
  virtual ~HeapInfoHelper();

  void iterateOverHeap();

private:
  ICorDebugHeapEnum* pCoreDebugHeapEnum;
  COR_HEAPINFO heapInfo;

  void getHeapInfo();
};
