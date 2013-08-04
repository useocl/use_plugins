/** 
* @file CLRFieldList.h
* This file declares a class, that represents information about fields that points to data structures
* including references to other instances.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CLRFieldBase.h"

class CLRFieldList : public CLRFieldBase
{
public:
  int refCount;                            /**< The number of elements contained in the data structure. */
  std::vector<CORDB_ADDRESS> references;   /**< A vecotor containing memory addresses of the referenced instances. */

  /**  
  * The constructor.
  * @param name The name of this field.
  * @param debugValue An interface to get the information about the field.
  * @param fieldDefToken The unique (within a module) token of this field.
  */
  CLRFieldList(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRFieldList();

  /** 
  * The method adds some information of this field to the console.
  */
  virtual void Print();
};
