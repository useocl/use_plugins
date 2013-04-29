#pragma once

#include "../Common/CommonTypes.h"
#include "../Common/CLRInfoBase.h"

class CLRMetaField : public CLRInfoBase
{
public:
  const mdFieldDef fieldDef;
  const DWORD fieldAttr;

  CLRMetaField(CString name, const mdTypeDef typeDefToken, const mdFieldDef fieldDefToken, const DWORD fieldAttr);

  virtual void Print();
};
