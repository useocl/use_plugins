/** 
* @file CLRFieldValue.h
* This file declares a class, that represents information about primitive data or string field values.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CLRFieldBase.h"

class CLRFieldValue : public CLRFieldBase
{
public:
  CString valueAsString;                     /**< A string representation of the actual value. */
  ICorDebugGenericValue* genericDebugValue;  /**< An interface to get or set the field value. */

  /**  
  * The constructor.
  * @param name The name of this field.
  * @param debugValue An interface to get the information about the field.
  * @param fieldDefToken The unique (within a module) token of this field.
  */
  CLRFieldValue(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRFieldValue();

  /** 
  * The method adds some information of this field to the console.
  */
  virtual void Print();
};
