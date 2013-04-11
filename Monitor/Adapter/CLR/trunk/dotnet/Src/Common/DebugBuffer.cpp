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
