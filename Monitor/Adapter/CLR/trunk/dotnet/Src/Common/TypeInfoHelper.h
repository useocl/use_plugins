/** 
* @file TypeInfoHelper.h
* This file declares the class TypeInfoHelper, that manages the loaded types and modules.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CommonTypes.h"
#include "../Common/DebugBuffer.h"
#include "../Common/CLRType.h"
#include "../Common/Settings.h"

class TypeInfoHelper
{
public:
  AppType appType;   /**< The type of the application (debugger or adapter). */

  /**  
  * The default constructor.
  */
  TypeInfoHelper();

  /**  
  * The virtual destructor.
  */
  virtual ~TypeInfoHelper();

  /**  
  * The method adds the given module to the loaded modules and all its types
  * to the loaded ones.
  * @param module The new module.
  */
  void AddModule(ICorDebugModule* module);

  /**  
  * The method removes the given module from the loaded modules and all its types
  * from the loaded ones.
  * @param module The module to remove.
  */
  void RemoveModule(ICorDebugModule* module);

  /**  
  * The method returns information about a type.
  * @param type The type information as CorTypeAttr.
  * @returns The information about the type (interface, abstract class or class).
  */
  TypeInfo GetTypeInfo(const CorTypeAttr type) const;

  /**  
  * The method returns the desired type.
  * @param name The name of the desired type.
  * @returns A pointer to the desired CLRType object.
  */
  CLRType* GetType(CString name) const;

  /**  
  * The method returns the desired type.
  * @param token The meta data token of the desired type.
  * @returns A pointer to the desired CLRType object.
  */
  CLRType* GetType(mdTypeDef token) const;

  /**  
  * The method returns if the TypeInfoHelper has any loaded types.
  * @returns Has the TypeInfoHelper any loaded type?
  */
  bool HasAny() const;

  /**  
  * The method returns if the TypeInfoHelper is initialized.
  * Therfore the number of modules has to be at least the configured Settings::MinNumberOfModules.
  * @returns Is the TypeInfoHelper initialized?
  */
  bool IsInitialized() const;

  /** 
  * The method adds the loaded modules to the console.
  */
  void PrintLoadedModules() const;

  /** 
  * The method adds the loaded types to the console.
  * @param fields Schould fields be printet?
  */
  void PrintLoadedTypes(bool fields) const;

  /** 
  * The method adds the inheritance information to the console.
  */
  void PrintSimpleInheritance() const;

  /** 
  * The method returns the number of loaded modules.
  * @returns The number of loaded modules.
  */
  unsigned int ModuleCount() const;

  /** 
  * The method returns the number of loaded types.
  * @returns The number of loaded types.
  */
  unsigned int TypeCount() const;

  /**
  * The method clares cached information.
  */
  void Detach();

private:
  TypeMap loadedTypes;      /**< Map of loaded types. */
  ModuleSet loadedModules;  /**< Map of loaded modules. */

  /** 
  * The method reads the types of the given module.
  * @param module The module to get its types from.
  */
  void GetTypesFromModules(ICorDebugModule* module);

  /** 
  * The method gets the field information of a desired type.
  * @param type The CLRType to get its fields.
  * @param metaData The meta data of the module, that declares the given type.
  */
  void GetFieldInformation(CLRType* type, IMetaDataImport* metaData) const;

  /** 
  * The method gets the inheritance information of about the types declared in the current analysed module.
  * @param metaData The meta data of the currentls analysed module.
  */
  void GetInheritanceInformation(IMetaDataImport* metaData) const;
};
