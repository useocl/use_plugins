#pragma once

#include <atlstr.h>
#include <iostream>
#include "../Common/InfoBoard.h"
#include "../Common/DebugBuffer.h"
#include "../Common/CommonTypes.h"
#include "../Common/CLRObject.h"
#include "../Common/CLRFieldBase.h"
#include "../Common/CLRFieldValue.h"
#include "../Common/CLRFieldReference.h"
#include "../Common/CLRFieldList.h"

class DebugValueHelper
{
public:
  DebugValueHelper(ICorDebugValue* const currentObject, CLRType* const type);
  virtual ~DebugValueHelper();

private:
  ICorDebugValue* const currentObject;
  CLRType* const clrType;
  CLRObject* clrObject;

  void GetCurrentObjectData();
  void GetFields();
  void GetFieldValues();
  void GetFieldValue(CLRFieldBase* field);
  void AddSpecificField(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);
  COR_TYPE_LAYOUT GetTypeLayout(const COR_TYPEID& type);
};
