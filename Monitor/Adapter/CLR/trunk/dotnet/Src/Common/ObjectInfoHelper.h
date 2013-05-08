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
#include "../Common/TypeInfoHelper.h"

class ObjectInfoHelper
{
public:
  ObjectInfoHelper(const TypeInfoHelper& typeInfo);
  virtual ~ObjectInfoHelper();

  void GetInstances(CLRType* type);
  CLRObject* GetCLRObject(const CORDB_ADDRESS address) const;
  CLRFieldBase* GetField(const CLRType* type, const CORDB_ADDRESS object, const mdFieldDef field);

  unsigned int InstanceCount() const;

  void Detach();

private:
  const TypeInfoHelper& typeInfo;
  ObjectMap loadedInstances;
  const bool inMemoryInstanceMap;
  const bool cacheAtStartUp;

  bool isHeapValid() const;
  void iterateOverHeap(CLRType* type, bool init = false);
  void createCLRObject(const COR_HEAPOBJECT* currentObject, CLRType* type, bool init = false);
  CLRObject* createCLRObject(const CORDB_ADDRESS address) const;

  bool isCacheInitialized;
  void initializeCache();
};
