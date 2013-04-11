#include "../Common/ObjectInfoHelper.h"

ObjectInfoHelper::ObjectInfoHelper() :
  instances(0),
  hr(E_FAIL)
{ }

ObjectInfoHelper::~ObjectInfoHelper()
{ }

void ObjectInfoHelper::getCurrentObjectInfo(const COR_HEAPOBJECT* currentObject)
{
  ICorDebugType* objectDebugType  = NULL;
  ICorDebugObjectValue* object    = NULL;
  ICorDebugClass* objectClass     = NULL;
  ICorDebugModule* currentModule  = NULL;
  IMetaDataImport* metaDataImport = NULL;
  CorElementType type;
  mdTypeDef objectClassToken;
  DebugBuffer typeName(_MAX_PATH);
  CorTypeAttr typeFlag = tdClass;
  mdToken baseToken;

  hr =  InfoBoard::theInstance()->pDebugProcess5->GetTypeForTypeID(currentObject->type, &objectDebugType);
  if(FAILED(hr))
  {
    wprintf(L"GetTypeForTypeID failed w/hr 0x%08lx\n", hr);
    return;
  }

  hr = objectDebugType->GetType(&type);
  if(FAILED(hr))
  {
    wprintf(L"GetType failed w/hr 0x%08lx\n", hr);
    return;
  }

  if(type == ELEMENT_TYPE_CLASS)
  {
    hr =  InfoBoard::theInstance()->pDebugProcess5->GetObjectW(currentObject->address, &object);
    if(SUCCEEDED(hr))
    {
      hr = object->GetClass(&objectClass);
      if(FAILED(hr))
      {
        wprintf(L"GetClass failed w/hr 0x%08lx\n", hr);
        return;
      }

      hr = objectClass->GetModule(&currentModule);
      if(FAILED(hr))
      {
        wprintf(L"GetModule failed w/hr 0x%08lx\n", hr);
        return;
      }

      hr = currentModule->GetMetaDataInterface(IID_IMetaDataImport, reinterpret_cast<IUnknown**>(&metaDataImport));
      if(FAILED(hr))
      {
        wprintf(L"GetMetaDataInterface failed w/hr 0x%08lx\n", hr);
        return;
      }

      hr = objectClass->GetToken(&objectClassToken);
      if(FAILED(hr))
      {
        wprintf(L"GetToken failed w/hr 0x%08lx\n", hr);
        return;
      }

      hr = metaDataImport->GetTypeDefProps(objectClassToken, typeName.buffer, typeName.buffer[0]*typeName.size, &typeName.size, (DWORD*)&typeFlag, &baseToken); 
      if(FAILED(hr))
      {
        wprintf(L"GetNameFromToken failed w/hr 0x%08lx\n", hr);
        return;
      }

      CLRType* type = NULL;
      TypeMap::const_iterator it = InfoBoard::theInstance()->loadedTypes.find(typeName.ToCString());
      if(it != InfoBoard::theInstance()->loadedTypes.end())
      {
        type = (*it).second;
      }
      else
      {
        type = new CLRType(typeName.ToCString(), objectClassToken); 
        InfoBoard::theInstance()->loadedTypes.insert(TypeMapValue(type->name, type));
      }

      CStringSet::const_iterator got = InfoBoard::theInstance()->typesOfInterest.find(typeName.ToCString());
      if(got != InfoBoard::theInstance()->typesOfInterest.end())
      {
        instances++;
        DebugValueHelper debugValue(object, type);
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
  if(metaDataImport)
  {
    metaDataImport->Release();
    metaDataImport = NULL;
  }
  if(object)
  {
    object->Release();
    object = NULL;
  }
  if(currentModule)
  {
    currentModule->Release();
    currentModule = NULL;
  }
}
