/** 
* @file CLRObject.h
* This file declares a class, that represents information about by the CLR loaded instances.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CLRInfoBase.h"
#include "../Common/CLRFieldBase.h"

class CLRObject : public CLRInfoBase
{
public:
  ICorDebugValue* debugValue; /**< A CLR interface to get debug information about the instance. */
  CORDB_ADDRESS address;      /**< The memory address of the instance. */
  FieldMap fields;            /**< A map for field values of the instance. */

  /**  
  * The constructor.
  * @param name The name of this type.
  * @param debugValue A CLR interface to get debug information about the instance.
  * @param typeDefToken The unique (within a module) token of the type.
  * @param address The memory address of the instance.
  */
  CLRObject(CString name, ICorDebugValue* debugValue, mdTypeDef typeDefToken, CORDB_ADDRESS address);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRObject();

  /**  
  * The methods adds some information about this type to the console.
  */
  virtual void Print();

  /**  
  * The methods adds some information about the fields of this type  and the type itself to the console.
  * @param fields Schould fields be printet?
  */
  virtual void Print(bool fields);
};
