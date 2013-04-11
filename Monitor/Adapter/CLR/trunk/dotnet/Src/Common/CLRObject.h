#pragma once

#include "../Common/CLRInfoBase.h"
#include "../Common/CLRFieldBase.h"

class CLRObject : public CLRInfoBase
{
public:
  ICorDebugValue* debugValue;
  CORDB_ADDRESS address;
  FieldMap fields;

  CLRObject(CString name, ICorDebugValue* debugValue, mdTypeDef typeDefToken, CORDB_ADDRESS address);
  virtual ~CLRObject();

  virtual void Print();
  virtual void Print(bool fields);
};
