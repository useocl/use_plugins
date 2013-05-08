#pragma once

#include "../Common/CLRInfoBase.h"
#include "../Common/CLRMetaField.h"
#include "../Common/CLRObject.h"

class CLRType : public CLRInfoBase
{
public:
  CLRType(CString name, mdTypeDef typeDefToken, ICorDebugModule* module);
  virtual ~CLRType();

  MetaFieldMap fieldDefs;
  std::vector<CORDB_ADDRESS> instances;

  CLRType* baseClass;
  std::vector<CLRType*> subClasses; 

  CorTypeAttr typeAttr;
  ICorDebugModule* module;

  virtual void Print();
  virtual void Print(bool fields);
  virtual void PrintInstances();

  virtual CLRMetaField* GetField(const CString name) const;
  virtual CLRMetaField* GetField(const mdFieldDef token) const;
};
