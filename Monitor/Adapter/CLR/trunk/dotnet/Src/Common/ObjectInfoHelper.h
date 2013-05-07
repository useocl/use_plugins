#pragma once

#include <iostream>
#include "../Common/CommonTypes.h"
#include "../Common/DebugBuffer.h"
#include "../Common/CLRType.h"
#include "../Common/Settings.h"
#include "../Common/CLRDebugCore.h"
#include "../Common/CLRObject.h"
#include "../Common/CLRFieldBase.h"
#include "../Common/FieldValueHelper.h"

class ObjectInfoHelper
{
public:
  ObjectInfoHelper();
  virtual ~ObjectInfoHelper();

  void GetInstances(CLRType* type);
  CLRObject* GetCLRObject(const CORDB_ADDRESS address) const;
  CLRFieldBase* GetField(const CLRType* type, const CORDB_ADDRESS object, const mdFieldDef field);

  unsigned int InstanceCount() const;

  void Detach();

private:
  ObjectMap loadedInstances;
  bool inMemoryInstanceMap;

  bool isHeapValid() const;
  void iterateOverHeap(CLRType* type);
  void createCLRObject(const COR_HEAPOBJECT* currentObject, CLRType* type);
  CLRObject* createCLRObject(const CORDB_ADDRESS address) const;
};
