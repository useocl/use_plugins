#include "../Common/CLRInfoBase.h"

CLRInfoBase::CLRInfoBase(CString name, mdTypeDef typeDefToken) : 
  name(name),
  typeDefToken(typeDefToken),
  corType(ELEMENT_TYPE_END),
  info(_T(""))
{
}


CLRInfoBase::~CLRInfoBase()
{
  name.~CStringT();
  info.~CStringT();
}


void CLRInfoBase::Print()
{
  wprintf(L"TypeName: %s\n", this->name);
}
