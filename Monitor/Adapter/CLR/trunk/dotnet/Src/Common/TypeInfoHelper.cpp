#include "../Common/TypeInfoHelper.h"

TypeInfoHelper::TypeInfoHelper() : 
  appType(ADAPTER),
  loadedTypes(TypeMap()),
  loadedModules(ModuleSet())
{ }

TypeInfoHelper::~TypeInfoHelper()
{
  loadedTypes.clear();
  loadedModules.clear();
}

void TypeInfoHelper::AddModule(ICorDebugModule* module)
{
  GetTypesFromModules(module);
  loadedModules.insert(module);
}

void TypeInfoHelper::RemoveModule(ICorDebugModule* module)
{
  //TODO: implement me!
}

bool TypeInfoHelper::HasAny() const
{
  return loadedTypes.size() > 0;
}

unsigned int TypeInfoHelper::ModuleCount() const
{
  return loadedModules.size();
}

unsigned int TypeInfoHelper::TypeCount() const
{
  return loadedTypes.size();
}

void TypeInfoHelper::GetTypesFromModules(ICorDebugModule* module)
{
  HRESULT hr                = E_FAIL;
  IMetaDataImport* metaData = NULL;
  CString error(_T(""));

  try
  {
    hr = module->GetMetaDataInterface(IID_IMetaDataImport, reinterpret_cast<IUnknown**>(&metaData));
    if(FAILED(hr))
    {
      error = _T("GetMetaDataInterface");
      throw hr;
    }

    HCORENUM typeEnumerator = NULL;
    unsigned long numberOfTypesRetrieved = 0;
    mdTypeDef symbolicType = 0;

    do
    {
      CorTypeAttr typeFlag = tdClass;
      mdToken baseToken    = 0;
      DebugBuffer typeName(_MAX_PATH);

      hr = metaData->EnumTypeDefs(&typeEnumerator, &symbolicType, 1, &numberOfTypesRetrieved);
      if(FAILED(hr))
      {
        error = _T("EnumTypeDefs");
        throw hr;
      }

      // If no types are returned, we have nothing to do
      if(numberOfTypesRetrieved == 0)
        break;

      hr = metaData->GetTypeDefProps(symbolicType, typeName.buffer, typeName.buffer[0]*typeName.size, &typeName.size, (DWORD*)&typeFlag, &baseToken); 
      if(FAILED(hr))
      {
        error = _T("GetTypeDefProps");
        throw hr;
      }

      // insert new type into global map
      CLRType* newType = new CLRType(typeName.ToCString(), symbolicType, module);
      newType->typeAttr = typeFlag;

      if(newType)
        loadedTypes.insert(TypeMapValue(newType->typeDefToken, newType));

      GetFieldInformation(newType, metaData);


      newType = NULL;

    } while(true);

    // Closes the enumerator with the specified handle.
    metaData->CloseEnum(typeEnumerator);

    GetInheritanceInformation(metaData);

    if(metaData)
    {
      metaData->Release();
      metaData = NULL;
    }

  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
  }
}

TypeInfo TypeInfoHelper::GetTypeInfo(const CorTypeAttr type) const
{
  //TODO: nested types.
  //TODO: made interface work.
  TypeInfo res = TypeInfo::NotKnown;

  if(type == (tdNotPublic | tdClass | tdBeforeFieldInit))
    res = TypeInfo::NClass;
  else if(type == (tdPublic | tdClass | tdBeforeFieldInit))
    res = TypeInfo::NClass;
  else if(type == (tdNotPublic | tdClass | tdSealed | tdBeforeFieldInit))
    res = TypeInfo::NClass;
  else if(type == (tdPublic | tdClass | tdSealed | tdBeforeFieldInit))
    res = TypeInfo::NClass;
  else if(type == (tdNotPublic | tdAbstract | tdBeforeFieldInit))
    res = TypeInfo::AClass;
  else if(type == (tdPublic | tdAbstract | tdBeforeFieldInit))
    res = TypeInfo::AClass;
  else if(type == (tdNotPublic | tdInterface))
    res = TypeInfo::Inter;
  else if(type == (tdPublic | tdInterface))
    res = TypeInfo::Inter;

  return res;
}

