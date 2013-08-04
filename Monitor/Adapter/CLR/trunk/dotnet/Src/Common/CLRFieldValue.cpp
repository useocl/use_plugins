/** 
* @file CLRFieldValue.cpp
* This file implements a class, that represents information about primitive data or string field values.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/
#include "../Common/CLRFieldValue.h"


CLRFieldValue::CLRFieldValue(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken) : 
  CLRFieldBase(name, debugValue, fieldDefToken),
  valueAsString(_T("")),
  genericDebugValue(NULL)
{ }


CLRFieldValue::~CLRFieldValue()
{
  if(genericDebugValue)
  {
    genericDebugValue->Release();
    genericDebugValue = NULL;
  }
  valueAsString.~CStringT();

  CLRFieldBase::~CLRFieldBase();
}


void CLRFieldValue::Print()
{
  CLRFieldBase::Print();

  wprintf(L"\t Value: %s\n", this->valueAsString);
}
