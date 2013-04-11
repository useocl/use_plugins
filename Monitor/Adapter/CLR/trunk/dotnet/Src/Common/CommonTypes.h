#pragma once

#include <cor.h>
#include <CorHdr.h>
#include <cordebug.h>
#include <unordered_set>
#include <unordered_map>
#include <atlstr.h>

class CLRFieldBase;
class CLRType;
class CLRObject;

struct CStringHash
{
  std::size_t operator()(CString s) const 
  {
    return stdext::hash_value((LPCTSTR)s);;
  }
};

typedef std::unordered_set<ICorDebugModule*> ModuleSet;
typedef std::unordered_set<CString, CStringHash> CStringSet;

typedef std::unordered_map<CString, CLRType*, CStringHash> TypeMap;
typedef TypeMap::value_type TypeMapValue;

typedef std::unordered_map<CORDB_ADDRESS, CLRObject*> ObjectMap;
typedef ObjectMap::value_type ObjectMapValue;

typedef std::unordered_map<mdFieldDef, CLRFieldBase*> FieldMap;
typedef FieldMap::value_type FieldMapValue;

enum AppType {DEBUGGER, ADAPTER};

enum FieldType {VALUE, REFERENCE, ARRAY, UNKNOWN};

class HelperMethods
{
public:
  static CString GetCorTypeAsString(CorElementType type);
  static CString GetValueAsString(ICorDebugGenericValue* debugValue, CorElementType type);
};
