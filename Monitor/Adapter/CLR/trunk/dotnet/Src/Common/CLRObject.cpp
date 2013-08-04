/** 
* @file CLRObject.cpp
* This file implements a class, that represents information about by the CLR loaded instances.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/CLRObject.h"

CLRObject::CLRObject(CString name, ICorDebugValue* debugValue, mdTypeDef typeDefToken, CORDB_ADDRESS address) :
  CLRInfoBase(name, typeDefToken),
  debugValue(debugValue),
  address(address),
  fields(FieldMap())
{
}


CLRObject::~CLRObject()
{
  if(debugValue)
  {
    debugValue->Release();
    debugValue = NULL;
  }
  fields.clear();

  CLRInfoBase::~CLRInfoBase();
}

void CLRObject::Print()
{
  CLRInfoBase::Print();

  wprintf(L"Address: %d\n", this->address);
}

void CLRObject::Print(bool fields)
{
  CLRObject::Print();
  
  if(fields)
  {
    wprintf(L"Loaded fields count: %d\n",this->fields.size());
    for(FieldMap::const_iterator it = this->fields.begin(); it != this->fields.end(); ++it) 
    {
      (*it).second->Print();
    }
  }
}
