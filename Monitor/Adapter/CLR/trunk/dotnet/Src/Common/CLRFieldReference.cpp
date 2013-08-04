/** 
* @file CLRFieldReference.cpp
* This file implements a class, that represents information about reference field values.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "CLRFieldReference.h"


CLRFieldReference::CLRFieldReference(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken) :
  CLRFieldBase(name, debugValue, fieldDefToken),
  typeName(_T("")),
  address(0)
{ }


CLRFieldReference::~CLRFieldReference()
{
  typeName.~CStringT();

  CLRFieldBase::~CLRFieldBase();
}


void CLRFieldReference::Print()
{
  CLRFieldBase::Print();

  wprintf(L"\t TypeName: %s\n", this->typeName);
  wprintf(L"\t Address: %d\n", this->address);
}
