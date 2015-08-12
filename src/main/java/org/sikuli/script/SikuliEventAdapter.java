/*
 * Copyright 2010-2014, Sikuli.org, sikulix.com
 * Released under the MIT License.
 *
 * modified RaiMan 2014
 */
package org.sikuli.script;

/**
 *
 * @deprecated use @{link ObserverCallBack} instead
 */
@Deprecated
public class SikuliEventAdapter implements SikuliEventObserver {

  @Override
  public void targetAppeared(ObserveEvent e) {
    appeared(e);
  }

  @Override
  public void targetVanished(ObserveEvent e) {
    vanished(e);
  }

  @Override
  public void targetChanged(ObserveEvent e) {
    changed(e);
  }

  public void appeared(ObserveEvent e) {
  }

  public void vanished(ObserveEvent e) {
  }

  public void changed(ObserveEvent e) {
  }
}
