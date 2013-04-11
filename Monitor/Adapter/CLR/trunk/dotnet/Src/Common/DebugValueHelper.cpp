#include "../Common/DebugValueHelper.h"

DebugValueHelper::DebugValueHelper(ICorDebugValue* const currentObject, CLRType* const type) : 
  currentObject(currentObject),
  clrType(type),
  clrObject(NULL)
{
  GetCurrentObjectData();
  GetFields();
  GetFieldValues();
}

DebugValueHelper::~DebugValueHelper()
{
  if(currentObject)
  {
    currentObject->Release();
  }
}

void DebugValueHelper::GetCurrentObjectData()
{
  HRESULT hr                                    = E_FAIL;
  ICorDebugReferenceValue* referencedDebugValue = NULL;
  ICorDebugValue2* debugValue2                  = NULL;
  ICorDebugType* debugType                      = NULL;
  CORDB_ADDRESS address                         = 0;
  CorElementType corType                        = ELEMENT_TYPE_MAX;
  CorElementType corTypeExact                   = ELEMENT_TYPE_MAX;

  try
  {
    hr = currentObject->GetAddress(&address);
    if(FAILED(hr))
    {
      wprintf(L"GetAddress failed w/hr 0x%08lx\n", hr);
      throw hr;
    }
    hr = currentObject->GetType(&corType);
    if(FAILED(hr))
    {
      wprintf(L"GetType failed w/hr 0x%08lx\n", hr);
      throw hr;
    }

    // Set type specific information and
    // add reference to the current object
    clrType->corType = corType;
    clrType->instances.push_back(address);

    currentObject->AddRef();

    hr = currentObject->QueryInterface(__uuidof(ICorDebugValue2), reinterpret_cast<LPVOID*>(&debugValue2));
    if(FAILED(hr))
    {
      wprintf(L"QueryInterface ICorDebugValue2 failed w/hr 0x%08lx\n", hr);
      throw hr;
    }

    hr = debugValue2->GetExactType(&debugType);
    if(FAILED(hr))
    {
      wprintf(L"GetExactType failed w/hr 0x%08lx\n", hr);
      throw hr;
    }

    hr = debugType->GetType(&corTypeExact);
    if(FAILED(hr))
    {
      wprintf(L"GetType (exact) failed w/hr 0x%08lx\n", hr);
      throw hr;
    }

    // add new clr object to global map
    clrObject = new CLRObject(clrType->name, currentObject, clrType->typeDefToken, address);
    InfoBoard::theInstance()->currentObjects.insert(ObjectMapValue(address, clrObject));

    // release local objects
    if(referencedDebugValue)
    {
      referencedDebugValue->Release();
      referencedDebugValue = NULL;
    }
    if(debugValue2)
    {
      debugValue2->Release();
      debugValue2 = NULL;
    }
    if(debugType)
    {
      debugType->Release();
      debugType = NULL;
    }
  }
  catch(HRESULT const &hr)
  {
    wprintf(L"HRESULT 0x%08lx\n recived during COM operation.", hr);
  }
}

