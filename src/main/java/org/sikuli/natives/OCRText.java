/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.sikuli.natives;

public class OCRText extends OCRRect {
  private long swigCPtr;

  protected OCRText(long cPtr, boolean cMemoryOwn) {
    super(VisionProxyJNI.OCRText_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(OCRText obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        VisionProxyJNI.delete_OCRText(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public String getString() {
    return VisionProxyJNI.OCRText_getString(swigCPtr, this);
  }

  public OCRWords getWords() {
    return new OCRWords(VisionProxyJNI.OCRText_getWords(swigCPtr, this), true);
  }

  public OCRParagraphs getParagraphs() {
    return new OCRParagraphs(VisionProxyJNI.OCRText_getParagraphs(swigCPtr, this), true);
  }

  public OCRText() {
    this(VisionProxyJNI.new_OCRText(), true);
  }

}
