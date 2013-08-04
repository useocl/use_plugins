/** 
* @file CLRMetaField.cpp
* This file implements a class, that represents field information.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/CLRMetaField.h"

CLRMetaField::CLRMetaField(CString name, const mdTypeDef typeDefToken, const mdFieldDef fieldDefToken, const DWORD fieldAttr) : 
  CLRInfoBase(name, typeDefToken),
  fieldDef(fieldDefToken),
  fieldAttr(fieldAttr)
{ }


void CLRMetaField::Print()
{
  CLRInfoBase::Print();
  wprintf(L"FieldToken: %d\n", fieldDef);
}

CLRMetaField::~CLRMetaField()
{ }
