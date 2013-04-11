#pragma once

#include "../Common/CLRFieldBase.h"

class CLRFieldReference : public CLRFieldBase
{
public:
  CString typeName;
  CORDB_ADDRESS address;


  CLRFieldReference(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);
  virtual ~CLRFieldReference();

  virtual void Print();
};
