/*
 * Copyright 2010-2014, Sikuli.org, sikulix.com
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.basics;


public class HotkeyEvent {
   public int keyCode;
   public int modifiers;

   public HotkeyEvent(int code_, int mod_){
      init(code_, mod_);
   }

   void init(int code_, int mod_){
      keyCode = code_;
      modifiers = mod_;
   }
}

