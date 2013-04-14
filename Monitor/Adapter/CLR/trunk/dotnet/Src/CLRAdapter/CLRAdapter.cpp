#include "CLRAdapter.h"

#include "JNIHelper.h"
#include "../Common/CLRDebugCore.h"
#include "../Common/HeapInfoHelper.h"
#include "../Common/CLRFieldBase.h"
#include "../Common/CLRFieldValue.h"
#include "../Common/CLRFieldReference.h"
#include "../Common/CLRFieldList.h"

CLRDebugCore debug;
HeapInfoHelper* heapHelper = NULL;

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_attachToCLR
  (JNIEnv* env, jobject adapter, jlong pid)
{
  //set debuggee types of interest
  InfoBoard::theInstance()->typesOfInterest.insert(L"Debuggee.Cat");
  InfoBoard::theInstance()->typesOfInterest.insert(L"Debuggee.Dog");
  InfoBoard::theInstance()->typesOfInterest.insert(L"Debuggee.PetColor");

  HRESULT hr = E_FAIL;

  debug.initializeProcessesByPid((DWORD)pid);

  // collecting information for snapshot
  hr = InfoBoard::theInstance()->pDebugProcess->Stop(0);

  heapHelper = new HeapInfoHelper();
  heapHelper->iterateOverHeap();

  hr = InfoBoard::theInstance()->pDebugProcess->Continue(FALSE);

  return InfoBoard::theInstance()->pDebugProcess ? 0 : -1;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_resumeCLR
  (JNIEnv* env, jobject adapter)
{
  HRESULT hr = E_FAIL;
  hr = InfoBoard::theInstance()->pDebugProcess->Continue(FALSE);
  if(FAILED(hr))
    return -1;
  else
    return 0;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_suspendCLR
  (JNIEnv* env, jobject adapter)
{
  HRESULT hr = E_FAIL;
  hr = InfoBoard::theInstance()->pDebugProcess->Stop(0);
  if(FAILED(hr))
    return -1;
  else
    return 0;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_stopCLR
  (JNIEnv* env, jobject adapter)
{
  HRESULT hr = E_FAIL;
  hr = InfoBoard::theInstance()->pDebugProcess->Detach();
  InfoBoard::theInstance()->release();
  heapHelper->~HeapInfoHelper();
  if(FAILED(hr))
    return -1;
  else
    return 0;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getNumOfInstances
  (JNIEnv* env, jobject adapter)
{
  return InfoBoard::theInstance()->currentObjects.size();
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getNumOfTypes
  (JNIEnv* env, jobject adapter)
{
  return InfoBoard::theInstance()->loadedTypes.size();
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getNumOfModules
  (JNIEnv* env, jobject adapter)
{
  return InfoBoard::theInstance()->loadedModules.size();
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getCLRType
  (JNIEnv* env, jobject adapter, jstring name)
{
  jboolean isCopyName = JNI_FALSE;
  const char* typeName = env->GetStringUTFChars(name, &isCopyName);
  CString searchName(typeName);
  TypeMap::const_iterator gotType = InfoBoard::theInstance()->loadedTypes.find(searchName);
  jobject clrType = env->NewGlobalRef(NULL);
  jthrowable ex = NULL;

  jclass typeClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRType");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID constructorId = env->GetMethodID(typeClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;JLjava/lang/String;)V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  //search type
  CLRType* type = NULL;
  
  if(gotType != InfoBoard::theInstance()->loadedTypes.end())
  {
    type = (*gotType).second;
    jlong typeId = type->typeDefToken;
    jobject res = env->NewObject(typeClass, constructorId, adapter, typeId, name);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;

    clrType = res;
  }

  // free memory
  if(isCopyName)
    env->ReleaseStringUTFChars(name, typeName);

  return clrType;

exception_handler:
    env->ExceptionDescribe();
    env->ExceptionClear();
    return clrType;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getInstances
  (JNIEnv* env, jobject adapter, jobject clrType)
{
  jthrowable ex = NULL;
  jobject hashSet = env->NewGlobalRef(NULL);
  CLRType* pClrType = NULL;

  // create java HashSet to return
  jclass setClass = env->FindClass("java/util/HashSet");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setConstructor = env->GetMethodID(setClass, "<init>", "()V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  hashSet = env->NewObject(setClass, setConstructor);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setAdd = env->GetMethodID(setClass, "add", "(Ljava/lang/Object;)Z");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // get type information
  pClrType = JNIHelper::GetCLRType(env, clrType);
  if(!pClrType)
    return hashSet;

  // get instances
  jobject clrObject = env->NewGlobalRef(NULL);
  jclass clrObjectClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRObject");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID clrObjectConstructor = env->GetMethodID(clrObjectClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;Lorg/tzi/use/plugins/monitor/vm/mm/VMType;J)V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  for(std::vector<CORDB_ADDRESS>::const_iterator address = pClrType->instances.begin(); address != pClrType->instances.end(); ++address)
  {
    jlong jAddress = *address;
    clrObject = env->NewObject(clrObjectClass, clrObjectConstructor, adapter, clrType, jAddress);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;
    else
    {
      env->CallBooleanMethod(hashSet, setAdd, clrObject);
      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;
    }
  }

  return hashSet;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return hashSet;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getFieldByName
  (JNIEnv* env, jobject adapter, jobject clrType, jstring name)
{
  jobject clrField = env->NewGlobalRef(NULL);
  CLRType* pClrType = NULL;
  CLRMetaField* clrMField = NULL;
  jboolean isCopyName = JNI_FALSE;
  const char* typeName = env->GetStringUTFChars(name, &isCopyName);
  CString searchName(typeName);
  jthrowable ex = NULL;

  jclass clrFieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRField");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID clrFieldConstructor = env->GetMethodID(clrFieldClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;Ljava/lang/String;J)V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // find given type
  pClrType = JNIHelper::GetCLRType(env, clrType);
  if(!pClrType)
    return clrField;

  // find field def token
  clrMField = pClrType->GetFieldByName(searchName);
  if(!clrMField)
    return clrField;

  // create new CLRField
  jlong token = (jlong)clrMField->fieldDef;
  clrField = env->NewObject(clrFieldClass, clrFieldConstructor, adapter, name, token);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // release
  if(isCopyName)
    env->ReleaseStringUTFChars(name, typeName);

  return clrField;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return clrField;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getWrappedField
  (JNIEnv* env, jobject adapter, jobject clrObject, jobject clrField)
{
  jthrowable ex = NULL;
  jobject wrapper = env->NewGlobalRef(NULL);
  jobject res = env->NewGlobalRef(NULL);
  CLRObject* pClrObject = NULL;
  CLRFieldBase* pField = NULL;
  mdFieldDef fieldToken = 0;
  FieldMap::const_iterator gotField;
  ObjectMap::const_iterator gotObject;

  // get pointer to given clr object
  pClrObject = JNIHelper::GetCLRObject(env, clrObject);
  if(!pClrObject)
    return wrapper;

  // get field token for given field
  fieldToken = JNIHelper::GetFieldDef(env, clrField);
  if(!fieldToken)
    return wrapper;

  // get desired field
  gotField = pClrObject->fields.find(fieldToken);

  if(gotField != pClrObject->fields.end())
    pField = (*gotField).second;
  else
    return wrapper;

  // if null value return
  if(pField->isNull)
    return wrapper;

  // get wrapper class information
  switch (pField->type)
  {
  case VALUE:
    {
      CLRFieldValue* pFieldV = static_cast<CLRFieldValue*>(pField);
      
      // get value
      if(pFieldV->corType == ELEMENT_TYPE_STRING)
      {
        CStringA str(pFieldV->valueAsString);
        res = env->NewStringUTF((const char*)str);
      }
      else
        res = JNIHelper::GetFieldPValue(env, pFieldV->genericDebugValue, pFieldV->corType);

      // create value field wrapper
      jclass fieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/wrap/clr/CLRFieldWrapValue");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jmethodID fieldConstructor = env->GetMethodID(fieldClass, "<init>", "(JLjava/lang/Object;)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jlong token = pFieldV->fieldDefToken;

      wrapper = env->NewObject(fieldClass, fieldConstructor, token, res);

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      break;
    }
  case REFERENCE:
    {
      CLRFieldReference* pFieldR = static_cast<CLRFieldReference*>(pField);

      // create reference field wrapper
      jclass fieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/wrap/clr/CLRFieldWrapReference");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jmethodID fieldConstructor = env->GetMethodID(fieldClass, "<init>", "(JJ)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jlong token = (jlong)pFieldR->fieldDefToken;
      jlong address = (jlong)pFieldR->address;

      wrapper = env->NewObject(fieldClass, fieldConstructor, token, address);

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      break;
    }
  case ARRAY:
    {
      CLRFieldList* pFieldL = static_cast<CLRFieldList*>(pField);

      // create array field wrapper
      jclass fieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/wrap/clr/CLRFieldWrapRefArray");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jmethodID fieldConstructor = env->GetMethodID(fieldClass, "<init>", "(J)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jlong token = pFieldL->fieldDefToken;

      wrapper = env->NewObject(fieldClass, fieldConstructor, token);


      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      // put addresses into array field wrapper
      jmethodID fieldAdd = env->GetMethodID(fieldClass, "addReference", "(J)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      for (std::vector<CORDB_ADDRESS>::const_iterator refit = pFieldL->references.begin(); refit != pFieldL->references.end(); ++refit) 
      {
        jlong address = *refit;

        env->CallVoidMethod(wrapper, fieldAdd, address);

        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }

      break;
    }
  default:
   return wrapper;
  }

  return wrapper;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return wrapper;
}