void DebugValueHelper::GetFields()
{
  HRESULT hr                             = E_FAIL;
  ICorDebugClass* objectClass            = NULL;
  ICorDebugModule* currentModule         = NULL;
  ICorDebugObjectValue* debugObjectValue = NULL;
  IMetaDataImport* metaData              = NULL;

  try
  {
    currentObject->AddRef();

    hr = currentObject->QueryInterface(__uuidof(ICorDebugObjectValue), reinterpret_cast<LPVOID*>(&debugObjectValue));

    if(SUCCEEDED(hr))
    {
      hr = debugObjectValue->GetClass(&objectClass);
      if(FAILED(hr))
      {
        wprintf(L"GetClass failed w/hr 0x%08lx\n", hr);
        throw hr;
      }

      mdTypeDef classTokenToGetFieldsFor;
      hr = objectClass->GetToken(&classTokenToGetFieldsFor);
      if(FAILED(hr))
      {
        wprintf(L"GetToken failed w/hr 0x%08lx\n", hr);
        throw hr;
      }

      hr = objectClass->GetModule(&currentModule);
      if(FAILED(hr))
      {
        wprintf(L"GetModule failed w/hr 0x%08lx\n", hr);
        throw hr;
      }

      do
      {
        hr = currentModule->GetMetaDataInterface(IID_IMetaDataImport, reinterpret_cast<IUnknown**>(&metaData));
        if(FAILED(hr))
        {
          wprintf(L"GetMetaDataInterface failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        HCORENUM fieldEnumerator = NULL;
        unsigned long numberFieldsRetrieved = 0;
        mdFieldDef symbolicField = 0;

        do
        {
          hr = metaData->EnumFields(&fieldEnumerator, classTokenToGetFieldsFor, &symbolicField, 1, &numberFieldsRetrieved);
          if(FAILED(hr))
          {
            wprintf(L"EnumFields failed w/hr 0x%08lx\n", hr);
            throw hr;
          }

          // If no fields are returned, we have nothing to do
          if(numberFieldsRetrieved == 0)
            break;

          DebugBuffer fieldName(1024);
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

          ICorDebugValue* actualFieldObject = NULL;

          if(fdStatic & fieldAttributes)
          {
            hr = objectClass->GetStaticFieldValue(symbolicField, NULL, &actualFieldObject);
            if(FAILED(hr))
            {
              //If the variable is optimized away, this is okay
              if(CORDBG_E_VARIABLE_IS_ACTUALLY_LITERAL != hr)
              {
                wprintf(L"GetStaticFieldValue failed w/hr 0x%08lx\n", hr);
                throw hr;
              }
            }
          }
          else
          {
            hr = debugObjectValue->GetFieldValue(objectClass, symbolicField, &actualFieldObject);
            if(FAILED(hr))
            {
              wprintf(L"GetFieldValue failed w/hr 0x%08lx\n", hr);
              throw hr;
            }
          }

          // create new CLRField instance
          AddSpecificField(fieldName.ToCString(), actualFieldObject, symbolicField);

          // add myTypeDef token to type
          if(!clrType->fieldsInitialized)
            clrType->fieldDefs.push_back(new CLRMetaField(fieldName.ToCString(), symbolicField));

        } while(true); // loop over properties

        metaData->CloseEnum(fieldEnumerator);

        mdTypeDef parentClassToken = 0;
        hr = metaData->GetTypeDefProps(classTokenToGetFieldsFor, NULL, NULL, NULL, NULL, &parentClassToken);
        if(FAILED(hr))
        {
          wprintf(L"GetTypeDefProps failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        // If it is a top-level object, we have nothing to do
        // parentClassToken == mdTypeDefNil || parentClassToken == mdTypeRefNil
        if(parentClassToken == 0x1000001)
          break;

        classTokenToGetFieldsFor = parentClassToken;

        objectClass->Release();

        hr = currentModule->GetClassFromToken(classTokenToGetFieldsFor, &objectClass);
        if(FAILED(hr))
        {
          wprintf(L"GetClassFromToken failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

      } while(true); // loop over base types

      // field types are initialized now
      clrType->fieldsInitialized = true;
    }
  }
  catch(HRESULT const &hr)
  {
    wprintf(L"HRESULT 0x%08lx\n recived during COM operation.", hr);
  }
}

void DebugValueHelper::AddSpecificField(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken)
{
  HRESULT hr                                    = E_FAIL;
  ICorDebugValue* fieldDebugValue               = NULL;
  ICorDebugReferenceValue* referencedDebugValue = NULL;
  ICorDebugBoxValue* boxedDebugValue            = NULL;
  ICorDebugObjectValue* debugObjectValue        = NULL;
  CorElementType objectType                     = ELEMENT_TYPE_MAX;
  CString info                                  = _T("");
  CString value                                 = _T("");
  bool isNull                                   = false;
  CLRFieldBase* field                           = NULL;

  try
  {
    fieldDebugValue = debugValue;
    fieldDebugValue->AddRef();

    do
    {
      hr = fieldDebugValue->QueryInterface(__uuidof(ICorDebugReferenceValue), reinterpret_cast<LPVOID*>(&referencedDebugValue));

      if(SUCCEEDED(hr))
      {
        ICorDebugValue* dereferencedValue = NULL;
        BOOL variableIsNull;

        hr = referencedDebugValue->IsNull(&variableIsNull);
        if(FAILED(hr))
        {
          wprintf(L"Get IsNull from object failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        if(!variableIsNull)
        {
          hr = referencedDebugValue->Dereference(&dereferencedValue);
          if(FAILED(hr))
          {
            wprintf(L"Dereference object failed w/hr 0x%08lx\n", hr);
            throw hr;
          }

          fieldDebugValue->Release();
          fieldDebugValue = dereferencedValue;
          info = _T("-dereferenced-");
        }
        else
        {
          value = _T("NULL");
          isNull = true;
          break;
        }
      }
    } while(SUCCEEDED(hr));

    hr = fieldDebugValue->QueryInterface(__uuidof(ICorDebugBoxValue), reinterpret_cast<LPVOID*>(&boxedDebugValue));
    if(SUCCEEDED(hr))
    {
      hr = boxedDebugValue->GetObject(&debugObjectValue);
      if(FAILED(hr))
      {
        wprintf(L"Unbox failed w/hr 0x%08lx\n", hr);
        throw hr;
      }
      fieldDebugValue->Release();
      fieldDebugValue = debugObjectValue;
    }

    hr = fieldDebugValue->GetType(&objectType);
    if(FAILED(hr))
    {
      wprintf(L"GetType failed w/hr 0x%08lx\n", hr);
      throw hr;
    }

    // istantiate specific clr field
    if(objectType == ELEMENT_TYPE_BOOLEAN || objectType == ELEMENT_TYPE_CHAR || objectType == ELEMENT_TYPE_I1 ||
       objectType == ELEMENT_TYPE_U1      || objectType == ELEMENT_TYPE_I2   || objectType == ELEMENT_TYPE_U2 ||
       objectType == ELEMENT_TYPE_I4      || objectType == ELEMENT_TYPE_I    || objectType == ELEMENT_TYPE_U4 ||
       objectType == ELEMENT_TYPE_I8      || objectType == ELEMENT_TYPE_U8   || objectType == ELEMENT_TYPE_R4 ||
       objectType == ELEMENT_TYPE_U       || objectType == ELEMENT_TYPE_R8   || objectType == ELEMENT_TYPE_STRING)
    {
      field = new CLRFieldValue(name, fieldDebugValue, fieldDefToken);
      field->corType = objectType;
      field->info = info;
      field->isNull = isNull;
      field->type = VALUE;
    }
    else if(objectType == ELEMENT_TYPE_CLASS)
    {
      field = new CLRFieldReference(name, fieldDebugValue, fieldDefToken);
      field->corType = objectType;
      field->info = info;
      field->isNull = isNull;
      field->type = REFERENCE;
    }
    else if(objectType == ELEMENT_TYPE_SZARRAY)
    {
      field = new CLRFieldList(name, fieldDebugValue, fieldDefToken);
      field->corType = objectType;
      field->info = info;
      field->isNull = isNull;
      field->type = ARRAY;
    }

    // add the specific field to the clr object
    if(field)
      clrObject->fields.insert(FieldMapValue(field->fieldDefToken, field));

    // release local debug values
    if(referencedDebugValue)
    {
      referencedDebugValue->Release();
      referencedDebugValue = NULL;
    }
    if(boxedDebugValue)
    {
      boxedDebugValue->Release();
      boxedDebugValue = NULL;
    }
    if(debugObjectValue)
    {
      debugObjectValue->Release();
      debugObjectValue = NULL;
    }
    field = NULL;
    fieldDebugValue = NULL;
  }
  catch(HRESULT const &hr)
  {
    wprintf(L"HRESULT 0x%08lx\n recived during COM operation.", hr);
  }
}

COR_TYPE_LAYOUT DebugValueHelper::GetTypeLayout(const COR_TYPEID& type)
{
  HRESULT hr = E_FAIL;
  COR_TYPE_LAYOUT layout;

  hr =  InfoBoard::theInstance()->pDebugProcess5->GetTypeLayout(type, &layout);
  if(FAILED(hr))
  {
    wprintf(L"GetTypeLayout failed w/hr 0x%08lx\n", hr);
    throw hr;
  }
  return layout;    
}

void DebugValueHelper::GetFieldValues()
{
  for (FieldMap::const_iterator it = clrObject->fields.begin(); it != clrObject->fields.end(); ++it) 
  {
    GetFieldValue((*it).second);
  }
}

void DebugValueHelper::GetFieldValue(CLRFieldBase* field)
{
  try
  {
    switch (field->corType)
    {
    case ELEMENT_TYPE_BOOLEAN:
    case ELEMENT_TYPE_CHAR:
    case ELEMENT_TYPE_I1:
    case ELEMENT_TYPE_U1:
    case ELEMENT_TYPE_I2:
    case ELEMENT_TYPE_U2:
    case ELEMENT_TYPE_I4:
    case ELEMENT_TYPE_I:
    case ELEMENT_TYPE_U4:
    case ELEMENT_TYPE_U:
    case ELEMENT_TYPE_I8:
    case ELEMENT_TYPE_U8:
    case ELEMENT_TYPE_R4:
    case ELEMENT_TYPE_R8:
      {
        HRESULT hr  = E_FAIL;
        CLRFieldValue* fieldValue = static_cast<CLRFieldValue*>(field);
        ICorDebugGenericValue* genericValue = NULL;
        fieldValue->debugValue->AddRef();
        hr = fieldValue->debugValue->QueryInterface(__uuidof(ICorDebugGenericValue), reinterpret_cast<LPVOID*>(&genericValue));
        if(SUCCEEDED(hr))
        {
          fieldValue->genericDebugValue = genericValue;
          fieldValue->valueAsString = HelperMethods::GetValueAsString(genericValue, fieldValue->corType);
        }
        genericValue = NULL;
      }
      break;
    case ELEMENT_TYPE_STRING:
      {
        HRESULT hr  = E_FAIL;
        ICorDebugStringValue* stringDebugValue = NULL;
        CLRFieldValue* fieldValue = static_cast<CLRFieldValue*>(field);

        fieldValue->debugValue->AddRef();

        hr = fieldValue->debugValue->QueryInterface(__uuidof(ICorDebugStringValue), reinterpret_cast<LPVOID*>(&stringDebugValue));
        if(SUCCEEDED(hr))
        {
          ULONG32 length = 0;
          hr = stringDebugValue->GetLength(&length);
          if(FAILED(hr))
          {
            wprintf(L"String GetLength failed w/hr 0x%08lx\n", hr);
            throw hr;
          }
          DebugBuffer stringValue(length+1);
          hr = stringDebugValue->GetString(length*stringValue.buffer[0], (ULONG32*)&stringValue.size, stringValue.buffer);
          if(FAILED(hr))
          {
            wprintf(L"GetString failed w/hr 0x%08lx\n", hr);
            throw hr;
          }
          fieldValue->valueAsString = stringValue.ToCString();
        }

        if(stringDebugValue)
        {
          stringDebugValue->Release();
          stringDebugValue = NULL;
        }
      }
      break;
    case ELEMENT_TYPE_PTR:
      break;
    case ELEMENT_TYPE_BYREF:
      break;
    case ELEMENT_TYPE_VALUETYPE:
      break;
    case ELEMENT_TYPE_CLASS:
      {
        HRESULT hr                      = E_FAIL;
        ICorDebugClass* objectClass     = NULL;
        ICorDebugModule* currentModule  = NULL;
        IMetaDataImport* metaDataImport = NULL;
        ICorDebugObjectValue* object    = NULL;
        ICorDebugValue2* debugValue2    = NULL;
        ICorDebugType* debugType        = NULL;
        CorTypeAttr typeFlag            = tdClass;
        mdTypeDef objectClassToken      = 0;
        mdToken baseToken               = 0;
        CORDB_ADDRESS address           = 0;
        DebugBuffer typeName(_MAX_PATH);
        CLRFieldReference* fieldValue = static_cast<CLRFieldReference*>(field);

        fieldValue->debugValue->AddRef();
        hr = fieldValue->debugValue->QueryInterface(__uuidof(ICorDebugValue2), reinterpret_cast<LPVOID*>(&debugValue2));
        if(FAILED(hr))
        {
          wprintf(L"QueryInterface ICorDebugValue2 failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        hr = debugValue2->GetExactType(&debugType);
        if(FAILED(hr))
        {
          wprintf(L"GetExactType failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        hr = debugType->GetClass(&objectClass);
        if(FAILED(hr))
        {
          wprintf(L"GetClass failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        hr = objectClass->GetModule(&currentModule);
        if(FAILED(hr))
        {
          wprintf(L"GetModule failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        hr = currentModule->GetMetaDataInterface(IID_IMetaDataImport, reinterpret_cast<IUnknown**>(&metaDataImport));
        if(FAILED(hr))
        {
          wprintf(L"GetMetaDataInterface failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        hr = objectClass->GetToken(&objectClassToken);
        if(FAILED(hr))
        {
          wprintf(L"GetToken failed w/hr 0x%08lx\n", hr);
          throw hr;
        }
        field->typeDefToken = objectClassToken;

        hr = metaDataImport->GetTypeDefProps(objectClassToken, typeName.buffer, typeName.buffer[0]*typeName.size, &typeName.size, (DWORD*)&typeFlag, &baseToken); 
        if(FAILED(hr))
        {
          wprintf(L"GetTypeDefProps failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        hr = fieldValue->debugValue->GetAddress(&address);
        if(FAILED(hr))
        {
          wprintf(L"GetAddress failed w/hr 0x%08lx\n", hr);
          throw hr;
        }

        fieldValue->typeName = typeName.ToCString();
        fieldValue->address = address;

        //Get object
        //TODO: clean up the test!
        if(!field->isNull)
        {
          hr =  InfoBoard::theInstance()->pDebugProcess5->GetObjectW(address, &object);
          if(FAILED(hr))
          {
            wprintf(L"GetObjectW failed w/hr 0x%08lx\n", hr);
            throw hr;
          }
        }

        if(objectClass)
        {
          objectClass->Release();
          objectClass = NULL;
        }
        if(currentModule)
        {
          currentModule->Release();
          currentModule = NULL;
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
        if(debugValue2)
        {
          debugValue2->Release();
          debugValue2 = NULL;
        }
        if(debugType)
        {
          debugType->Release();
          debugType = NULL;
        }
      }
      break;
    case ELEMENT_TYPE_VAR:
      break;
    case ELEMENT_TYPE_ARRAY:
      break;
    case ELEMENT_TYPE_GENERICINST:
      break;
    case ELEMENT_TYPE_TYPEDBYREF:
      break;
    case ELEMENT_TYPE_FNPTR:
      break;
    case ELEMENT_TYPE_OBJECT:
      break;
    case ELEMENT_TYPE_SZARRAY:
      {
        //TODO: treat multiple dimensions!
        HRESULT hr                                 = E_FAIL;
        ICorDebugArrayValue* arrayDebugValue       = NULL;
        ICorDebugValue* element                    = NULL;
        ICorDebugReferenceValue* referencedElement = NULL;
        CORDB_ADDRESS address                      = 0;
        CLRFieldList* fieldValue = static_cast<CLRFieldList*>(field);

        fieldValue->debugValue->AddRef();
        hr = fieldValue->debugValue->QueryInterface(__uuidof(ICorDebugArrayValue), reinterpret_cast<LPVOID*>(&arrayDebugValue));
        if(SUCCEEDED(hr))
        {
          ULONG32 count;
          arrayDebugValue->GetCount(&count);
          fieldValue->refCount = count;

          //TODO: treat different element types!
          for(unsigned int i = 0; i < count; i++)
          {
            hr = arrayDebugValue->GetElementAtPosition(i, &element);
            if(SUCCEEDED(hr))
            {
              hr = element->GetAddress(&address);
              if(FAILED(hr))
              {
                wprintf(L"Element GetAddress failed w/hr 0x%08lx\n", hr);
                throw hr;
              }

              element->AddRef();
              hr = element->QueryInterface(__uuidof(ICorDebugReferenceValue), reinterpret_cast<LPVOID*>(&referencedElement));

              if(SUCCEEDED(hr))
              {
                ICorDebugValue* dereferencedElement = NULL;
                BOOL variableIsNull;

                hr = referencedElement->IsNull(&variableIsNull);
                if(FAILED(hr))
                {
                  wprintf(L"Element Get IsNull from object failed w/hr 0x%08lx\n", hr);
                  throw hr;
                }

                if(!variableIsNull)
                {
                  hr = referencedElement->Dereference(&dereferencedElement);
                  if(FAILED(hr))
                  {
                    wprintf(L"Element Dereference object failed w/hr 0x%08lx\n", hr);
                    throw hr;
                  }

                  element->Release();
                  element = dereferencedElement;

                  fieldValue->references.push_back(address);                    
                }
              }
            }
          }
        }
        if(element)
        {
          element->Release();
          element = NULL;
        }
        if(arrayDebugValue)
        {
          arrayDebugValue->Release();
          arrayDebugValue = NULL;
        }
        if(referencedElement)
        {
          referencedElement->Release();
          referencedElement = NULL;
        }
      }
      break;
    case ELEMENT_TYPE_MVAR:
      break;
    case ELEMENT_TYPE_CMOD_REQD:
      break;
    case ELEMENT_TYPE_CMOD_OPT:
      break;
    case ELEMENT_TYPE_INTERNAL:
      break;
    case ELEMENT_TYPE_MAX:
      break;
    case ELEMENT_TYPE_MODIFIER:
      break;
    case ELEMENT_TYPE_SENTINEL:
      break;
    case ELEMENT_TYPE_PINNED:
      break;
    default:
      break;
    }
  }
  catch(HRESULT const &hr)
  {
    wprintf(L"HRESULT 0x%08lx\n recived during COM operation.", hr);
  }
}