void TypeInfoHelper::GetFieldInformation(CLRType* type, IMetaDataImport* metaData) const
{
  HRESULT hr = E_FAIL;
  CString error(_T(""));

  try
  {
    HCORENUM fieldEnumerator = NULL;
    unsigned long numberOfFieldsRetrieved = 0;
    mdFieldDef symbolicField = 0;

    do
    {
      hr = metaData->EnumFields(&fieldEnumerator, type->typeDefToken, &symbolicField, 1, &numberOfFieldsRetrieved);
      if(FAILED(hr))
      {
        error = _T("EnumFields");
        throw hr;
      }

      // If no fields are returned, we have nothing to do
      if(numberOfFieldsRetrieved == 0)
        break;

      DebugBuffer fieldName(MAX_PATH);
      DWORD fieldAttributes = 0;

      hr = metaData->GetFieldProps(
        symbolicField,
        NULL,
        fieldName.buffer,
        fieldName.buffer[0]*fieldName.size,
        &fieldName.size,
        &fieldAttributes,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL
        );
      if(FAILED(hr))
      {
        wprintf(L"GetFieldProps failed w/hr 0x%08lx\n", hr);
        throw hr;
      }

      type->fieldDefs.insert(MetaFieldMapValue(symbolicField, new CLRMetaField(fieldName.ToCString(), type->typeDefToken, symbolicField, fieldAttributes)));

    } while(true);

  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
  }
}

void TypeInfoHelper::GetInheritanceInformation(IMetaDataImport* metaData) const
{
  HRESULT hr = E_FAIL;
  CString error(_T(""));

  try
  {
    HCORENUM typeEnumerator = NULL;
    unsigned long numberOfTypesRetrieved = 0;
    mdTypeDef symbolicType = 0;

    do
    {
      CorTypeAttr typeFlag = tdClass;
      mdToken baseToken    = 0;
      DebugBuffer typeName(_MAX_PATH);

      hr = metaData->EnumTypeDefs(&typeEnumerator, &symbolicType, 1, &numberOfTypesRetrieved);
      if(FAILED(hr))
      {
        error = _T("EnumTypeDefs");
        throw hr;
      }

      // If no types are returned, we have nothing to do
      if(numberOfTypesRetrieved == 0)
        break;

      hr = metaData->GetTypeDefProps(symbolicType, NULL, NULL, NULL, (DWORD*)&typeFlag, &baseToken); 
      if(FAILED(hr))
      {
        error = _T("GetTypeDefProps");
        throw hr;
      }

      if(baseToken !=  0x1000001)
      {
        CLRType* parent = GetType(baseToken);
        CLRType* child = GetType(symbolicType);

        if(parent && child)
        {
          child->baseClass = parent;
          parent->subClasses.push_back(child);
        }
      }

    } while(true);

    // Closes the enumerator with the specified handle.
    metaData->CloseEnum(typeEnumerator);

  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
  }
}

CLRType* TypeInfoHelper::GetType(mdTypeDef token) const
{
  CLRType* res = NULL;

  TypeMap::const_iterator it = loadedTypes.find(token);
  if(it != loadedTypes.end())
    res = (*it).second;

  return res;
}

CLRType* TypeInfoHelper::GetType(CString name) const
{
  CLRType* res = NULL;

  for(TypeMap::const_iterator it = loadedTypes.begin(); it != loadedTypes.end(); ++it)
  {
    if((*it).second->name == name)
    {
      res = (*it).second;
      break;
    }
  }

  return res;
}

void TypeInfoHelper::PrintLoadedModules() const
{
  wprintf(L"Loaded modul count: %d", loadedModules.size());
  for(ModuleSet::const_iterator it = loadedModules.begin(); it != loadedModules.end(); ++it) 
  {
    DebugBuffer name(_MAX_PATH);
    (*it)->GetName(name.size, (ULONG32*)&name.size, name.buffer);
    wprintf(L"LoadModule %s\n", name.buffer);
  }
}

void TypeInfoHelper::PrintLoadedTypes(bool fields) const
{
  wprintf(L"Loaded type count: %d\n", this->loadedTypes.size());
  for(TypeMap::const_iterator it = this->loadedTypes.begin(); it != this->loadedTypes.end(); ++it) 
  {
    (*it).second->Print(fields);
  }
}

void TypeInfoHelper::PrintSimpleInheritance() const
{
  for(TypeMap::const_iterator it = this->loadedTypes.begin(); it != this->loadedTypes.end(); ++it) 
  {
    wprintf(L"Type: %s\n", (*it).second->name);
    wprintf(L"\tParent: %s\n", (*it).second->baseClass ? (*it).second->baseClass->name : L"NULL");
    for(std::vector<CLRType*>::const_iterator child = (*it).second->subClasses.begin(); child != (*it).second->subClasses.end(); ++child) 
    {
      wprintf(L"\tChild: %s\n", (*child)->name);
    }
  }
}
