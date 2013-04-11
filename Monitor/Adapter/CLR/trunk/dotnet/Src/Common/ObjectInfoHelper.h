#pragma once

#include <iostream>
#include "../Common/CommonTypes.h"
#include "../Common/DebugBuffer.h"
#include "../Common/DebugValueHelper.h"
#include "../Common/CLRType.h"

class ObjectInfoHelper
{
public:
  ObjectInfoHelper();
  virtual ~ObjectInfoHelper();

  int instances;
  void getCurrentObjectInfo(const COR_HEAPOBJECT* currentObject);

private:
  HRESULT hr;
};
