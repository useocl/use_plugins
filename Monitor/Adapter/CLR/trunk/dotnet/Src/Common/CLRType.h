/** 
* @file CLRType.h
* This file declares a class, that represents information about by the CLR loaded types.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CLRInfoBase.h"
#include "../Common/CLRMetaField.h"
#include "../Common/CLRObject.h"

class CLRType : public CLRInfoBase
{
public:
  MetaFieldMap fieldDefs;               /**< A map that contains meta data about the fields of this type. */
  std::vector<CORDB_ADDRESS> instances; /**< A vector that contains addresses of currently loaded instances of this type. */
  CorTypeAttr typeAttr;                 /**< An enumeration that represents the specific CLR type of this. */
  ICorDebugModule* module;              /**< The module that declares this type. */
  CLRType* baseClass;                   /**< The base type of this type. */
  std::vector<CLRType*> subClasses;     /**< The subclasses of this type. */

  /**  
  * The constructor.
  * @param name The name of this type.
  * @param typeDefToken The unique (within a module) token of the type.
  * @param module The module that declares this type.
  */
  CLRType(CString name, mdTypeDef typeDefToken, ICorDebugModule* module);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRType();

  /** 
  * The method adds some information of this type to the console.
  */
  virtual void Print();

  /** 
  * The method adds some information of this type and its fields to the console.
  * @param fields Schould fields be printet?
  */
  virtual void Print(bool fields);

  /** 
  * The method adds the addresses of loaded instances of this type to the console.
  */
  virtual void PrintInstances();

  /** 
  * The method returns field information.
  * @param name The name of the desired field.
  * @returns The meta data information about the desired field.
  */
  virtual CLRMetaField* GetField(const CString name) const;


  /** 
  * The method returns field information.
  * @param token The token of the desired field.
  * @returns The meta data information about the desired field.
  */
  virtual CLRMetaField* GetField(const mdFieldDef token) const;
};
