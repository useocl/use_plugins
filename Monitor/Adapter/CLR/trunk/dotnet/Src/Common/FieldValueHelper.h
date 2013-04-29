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
#include "../Common/CLRDebugCore.h"
#include "../Common/CLRType.h"


class FieldValueHelper
{
public:
  static CLRFieldBase* GetField(const CLRType* type, const CLRObject* object, const mdFieldDef field);

private:
  static CLRFieldBase* CreateSpecificField(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);
  static void SetFieldValue(CLRFieldBase* field);
};
