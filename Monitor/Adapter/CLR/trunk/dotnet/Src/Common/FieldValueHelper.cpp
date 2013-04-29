#include "../Common/FieldValueHelper.h"

CLRFieldBase* FieldValueHelper::GetField(const CLRType* type, const CLRObject* object, const mdFieldDef field)
{
  HRESULT hr                             = E_FAIL;
  ICorDebugObjectValue* debugObjectValue = NULL;
  ICorDebugClass* objectClass            = NULL;
  CLRFieldBase* res                      = NULL;
  ICorDebugValue* debugObject            = NULL;

  CString error(_T(""));

  if(!type)
    return res;

  if(!object)
    return res;

  debugObject = object->debugValue;
  const CLRMetaField* metaField = type->GetField(field);

  if(!metaField)
    return res;

  try
  {
    debugObject->AddRef();

    hr = debugObject->QueryInterface(__uuidof(ICorDebugObjectValue), reinterpret_cast<LPVOID*>(&debugObjectValue));

    if(SUCCEEDED(hr))
    {
      hr = debugObjectValue->GetClass(&objectClass);
      if(FAILED(hr))
      {
        error = _T("GetClass");
        throw hr;
      }

      objectClass->Release();

      hr = type->module->GetClassFromToken(metaField->typeDefToken, &objectClass);
      if(FAILED(hr))
      {
        wprintf(L"GetClassFromToken failed w/hr 0x%08lx\n", hr);
        throw hr;
      }

      ICorDebugValue* actualFieldObject = NULL;

      if(fdStatic & metaField->fieldAttr)
      {
        hr = objectClass->GetStaticFieldValue(metaField->fieldDef, NULL, &actualFieldObject);
        if(FAILED(hr))
        {
          //If the variable is optimized away, this is okay
          if(CORDBG_E_VARIABLE_IS_ACTUALLY_LITERAL != hr)
          {
            error = _T("GetStaticFieldValue");
            throw hr;
          }
        }
      }
      else
      {
        mdFieldDef token =  metaField->fieldDef;
        hr = debugObjectValue->GetFieldValue(objectClass, token, &actualFieldObject);
        if(FAILED(hr))
        {
          error = _T("GetFieldValue");
          throw hr;
        }
      }
      res = CreateSpecificField(metaField->name, actualFieldObject, metaField->fieldDef);
    }

    if(res)
      SetFieldValue(res);

    if(debugObjectValue)
    {
      debugObjectValue->Release();
      debugObjectValue = NULL;
    }
    if(objectClass)
    {
      objectClass->Release();
      objectClass = NULL;
    }
    if(debugObject)
    {
      debugObject = NULL;
    }

    return res;
  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
    return res;
  }  
}

CLRFieldBase* FieldValueHelper::CreateSpecificField(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken)
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
  CString error(_T(""));

  if(!debugValue)
    return field;

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
          error = _T("debug value: IsNull");
          throw hr;
        }

        if(!variableIsNull)
        {
          hr = referencedDebugValue->Dereference(&dereferencedValue);
          if(FAILED(hr))
          {
            error = _T("Dereference");
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
        error = _T("GetObject");
        throw hr;
      }
      fieldDebugValue->Release();
      fieldDebugValue = debugObjectValue;
    }

    hr = fieldDebugValue->GetType(&objectType);
    if(FAILED(hr))
    {
      error = _T("GetType");
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

    return field;

  }
  catch(HRESULT const &hr)
  {
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
    return field;
  }  
}

void FieldValueHelper::SetFieldValue(CLRFieldBase* field)
{
  if(!field)
    return;

  CString error(_T(""));

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
            error = _T("stringDebugValue->GetLength");
            throw hr;
          }
          DebugBuffer stringValue(length+1);
          hr = stringDebugValue->GetString(length*stringValue.buffer[0], (ULONG32*)&stringValue.size, stringValue.buffer);
          if(FAILED(hr))
          {
            error = _T("GetString");
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
          error = _T("QueryInterface: ICorDebugValue2");
          throw hr;
        }

        hr = debugValue2->GetExactType(&debugType);
        if(FAILED(hr))
        {
          error = _T("GetExactType");
          throw hr;
        }

        hr = debugType->GetClass(&objectClass);
        if(FAILED(hr))
        {
          error = _T("GetClass");
          throw hr;
        }

        hr = objectClass->GetModule(&currentModule);
        if(FAILED(hr))
        {
          error = _T("GetModule");
          throw hr;
        }

        hr = currentModule->GetMetaDataInterface(IID_IMetaDataImport, reinterpret_cast<IUnknown**>(&metaDataImport));
        if(FAILED(hr))
        {
          error = _T("GetMetaDataInterface");
          throw hr;
        }

        hr = objectClass->GetToken(&objectClassToken);
        if(FAILED(hr))
        {
          error = _T("GetToken");
          throw hr;
        }
        field->typeDefToken = objectClassToken;

        hr = metaDataImport->GetTypeDefProps(objectClassToken, typeName.buffer, typeName.buffer[0]*typeName.size, &typeName.size, (DWORD*)&typeFlag, &baseToken); 
        if(FAILED(hr))
        {
          error = _T("GetTypeDefProps");
          throw hr;
        }

        hr = fieldValue->debugValue->GetAddress(&address);
        if(FAILED(hr))
        {
          error = _T("GetAddress");
          throw hr;
        }

        fieldValue->typeName = typeName.ToCString();
        fieldValue->address = address;

        //Get object
        //TODO: clean up the test!
        if(!field->isNull)
        {
          hr =  CLRDebugCore::theInstance()->pDebugProcess5->GetObjectW(address, &object);
          if(FAILED(hr))
          {
            error = _T("GetObjectW");
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
                error = _T("GetAddress");
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
                  error = _T("IsNull");
                  throw hr;
                }

                if(variableIsNull == FALSE)
                {
                  hr = referencedElement->Dereference(&dereferencedElement);
                  if(FAILED(hr))
                  {
                    error = _T("Dereference");
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
    std::wcerr << _T("Error occured during COM operation: ") << (const wchar_t*)error << _T(": ") << hr << std::endl;
  }
}
