/*
 * Copyright 2010-2014, Sikuli.org, sikulix.com
 * Released under the MIT License.
 *
 * modified RaiMan
 */
package org.sikuli.script;

import java.awt.event.InputEvent;

/**
 * complementing class Key with the constants for the modifier keys<br>
 * only still there for backward compatibility (is already duplicated in Key)
 */
public class KeyModifier {
   public static final int CTRL = InputEvent.CTRL_MASK;
   public static final int SHIFT = InputEvent.SHIFT_MASK;
   public static final int ALT = InputEvent.ALT_MASK;
   public static final int ALTGR = InputEvent.ALT_GRAPH_MASK;
   public static final int META = InputEvent.META_MASK;
   public static final int CMD = InputEvent.META_MASK;
   public static final int WIN = 64;

   @Deprecated
   public static final int KEY_CTRL = InputEvent.CTRL_MASK;
   @Deprecated
   public static final int KEY_SHIFT = InputEvent.SHIFT_MASK;
   @Deprecated
   public static final int KEY_ALT = InputEvent.ALT_MASK;
   @Deprecated
   public static final int KEY_META = InputEvent.META_MASK;
   @Deprecated
   public static final int KEY_CMD = InputEvent.META_MASK;
   @Deprecated
   public static final int KEY_WIN = InputEvent.META_MASK;
}

