/*
 * Copyright 2010-2014, Sikuli.org, sikulix.com
 * Released under the MIT License.
 *
 * modified RaiMan
 */
package org.sikuli.script;

import java.util.ArrayList;
import java.util.List;

/**
 * provides information about the observed event being in the {@link ObserverCallBack}
 */
public class ObserveEvent {

  public enum Type {
    APPEAR, VANISH, CHANGE, GENERIC
  }

  /**
   * the event's type as ObserveEvent.APPEAR, .VANISH, .CHANGE
   */
  public Type type;

  private Region region = null;
  private Object pattern = null;
  private Match match = null;
  private int index = -1;
  private List<Match> changes = null;
  private long time;
  private String name;
  private Object[] vals = new Object[] {null, null, null};

  protected ObserveEvent() {
  }

  /**
   * INTERNAL USE ONLY: creates an observed event
   */
  protected ObserveEvent(String name, Type type, Object ptn, Object m, Object r, long now) {
    init(name, type, ptn, m, r, now);
  }

	private void init(String name, Type type, Object ptn, Object m, Object r, long now) {
    this.name = name;
    this.type = type;
    time = now;
    if (type == Type.GENERIC) {
      setVals(ptn, m, r);
    } else {
      setRegion(r);
      setMatch(m);
      setPattern(ptn);
    }
	}

  /**
   * for type GENERIC: 3 values can be stored in the event
   * (the value's type is known by creator and user of getVals as some private protocol)
   * @param v1
   * @param v2
   * @param v3
   */
  protected void setVals(Object v1, Object v2, Object v3) {
    vals[0] = v1;
    vals[1] = v2;
    vals[2] = v3;
  }

  /**
   * for type GENERIC: (the value's type is known by creator and user of getVals as some private protocol)
   * @return an array with the 3 stored values (might be null)
   */
  public Object[] getVals() {
    return vals;
  }

  /**
   *
   * @return the observer name of this event
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @return this event's observer's region
   */
  public Region getRegion() {
    return region;
  }

  protected void setRegion(Object r) {
    if (r instanceof Region) {
      region = (Region) r;
    }
  }

  /**
   *
   * @return the observed match (APEAR, VANISH)
   */
  public Match getMatch() {
    return match;
  }

  protected void setMatch(Object m) {
    if (null != m && m instanceof Match) {
      match = new Match((Match) m);
    }
  }

  protected void setIndex(int index) {
    this.index = index;
  }

  /**
   *
   * @return a list of observed changes as matches (CHANGE)
   */
  public List<Match> getChanges() {
    return changes;
  }

  protected void setChanges(List<Match> c) {
    if (c != null) {
      changes = new ArrayList<Match>();
      changes.addAll(c);
    }
  }

  /**
   *
   * @return the used pattern for this event's observing
   */
  public Pattern getPattern() {
    if (null != pattern) {
      if (pattern.getClass().isInstance("")) {
        return (new Pattern((String) pattern));
      } else {
        return (new Pattern((Pattern) pattern));
      }
    }
    return null;
  }

  protected void setPattern(Object p) {
    if (null != p) {
      if (p.getClass().isInstance("")) {
        pattern = new Pattern((String) p);
      } else {
        if (p instanceof Pattern) {
          pattern = new Pattern((Pattern) p);
        }
      }
    }
  }

  public long getTime() {
    return time;
  }

  /**
   * tell the observer to repeat this event's observe action immediately
   * after returning from this handler (APPEAR, VANISH)
   */
  public void repeat() {
    repeat(0);
  }

  /**
   * tell the observer to repeat this event's observe action after given time in secs
   * after returning from this handler (APPEAR, VANISH)
   * @param secs seconds
   */
  public void repeat(long secs) {
    region.getObserver().repeat(name, secs);
  }

  /**
   * @return the number how often this event has already been triggered until now
   */
  public int getCount() {
    return region.getObserver().getCount(name);
  }

  /**
   * stops the observer
   */
  public void stopObserver() {
    region.stopObserver();
  }

  /**
   * stops the observer and prints the given text
   * @param text text
   */
  public void stopObserver(String text) {
    region.stopObserver(text);
  }

  @Override
  public String toString() {
    if (type == Type.CHANGE) {
      return String.format("Event(%s) %s on: %s with: %d count: %d",
            type, name, region, index, getCount());
    } else {
      return String.format("Event(%s) %s on: %s with: %s\nmatch: %s count: %d",
            type, name, region, pattern, match, getCount());
    }
  }
}
