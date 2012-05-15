package org.tzi.use.plugins.xmihandler;

public class XMIHandlerException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public XMIHandlerException(String error) {
    Utils.error(error);
  }
  
  public XMIHandlerException(Exception error) {
    Utils.error(error.getMessage());
  }  
  
  public XMIHandlerException() {
  }

}
