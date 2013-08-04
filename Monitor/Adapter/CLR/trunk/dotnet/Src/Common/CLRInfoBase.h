/** 
* @file CLRInfoBase.h
* This file declares the base class of all used meta data classes.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CommonTypes.h"

class CLRInfoBase
{
public:
  CString name;             /**< The name of this type. */
  mdTypeDef typeDefToken;   /**< The unique (within a module) token of the type. */

  /**  
  * The constructor.
  * @param name The name of this type.
  * @param typeDefToken The unique (within a module) token of the type.
  */
  CLRInfoBase(CString name, mdTypeDef typeDefToken);

  /**  
  * The virtual destructor.
  */
  virtual ~CLRInfoBase();

  /** 
  * The method adds the name of the type to the console.
  */
  virtual void Print();
};
