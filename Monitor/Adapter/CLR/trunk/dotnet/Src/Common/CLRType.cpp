#include "../Common/CLRType.h"

CLRType::CLRType(CString name, mdTypeDef typeDefToken) : 
  CLRInfoBase(name, typeDefToken),
  instances(std::vector<CORDB_ADDRESS>()),
  fieldDefs(std::vector<CLRMetaField*>()),
  fieldsInitialized(false)
{ }

CLRType::~CLRType()
{
  instances.clear();
  fieldDefs.clear();

  CLRInfoBase::~CLRInfoBase();
}

void CLRType::Print()
{
  CLRInfoBase::Print();

  wprintf(L"TypeToken: %d\n", this->typeDefToken);
}

void CLRType::Print(bool instances, bool fields)
{
  CLRType::Print();
  
  if(instances)
  {
    for(std::vector<CORDB_ADDRESS>::const_iterator iter = this->instances.begin(); iter != this->instances.end(); ++iter)
    {
      wprintf(L"Address: %d\n", *iter);
    }
  }

  if(fields)
  {
    for(std::vector<CLRMetaField*>::const_iterator iter = this->fieldDefs.begin(); iter != this->fieldDefs.end(); ++iter)
    {
      (*iter)->Print();
    }
  }
}

CLRMetaField* CLRType::GetFieldByName(CString name)
{
  CString searchString("<");
  searchString += name;
  int size = searchString.GetLength();

  for(std::vector<CLRMetaField*>::const_iterator iter = this->fieldDefs.begin(); iter != this->fieldDefs.end(); ++iter)
  {
    if((*iter)->name.Left(size) == searchString)
      return *iter;
  }
  return NULL;
}
