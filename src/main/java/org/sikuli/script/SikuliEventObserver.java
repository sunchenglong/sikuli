/*
 * Copyright 2010-2014, Sikuli.org, sikulix.com
 * Released under the MIT License.
 *
 * modified RaiMan
 */
package org.sikuli.script;

import java.util.*;

/**
 * see @{link SikuliEventAdapter}
 * @deprecated
 */
@Deprecated
public interface SikuliEventObserver extends EventListener {
   public void targetAppeared(ObserveEvent e);
   public void targetVanished(ObserveEvent e);
   public void targetChanged(ObserveEvent e);
}
