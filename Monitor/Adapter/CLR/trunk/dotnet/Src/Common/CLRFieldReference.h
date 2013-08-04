/** 
* @file CLRFieldReference.h
* This file declares a class, that represents information about reference field values.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CLRFieldBase.h"

class CLRFieldReference : public CLRFieldBase
{
public:
  CString typeName;       /**< The name of the type of this field. */
  CORDB_ADDRESS address;  /**< The memory address of the referenced instance. */

  /**  
  * The constructor.
  * @param name The name of this field.
  * @param debugValue An interface to get the information about the field.
  * @param fieldDefToken The unique (within a module) token of this field.
  */
  CLRFieldReference(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRFieldReference();

  /** 
  * The method adds some information of this field to the console.
  */
  virtual void Print();
};
