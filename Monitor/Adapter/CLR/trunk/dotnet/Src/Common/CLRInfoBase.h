#pragma once

#include "../Common/CommonTypes.h"

class CLRInfoBase
{
public:
  CString name;
  mdTypeDef typeDefToken;
  CorElementType corType;
  CString info;

  CLRInfoBase(CString name, mdTypeDef typeDefToken);

  virtual ~CLRInfoBase();

  virtual void Print();
};
