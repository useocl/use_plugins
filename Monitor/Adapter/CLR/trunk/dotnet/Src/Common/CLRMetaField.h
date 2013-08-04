/** 
* @file CLRMetaField.h
* This file declares a class, that represents field information.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CommonTypes.h"
#include "../Common/CLRInfoBase.h"

class CLRMetaField : public CLRInfoBase
{
public:
  const mdFieldDef fieldDef; /**< The unique (within a module) field token. */
  const DWORD fieldAttr;     /**< A flag to get more information about the field. */

  /**  
  * The constructor.
  * @param name The name of this type.
  * @param typeDefToken The unique (within a module) token of the type, that declares the field.
  * @param fieldDefToken The unique (within a module) field token.
  * @param fieldAttr A flag to get more information about the field.
  */
  CLRMetaField(CString name, const mdTypeDef typeDefToken, const mdFieldDef fieldDefToken, const DWORD fieldAttr);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRMetaField();

  /** 
  * The method adds the name and the field token to the console.
  */
  virtual void Print();
};
