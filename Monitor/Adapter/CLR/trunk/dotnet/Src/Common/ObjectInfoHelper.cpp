#include "../Common/ObjectInfoHelper.h"

ObjectInfoHelper::ObjectInfoHelper() :
  loadedInstances(ObjectMap()),
  inMemoryInstanceMap(Settings::theInstance()->InMemoryInstanceMap),
  compareTypeNames(Settings::theInstance()->CompareTypeNames)
{ }

ObjectInfoHelper::~ObjectInfoHelper()
{
  loadedInstances.clear();
}

void ObjectInfoHelper::createCLRObject(const COR_HEAPOBJECT* currentObject, CLRType* type)
{
  HRESULT  hr                                   = E_FAIL;
  ICorDebugType* objectDebugType                = NULL;
  ICorDebugObjectValue* object                  = NULL;
  ICorDebugClass* objectClass                   = NULL;
  mdTypeDef objectClassToken                    = 0;
  CorElementType corType                        = ELEMENT_TYPE_MAX;
  IMetaDataImport* metaData                     = NULL;
  ICorDebugModule* module                       = NULL;
  CLRObject* clrObject                          = NULL;
  CorTypeAttr typeFlag                          = tdClass;
  mdToken baseToken                             = 0;
  CString error(_T(""));
  DebugBuffer typeName(_MAX_PATH);

  try
  {
    hr =  CLRDebugCore::theInstance()->pDebugProcess5->GetTypeForTypeID(currentObject->type, &objectDebugType);
    if(FAILED(hr))
    {
      error = _T("GetTypeForTypeID");
      throw hr;
    }

    hr = objectDebugType->GetType(&corType);
    if(FAILED(hr))
    {
      error = _T("GetType");
      throw hr;
    }

    if(corType == ELEMENT_TYPE_CLASS)
    {
      hr =  CLRDebugCore::theInstance()->pDebugProcess5->GetObjectW(currentObject->address, &object);
      if(SUCCEEDED(hr))
      {
        hr = object->GetClass(&objectClass);
        if(FAILED(hr))
        {
          error = _T("GetClass");
          throw hr;
        }

        hr = objectClass->GetToken(&objectClassToken);
        if(FAILED(hr))
        {
          error = _T("GetToken");
          throw hr;
        }

        if(!compareTypeNames)
        {
          if(type->typeDefToken == objectClassToken)
          {
            clrObject = new CLRObject(type->name, object, type->typeDefToken, currentObject->address);
            type->instances.push_back(clrObject);

            if(inMemoryInstanceMap)
              loadedInstances.insert(ObjectMapValue(clrObject->address, clrObject));
          }
        }
        else
        {
          hr = objectClass->GetModule(&module);
          if(FAILED(hr))
          {
            error = _T("GetModule");
            throw hr;
          }

          hr = module->GetMetaDataInterface(IID_IMetaDataImport, reinterpret_cast<IUnknown**>(&metaData));
          if(FAILED(hr))
          {
            error = _T("GetMetaDataInterface");
            throw hr;
          }

          hr = metaData->GetTypeDefProps(objectClassToken, typeName.buffer, typeName.buffer[0]*typeName.size, &typeName.size, (DWORD*)&typeFlag, &baseToken); 
          if(FAILED(hr))
          {
            error = _T("GetTypeDefProps");
            throw hr;
          }

          if(type->typeDefToken == objectClassToken && typeName.ToCString() == type->name)
          {
            clrObject = new CLRObject(type->name, object, type->typeDefToken, currentObject->address);
            type->instances.push_back(clrObject);

            if(inMemoryInstanceMap)
              loadedInstances.insert(ObjectMapValue(clrObject->address, clrObject));
          }

        }
      }
    }

    if(objectDebugType)
    {
      objectDebugType->Release();
      objectDebugType = NULL;
    }
    if(objectClass)
    {
      objectClass->Release();
      objectClass = NULL;
    }
    if(object)
    {
      object->Release();
      object = NULL;
    }
    if(metaData)
    {
      metaData->Release();
      metaData = NULL;
    }
    if(module)
    {
      module->Release();
      module = NULL;
    }
  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
  }
}

