/** 
* @file DebugBuffer.h
* This file declares a helper class for COM returned char arrays.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include <atlstr.h>
#include "windows.h"

class DebugBuffer
{
public:
  /**  
  * The constructor.
  * @param name The initial size of the buffer.
  */
  DebugBuffer(const DWORD size);

  /**  
  * The virtual destructor.
  */
  virtual ~DebugBuffer();

  WCHAR* buffer;  /**< The char array to return. */
  DWORD size;     /**< The size of the buffer. */

  /** 
  * The method converts the buffer to a CString.
  * @returns The CString representing the buffer.
  */
  const CString ToCString();
};
