#include "../Common/CLRFieldBase.h"


CLRFieldBase::CLRFieldBase(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken) : CLRInfoBase(name, 0),
  debugValue(debugValue),
  fieldDefToken(fieldDefToken),
  isNull(false),
  type(UNKNOWN)
{
}


CLRFieldBase::~CLRFieldBase()
{
  if(debugValue)
  {
    debugValue->Release();
    debugValue = NULL;
  }

  CLRInfoBase::~CLRInfoBase();
}


void CLRFieldBase::Print()
{
  CLRInfoBase::Print();

  wprintf(L"\t Type: %s\n", HelperMethods::GetCorTypeAsString(this->corType));
  wprintf(L"\t Info: %s\n", this->info);
}
