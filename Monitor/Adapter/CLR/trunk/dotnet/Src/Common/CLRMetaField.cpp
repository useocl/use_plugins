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
