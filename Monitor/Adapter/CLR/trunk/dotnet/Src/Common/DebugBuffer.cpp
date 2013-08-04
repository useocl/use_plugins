/** 
* @file DebugBuffer.cpp
* This file implements a helper class for COM returned char arrays.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/DebugBuffer.h"

DebugBuffer::DebugBuffer(const DWORD size) : 
  buffer(new WCHAR[size]),
  size(size)
{ }


DebugBuffer::~DebugBuffer(void)
{
  delete[] buffer;
}

const CString DebugBuffer::ToCString()
{
  return CString(buffer);
}
