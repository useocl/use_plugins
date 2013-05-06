#pragma once

#include "../Common/CommonTypes.h"
#include "../Common/DebugBuffer.h"
#include "../Common/CLRType.h"
#include "../Common/Settings.h"

class TypeInfoHelper
{
public:
  TypeInfoHelper();
  virtual ~TypeInfoHelper();

  void AddModule(ICorDebugModule* module);
  void RemoveModule(ICorDebugModule* module);

  TypeInfo GetTypeInfo(const CorTypeAttr type) const;
  CLRType* GetType(CString name) const;
  CLRType* GetType(mdTypeDef token) const;
  bool HasAny() const;
  bool IsInitialized() const;

  AppType appType;

  void PrintLoadedModules() const;
  void PrintLoadedTypes(bool fields) const;
  void PrintSimpleInheritance() const;

  unsigned int ModuleCount() const;
  unsigned int TypeCount() const;

  void Detach();

private:
  void GetTypesFromModules(ICorDebugModule* module);
  void GetFieldInformation(CLRType* type, IMetaDataImport* metaData) const;
  void GetInheritanceInformation(IMetaDataImport* metaData) const;

  TypeMap loadedTypes;
  ModuleSet loadedModules;
};
