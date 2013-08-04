/** 
* @file CLRInfoBase.cpp
* This file implements the base class of all used meta data classes.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/CLRInfoBase.h"

CLRInfoBase::CLRInfoBase(CString name, mdTypeDef typeDefToken) : 
  name(name),
  typeDefToken(typeDefToken)
{
}


CLRInfoBase::~CLRInfoBase()
{ }


void CLRInfoBase::Print()
{
  wprintf(L"TypeName: %s\n", this->name);
}
