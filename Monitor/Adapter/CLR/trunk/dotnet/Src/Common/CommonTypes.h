#pragma once

#include <cor.h>
#include <CorHdr.h>
#include <cordebug.h>
#include <unordered_set>
#include <unordered_map>
#include <atlstr.h>
#include <iostream>

class CLRFieldBase;
class CLRType;
class CLRObject;
class CLRMetaField;

struct CStringHash
{
  std::size_t operator()(CString s) const 
  {
    return stdext::hash_value((LPCTSTR)s);;
  }
};

typedef std::unordered_set<ICorDebugModule*> ModuleSet;
typedef std::unordered_set<CString, CStringHash> CStringSet;
typedef std::vector<CLRObject*> ObjectVector;

typedef std::unordered_map<mdTypeDef, CLRType*> TypeMap;
typedef TypeMap::value_type TypeMapValue;

typedef std::unordered_map<CORDB_ADDRESS, CLRObject*> ObjectMap;
typedef ObjectMap::value_type ObjectMapValue;

typedef std::unordered_map<mdFieldDef, CLRFieldBase*> FieldMap;
typedef FieldMap::value_type FieldMapValue;

typedef std::unordered_map<mdFieldDef, CLRMetaField*> MetaFieldMap;
typedef MetaFieldMap::value_type MetaFieldMapValue;

enum AppType {DEBUGGER, ADAPTER};

enum FieldType {VALUE, REFERENCE, ARRAY, UNKNOWN};

enum TypeInfo {NClass, AClass, Inter, NotKnown};

class HelperMethods
{
public:
  static CString GetCorTypeAsString(CorElementType type);
  static CString GetValueAsString(ICorDebugGenericValue* debugValue, CorElementType type);
  static CString GetTypeInfoAsString(const TypeInfo type);
};
