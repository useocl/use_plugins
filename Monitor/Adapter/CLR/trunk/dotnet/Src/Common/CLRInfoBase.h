#pragma once

#include "../Common/CommonTypes.h"

class CLRInfoBase
{
public:
  CString name;
  mdTypeDef typeDefToken;

  CLRInfoBase(CString name, mdTypeDef typeDefToken);

  virtual ~CLRInfoBase();

  virtual void Print();
};
