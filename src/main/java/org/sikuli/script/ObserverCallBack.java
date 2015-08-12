/*
 * Copyright 2010-2014, Sikuli.org, sikulix.com
 * Released under the MIT License.
 *
 * modified RaiMan
 */
package org.sikuli.script;

import java.lang.reflect.Method;
import java.util.EventListener;
import org.sikuli.basics.Debug;
import org.sikuli.util.JythonHelper;

/**
 * Use this class to implement call back methods for the Region observers
 * onAppear, onVanish and onChange. <br>
 * by overriding the contained empty methods appeared, vanished and changed
 * <pre>
 * example:
 * aRegion.onAppear(anImage,
 *   new ObserverCallBack() {
 *     appeared(ObserveEvent e) {
 *       // do something
 *     }
 *   }
 * );
 * </pre>
 when the image appears, your above call back appeared() will be called
 see {@link ObserveEvent} about the features available in the callback function
 */
public class ObserverCallBack implements EventListener {

  Object callback = null;
  ObserveEvent.Type obsType = ObserveEvent.Type.GENERIC;
  Object scriptRunner = null;
  String scriptRunnerType = null;
  Method doSomethingSpecial = null;

	public ObserverCallBack(Object callback, ObserveEvent.Type obsType) {
		this.callback = callback;
		this.obsType = obsType;
		if (callback.getClass().getName().contains("org.python")) {
			scriptRunnerType = "jython";
			scriptRunner = JythonHelper.get();
		} else {
//TODO implement JRubyHelper
			try {
				if (callback.getClass().getName().contains("org.jruby")) {
					scriptRunnerType = "jruby";
				}
				if (scriptRunnerType != null) {
					Class Scripting = Class.forName("org.sikuli.scriptrunner.ScriptingSupport");
					Method getRunner = Scripting.getMethod("getRunner",
									new Class[]{String.class, String.class});
					scriptRunner = getRunner.invoke(Scripting, new Object[]{null, scriptRunnerType});
					if (scriptRunner != null) {
						doSomethingSpecial = scriptRunner.getClass().getMethod("doSomethingSpecial",
										new Class[]{String.class, Object[].class});
					}
				} else {
					Debug.error("ObserverCallBack: no valid callback: %s", callback);
				}
			} catch (Exception ex) {
				Debug.error("ObserverCallBack: %s init: ScriptRunner not available for %s", obsType, scriptRunnerType);
				scriptRunner = null;
			}
		}
	}

  public void appeared(ObserveEvent e) {
    if (scriptRunner != null && ObserveEvent.Type.APPEAR.equals(obsType)) {
      run(e);
    }
  }

  public void vanished(ObserveEvent e) {
    if (scriptRunner != null && ObserveEvent.Type.VANISH.equals(obsType)) {
      run(e);
    }
  }

  public void changed(ObserveEvent e) {
    if (scriptRunner != null && ObserveEvent.Type.CHANGE.equals(obsType)) {
      run(e);
    }
  }

  public void happened(ObserveEvent e) {
    if (scriptRunner != null && ObserveEvent.Type.GENERIC.equals(obsType)) {
      run(e);
    }
  }

  private void run(ObserveEvent e) {
    boolean success = true;
		Object[] args = new Object[] {callback, e};
		if (scriptRunnerType == "jython") {
			success = ((JythonHelper) scriptRunner).runObserveCallback(args);
		} else {
			String msg = "IScriptRunner: doSomethingSpecial returned false";
			try {
				if (scriptRunner != null) {
					success = (Boolean) doSomethingSpecial.invoke(scriptRunner, new Object[]{"runObserveCallback", args});
				}
			} catch (Exception ex) {
				success = false;
				msg = ex.getMessage();
			}
			if (!success) {
				Debug.error("ObserverCallBack: problem with scripting handler: %s\n%s\n%s", scriptRunner, callback, msg);
			}
		}
  }

  public ObserverCallBack() {
  }
}
