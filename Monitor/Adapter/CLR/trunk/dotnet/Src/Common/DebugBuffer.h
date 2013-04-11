#pragma once

#include <atlstr.h>
#include "windows.h"

class DebugBuffer
{
public:
  DebugBuffer(const DWORD size);
  virtual ~DebugBuffer();

  WCHAR* buffer;
  DWORD size;

  const CString ToCString();
};
