/** 
* @file CommonTypes.h
* This file declares often used types or methods for the debugger and the adapter.
* It includes the CLR debug api headers.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

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

/** 
* @struct CStringHash
* This struct implements the hash function for the type CString.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/
struct CStringHash
{
  std::size_t operator()(CString s) const 
  {
    return stdext::hash_value((LPCTSTR)s);;
  }
};

/* This set contains pointer to CLR modules. */
typedef std::unordered_set<ICorDebugModule*> ModuleSet;

/* This set contains CStrings. */
typedef std::unordered_set<CString, CStringHash> CStringSet;

/* This map contains pointers to CLRType instances. */
typedef std::unordered_map<mdTypeDef, CLRType*> TypeMap;
/* This type represents the key value pair of TypeMap. */
typedef TypeMap::value_type TypeMapValue;

/* This map contains pointers to CLRObject instances. */
typedef std::unordered_map<CORDB_ADDRESS, CLRObject*> ObjectMap;
/* This type represents the key value pair of TypeMap. */
typedef ObjectMap::value_type ObjectMapValue;

/* This map contains pointers to CLRFieldBase instances. */
typedef std::unordered_map<mdFieldDef, CLRFieldBase*> FieldMap;
/* This type represents the key value pair of TypeMap. */
typedef FieldMap::value_type FieldMapValue;

/* This map contains pointers to CLRMetaField instances. */
typedef std::unordered_map<mdFieldDef, CLRMetaField*> MetaFieldMap;
/* This type represents the key value pair of TypeMap. */
typedef MetaFieldMap::value_type MetaFieldMapValue;

/* This enumeration defines the application type. */
enum AppType {DEBUGGER, ADAPTER};

/* This enumeration defines the specific class of field values. */
enum FieldType {VALUE, REFERENCE, ARRAY, UNKNOWN};

/* This enumeration defines the specific class type. */
enum TypeInfo {NClass, AClass, Inter, NotKnown};

/** 
* @class HelperMethods
* This class provides often used methods to convert something to strings.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/
class HelperMethods
{
public:
  /** 
  * The method converts a CorElementType to its string representation.
  * @param type The CorElementType to convert.
  * @returns The string representation of the given type.
  */
  static CString GetCorTypeAsString(CorElementType type);

  /** 
  * The method converts a field value to its string representation.
  * @param debugValue An ICorDebugGenericValue interface to get the field value.
  * @type debugValue The CorElementType of the field.
  * @returns The string representation of the desired field vlaue.
  */
  static CString GetValueAsString(ICorDebugGenericValue* debugValue, CorElementType type);

  /** 
  * The method converts a TypeInfo to its string representation.
  * @param The TypeInfo to convert.
  * @returns The string representation of the given type.
  */
  static CString GetTypeInfoAsString(const TypeInfo type);
};
