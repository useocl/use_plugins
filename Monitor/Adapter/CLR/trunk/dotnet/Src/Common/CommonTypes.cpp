/** 
* @file CommonTypes.cpp
* This file implements often used methods for the debugger and the adapter.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/CommonTypes.h"

CString HelperMethods::GetCorTypeAsString(CorElementType type)
{
  CString typeStr;

  switch(type)
  {
  case ELEMENT_TYPE_BOOLEAN:
    typeStr = _T("Boolean");
    break;

  case ELEMENT_TYPE_CHAR:
    typeStr = _T("Character");
    break;

  case ELEMENT_TYPE_I1:
    typeStr = _T("8-bit signed integer");
    break;

  case ELEMENT_TYPE_U1:
    typeStr = _T("8-bit unsigned integer");
    break;

  case ELEMENT_TYPE_I2:
    typeStr = _T("16-bit signed integer");
    break;

  case ELEMENT_TYPE_U2:
    typeStr = _T("16-bit unsigned integer");
    break;

  case ELEMENT_TYPE_I4:
  case ELEMENT_TYPE_I:
    typeStr = _T("32-bit signed integer");
    break;

  case ELEMENT_TYPE_U4:
  case ELEMENT_TYPE_U:
    typeStr = _T("32-bit unsigned integer");
    break;

  case ELEMENT_TYPE_I8:
    typeStr = _T("64-bit signed integer");
    break;

  case ELEMENT_TYPE_U8:
    typeStr = _T("64-bit unsigned integer");
    break;

  case ELEMENT_TYPE_R4:
    typeStr = _T("32-bit floating point");
    break;

  case ELEMENT_TYPE_R8:
    typeStr = _T("64-bit floating point");
    break;

  case ELEMENT_TYPE_VALUETYPE:
    typeStr = _T("Value type");
    break;

  case ELEMENT_TYPE_CLASS:
    typeStr = _T("Class");
    break;

  case ELEMENT_TYPE_ARRAY:
    typeStr = _T("Array: multi-dimensional");
    break;

  case ELEMENT_TYPE_OBJECT:
    typeStr = _T("Object");
    break;

  case ELEMENT_TYPE_SZARRAY:
    typeStr = _T("Array: single-dimensional");
    break;

  case ELEMENT_TYPE_STRING:
    typeStr = _T("String");
    break;

  default:
    typeStr = _T("Unknown");
    break;
  }

  return typeStr;
}

CString HelperMethods::GetValueAsString(ICorDebugGenericValue* debugValue, CorElementType type)
{
  CString valueStr = _T("VALUE NOT FOUND!");
  HRESULT hr = E_FAIL;

  if(!debugValue)
    return valueStr;

  switch(type)
  {
  case ELEMENT_TYPE_BOOLEAN:
    {
      bool value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_CHAR:
    {
      char value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%c");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_I1:               // 8 bit signed integer
    {
      __int8 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_U1:               // 8 bit unsigned integer
    {
      unsigned __int8 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_I2:               // 16 bit signed integer
    {
      __int16 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_U2:               // 16 bit unsigned integer
    {
      unsigned __int16 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_I4:               // 32 bit signed integer
  case ELEMENT_TYPE_I:
    {
      __int32 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_U4:               // 32 bit unsigned integer
  case ELEMENT_TYPE_U:
    {
      unsigned __int32 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_I8:               // 64 bit signed integer
    {
      __int64 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_U8:               // 64 bit unsigned integer
    {
      unsigned __int64 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%d");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_R4:               // 32 bit float
    {
      float value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%g");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  case ELEMENT_TYPE_R8:               // 64 bit float
    {
      double value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        LPCTSTR pszFormat = TEXT("%g");
        valueStr.Format(pszFormat, value);
      }
    }
    break;

  default:
    break;
  }

  return valueStr;
}

CString HelperMethods::GetTypeInfoAsString(const TypeInfo type)
{
  switch (type)
  {
  case NClass:
    {
      return CString(_T("Class"));
      break;
    }
  case AClass:
    {
      return CString(_T("Abstract classs"));
      break;
    }
  case Inter:
    {
      return CString(_T("Interface"));
      break;
    }
  default:
    {
      return CString(_T("Unknown"));
      break;
    }
  }
}
