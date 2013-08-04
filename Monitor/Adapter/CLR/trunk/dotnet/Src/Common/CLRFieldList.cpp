/** 
* @file CLRFieldList.cpp
* This file implements a class, that represents information about fields that points to data structures
* including references to other instances.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/CLRFieldList.h"


CLRFieldList::CLRFieldList(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken) :
  CLRFieldBase(name, debugValue, fieldDefToken),
  refCount(0),
  references(std::vector<CORDB_ADDRESS>())
{ }


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
