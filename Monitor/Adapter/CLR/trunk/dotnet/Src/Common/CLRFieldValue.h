#pragma once

#include "../Common/CLRFieldBase.h"

class CLRFieldValue : public CLRFieldBase
{
  public:
    CString valueAsString;
    ICorDebugGenericValue* genericDebugValue;

    CLRFieldValue(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);

    virtual ~CLRFieldValue();

    virtual void Print();
};
