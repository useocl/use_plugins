#pragma once

#include "../Common/CLRInfoBase.h"

class CLRFieldBase : public CLRInfoBase
{
public:
  ICorDebugValue* debugValue;
  mdFieldDef fieldDefToken;
  bool isNull;
  FieldType type;
  CString info;
  CorElementType corType;

  CLRFieldBase(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);
  virtual ~CLRFieldBase();

  virtual void Print();
};
