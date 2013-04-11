#pragma once

#include "../Common/CLRFieldBase.h"

class CLRFieldList : public CLRFieldBase
{
public:
  int refCount;
  std::vector<CORDB_ADDRESS> references;

  CLRFieldList(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);
  virtual ~CLRFieldList();

  virtual void Print();
};
