#pragma once

#include "../Common/CLRInfoBase.h"
#include "../Common/CLRMetaField.h"

class CLRType : public CLRInfoBase
{
public:
  CLRType(CString name, mdTypeDef typeDefToken);
  virtual ~CLRType();

  std::vector<CORDB_ADDRESS> instances;
  std::vector<CLRMetaField*> fieldDefs;

  bool fieldsInitialized;

  virtual void Print();
  virtual void Print(bool instances, bool fields);

  virtual CLRMetaField* GetFieldByName(CString name);
};
