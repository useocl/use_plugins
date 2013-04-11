#include "../Common/CLRFieldList.h"


CLRFieldList::CLRFieldList(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken) :
  CLRFieldBase(name, debugValue, fieldDefToken),
  refCount(0),
  references(std::vector<CORDB_ADDRESS>())
{
}


CLRFieldList::~CLRFieldList()
{
  references.clear();

  CLRFieldBase::~CLRFieldBase();
}


void CLRFieldList::Print()
{
  CLRFieldBase::Print();

  wprintf(L"\t References: \n\t");
  for (std::vector<CORDB_ADDRESS>::const_iterator refit = this->references.begin(); refit != this->references.end(); ++refit) 
  {
    wprintf(L"\t Address %d\n", *refit);
  }
}
