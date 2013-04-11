#pragma once

#include "../Common/CommonTypes.h"
#include "../Common/CLRInfoBase.h"

class CLRMetaField : public CLRInfoBase
{
public:
  mdFieldDef fieldDef;

  CLRMetaField(CString name, mdFieldDef fieldDefToken);

  virtual void Print();
};
