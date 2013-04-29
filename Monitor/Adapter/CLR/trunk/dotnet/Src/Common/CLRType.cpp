#include "../Common/CLRType.h"

CLRType::CLRType(CString name, mdTypeDef typeDefToken, ICorDebugModule* module) : 
  CLRInfoBase(name, typeDefToken),
  fieldDefs(MetaFieldMap()),
  baseClass(NULL),
  subClasses(std::vector<CLRType*>()),
  instances(ObjectVector()),
  typeAttr(tdClass),
  module(module)
{ }

CLRType::~CLRType()
{
  fieldDefs.clear();
  instances.clear();

  module = NULL;

  CLRInfoBase::~CLRInfoBase();
}

void CLRType::Print()
{
  CLRInfoBase::Print();

  wprintf(L"TypeToken: %d\n", this->typeDefToken);
}

void CLRType::Print(bool fields)
{
  CLRType::Print();
  
  if(fields)
  {
    std::wcout << L"Fields:" << std::endl;
    for(MetaFieldMap::const_iterator iter = this->fieldDefs.begin(); iter != this->fieldDefs.end(); ++iter)
    {
      (*iter).second->Print();
    }
  }
}

void CLRType::PrintInstances()
{
  std::wcout << L"Instances of " << (const wchar_t*)this->name << L":" << std::endl;
  for(ObjectVector::const_iterator iter = this->instances.begin(); iter != this->instances.end(); ++iter)
  {
    std::wcout << "\t" << (*iter)->address << std::endl;
  }
}

CLRMetaField* CLRType::GetField(const CString name) const
{
  CLRMetaField* res = NULL;
  CString searchString(_T("<"));
  searchString += name;
  int size = searchString.GetLength();
  CLRType* currentType = (CLRType*)this;
  CLRType* parent = NULL;

  do
  {
    parent = currentType->baseClass;

    for(MetaFieldMap::const_iterator iter = currentType->fieldDefs.begin(); iter != currentType->fieldDefs.end(); ++iter)
    {
      if((*iter).second->name.Left(size) == searchString || (*iter).second->name.Left(size - 1) == name)
      {
        res = (*iter).second;
        break;
      }
    }
    currentType = parent;

  } while (parent);

  return res;
}

CLRMetaField* CLRType::GetField(const mdFieldDef token) const
{
  CLRMetaField* res = NULL;
  CLRType* currentType = (CLRType*)this;
  CLRType* parent = NULL;

  do
  {
    parent = currentType->baseClass;

    MetaFieldMap::const_iterator it = currentType->fieldDefs.find(token);
    if(it != currentType->fieldDefs.end())
    {
      res = (*it).second;
      break;
    }
    currentType = parent;

  } while (parent);

  return res;
}
