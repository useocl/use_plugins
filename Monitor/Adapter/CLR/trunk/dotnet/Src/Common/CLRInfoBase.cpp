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
