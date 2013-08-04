/** 
* @file CLRFieldBase.h
* This file declares the base class of all field value representing classes.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CLRInfoBase.h"

class CLRFieldBase : public CLRInfoBase
{
public:
  ICorDebugValue* debugValue; /**< An interface to get the information about the field. */
  mdFieldDef fieldDefToken;   /**< The unique (within a module) token of this field. */
  bool isNull;                /**< Represents this field a null pointer? */
  FieldType type;             /**< The specific type of the derived class to get the instances correct type. */
  CString info;               /**< A String which contains debug information. */
  CorElementType corType;     /**< An enumeration that represents the specific CLR type of this. */

  /**  
  * The constructor.
  * @param name The name of this field.
  * @param debugValue An interface to get the information about the field.
  * @param fieldDefToken The unique (within a module) token of this field.
  */
  CLRFieldBase(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRFieldBase();

  /** 
  * The method adds some information of this field to the console.
  */
  virtual void Print();
};
