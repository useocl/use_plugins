#include "../Common/CLRMetaField.h"

CLRMetaField::CLRMetaField(CString name, mdFieldDef fieldDefToken) : CLRInfoBase(name, 0),
  fieldDef(fieldDefToken)
{ }


void CLRMetaField::Print()
{
  CLRInfoBase::Print();
  wprintf(L"FieldToken: %d\n", fieldDef);
}