CLRObject* ObjectInfoHelper::createCLRObject(const CORDB_ADDRESS address) const
{
  HRESULT  hr                                   = E_FAIL;
  ICorDebugObjectValue* object                  = NULL;
  ICorDebugClass* objectClass                   = NULL;
  IMetaDataImport* metaData                     = NULL;
  ICorDebugModule* module                       = NULL;
  mdTypeDef objectClassToken                    = 0;
  CLRObject* clrObject                          = NULL;
  CorTypeAttr typeFlag                          = tdClass;
  mdToken baseToken                             = 0;
  CString error(_T(""));
  DebugBuffer typeName(_MAX_PATH);

  if(address == 0)
    return clrObject;

  try
  {
    hr =  CLRDebugCore::theInstance()->pDebugProcess5->GetObjectW(address, &object);
    if(SUCCEEDED(hr))
    {
      hr = object->GetClass(&objectClass);
      if(FAILED(hr))
      {
        error = _T("GetClass");
        throw hr;
      }

      hr = objectClass->GetModule(&module);
      if(FAILED(hr))
      {
        error = _T("GetModule");
        throw hr;
      }

      hr = objectClass->GetToken(&objectClassToken);
      if(FAILED(hr))
      {
        error = _T("GetToken");
        throw hr;
      }

      hr = module->GetMetaDataInterface(IID_IMetaDataImport, reinterpret_cast<IUnknown**>(&metaData));
      if(FAILED(hr))
      {
        error = _T("GetMetaDataInterface");
        throw hr;
      }

      hr = metaData->GetTypeDefProps(objectClassToken, typeName.buffer, typeName.buffer[0]*typeName.size, &typeName.size, (DWORD*)&typeFlag, &baseToken); 
      if(FAILED(hr))
      {
        error = _T("GetTypeDefProps");
        throw hr;
      }

      clrObject = new CLRObject(typeName.ToCString(), object, objectClassToken, address);
    }

    if(objectClass)
    {
      objectClass->Release();
      objectClass = NULL;
    }
    if(object)
    {
      object->Release();
      object = NULL;
    }
    if(metaData)
    {
      metaData->Release();
      metaData = NULL;
    }
    if(module)
    {
      module->Release();
      module = NULL;
    }

    return clrObject;
  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
    return NULL;
  }
}

bool ObjectInfoHelper::isHeapValid() const
{
  HRESULT hr = E_FAIL;
  COR_HEAPINFO heapInfo;
  CString error(_T(""));

  try
  {
    hr = CLRDebugCore::theInstance()->pDebugProcess5->GetGCHeapInformation(&heapInfo);
    if(FAILED(hr))
    {
      error = _T("GetGCHeapInformation");
      throw hr;
    }

    if(InfoBoard::theInstance()->AppType == DEBUGGER)
    {
      std::wcout << L"GC Type: " << (heapInfo.gcType == CorDebugWorkstationGC ? L"Workstation" : L"Server") << std::endl;
      std::wcout << L"Heaps: " << heapInfo.numHeaps << std::endl;
    }

    return (heapInfo.areGCStructuresValid != FALSE);
  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
    return false;
  }
}

void ObjectInfoHelper::iterateOverHeap(CLRType* type)
{
  HRESULT hr = E_FAIL;
  ICorDebugHeapEnum* pCoreDebugHeapEnum = NULL;
  CString error(_T(""));

  if(!isHeapValid())
  {
    std::wcerr << L"HeapStructure not valid." << std::endl;
    return;
  }

  try
  {
    hr =  CLRDebugCore::theInstance()->pDebugProcess5->EnumerateHeap(&pCoreDebugHeapEnum);
    if(FAILED(hr))
    {
      error = _T("EnumerateHeap");
      throw hr;
    }

    COR_HEAPOBJECT obj;

    while(pCoreDebugHeapEnum->Next(1, &obj, NULL) == S_OK)
    {
      if(FAILED(hr))
      {
        error = _T("EnumerateHeap->Next");
        throw hr;
      }

      createCLRObject(&obj, type);
    }

    if(pCoreDebugHeapEnum)
    {
      pCoreDebugHeapEnum->Release();
      pCoreDebugHeapEnum = NULL;
    }
  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
  }
}

void ObjectInfoHelper::GetInstances(CLRType* type)
{
  if(!type)
    return;

  type->instances.clear();
  iterateOverHeap(type);
}

CLRObject* ObjectInfoHelper::GetCLRObject(const CORDB_ADDRESS address) const
{
  if(inMemoryInstanceMap)
  {
    CLRObject* res = NULL;

    ObjectMap::const_iterator it = loadedInstances.find(address);
    if(it != loadedInstances.end())
      res = (*it).second;

    return res;
  }
  else
    return createCLRObject(address);
}

CLRFieldBase* ObjectInfoHelper::GetField(const CLRType* type, const CORDB_ADDRESS object, const mdFieldDef field)
{
  CLRFieldBase* res = NULL;

  if(!type || object == 0 || field == 0)
    return res;

  const CLRObject* clrObject = GetCLRObject(object);

  if(!clrObject)
    return res;

  res = FieldValueHelper::GetField(type, clrObject, field);

  return res;
}

unsigned int ObjectInfoHelper::InstanceCount() const
{
  return loadedInstances.size();
}
