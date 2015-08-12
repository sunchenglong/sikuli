/*
 *                       University of New Hampshire
 *                       InterOperability Laboratory
 *                           Copyright (c) 2014
 *
 * This software is provided by the IOL ``AS IS'' and any express or implied
 * warranties, including, but not limited to, the implied warranties of
 * merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the InterOperability Lab be liable for any direct,
 * indirect, incidental, special, exemplary, or consequential damages.
 *
 * This software may not be resold without the express permission of
 * the InterOperability Lab.
 *
 * Feedback on this code may be sent to Mike Johnson (mjohnson@iol.unh.edu)
 * and dlnalab@iol.unh.edu.
 */
package edu.unh.iol.dlc;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import org.sikuli.basics.Debug;
import org.sikuli.basics.FileManager;
import org.sikuli.script.RunTime;
import org.sikuli.basics.Settings;
import org.sikuli.util.EventObserver;
import org.sikuli.util.EventSubject;
import org.sikuli.script.IRobot;
import org.sikuli.script.IScreen;
import org.sikuli.script.Location;
import org.sikuli.script.Mouse;
import org.sikuli.util.OverlayCapturePrompt;
import org.sikuli.script.Region;
import org.sikuli.util.ScreenHighlighter;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.ScreenUnion;
import org.sikuli.script.Sikulix;

/**
 * The VNCScreen is an implementation of IScreen that uses a VNCRobot to
 * control the VNC connection.
 */
public class VNCScreen extends Region implements EventObserver, IScreen {

	private static String me = "VNCScreen: ";
	private static int lvl = 3;
	private static void log(int level, String message, Object... args) {
		Debug.logx(level, me + message, args);
	}

	protected static int _primaryScreen = -1;

	protected static Framebuffer[] _gdev;
	protected static Rectangle[] gdevsBounds;
	protected static ConnectionController _genv = null;
	protected static VNCScreen[] screens;
	private static VNCRobot[] mouseRobot;
	private static int waitForScreenshot = 300;

	protected Framebuffer _curGD;
	protected int _curID = 0;
	protected int oldID = 0;
	protected IRobot robot = null;
	protected boolean waitPrompt;
	protected OverlayCapturePrompt prompt;
	private ScreenImage lastScreenImage = null;
	private String promptMsg = "Select a region on the screen";

//Screen Methods**************************************************************/

	static{
		RunTime.loadLibrary("VisionProxy");
	    initScreens(false);
	}

	private static void initScreens(boolean reset) {

		log(lvl+1, "initScreens: entry");
		if (_genv != null && !reset) {
			return;
		}

		_genv = ConnectionController.getActiveController(0);
		if(_genv == null){
			Debug.error("Did not find any active ConnectionControllers.  " +
					"Cannot use VNCScreen without a ConnectionController instance.");
			Sikulix.terminate(999);
		}
		_gdev = (Framebuffer[])_genv.getScreenDevices();

		gdevsBounds = new Rectangle[_gdev.length];
		screens = new VNCScreen[_gdev.length];

		if (_gdev.length == 0) {
			Debug.error("VNCScreen: initScreens: GraphicsEnvironment has no screens");
			Sikulix.terminate(999);
		}

		_primaryScreen = -1;

		for (int i = 0; i < getNumberScreens(); i++) {
			gdevsBounds[i] = _gdev[i].getDefaultConfiguration().getBounds();

			if (gdevsBounds[i].contains(new Point(0, 0))) {
				if (_primaryScreen < 0) {
					_primaryScreen = i;
					log(lvl, "initScreens: ScreenDevice %d contains (0,0) --- will be used as primary", i);
				}
				else {
					log(lvl, "initScreens: ScreenDevice %d too contains (0,0)!", i);
				}
			}
		}

		if (_primaryScreen < 0) {
			Debug.log("Screen: initScreens: no ScreenDevice contains (0,0) --- using first ScreenDevice as primary");
			_primaryScreen = 0;
		}

		log(lvl+1, "initScreens: after GD evaluation");
		for (int i = 0; i < screens.length; i++) {
			screens[i] = new VNCScreen(i, true);
			screens[i].initScreen();
		}
		try {
			log(lvl+1, "initScreens: getting mouseRobot");

			mouseRobot = new VNCRobot[screens.length];

			for(int i = 0; i < screens.length; i++){
				mouseRobot[i] = new VNCRobot(_gdev[i]);
				mouseRobot[i].setAutoDelay(10);
			}
		}
		catch (AWTException e) {
			Debug.error("Can't initialize global Robot for Mouse: " + e.getMessage());
			Sikulix.terminate(999);
		}

		if (!reset) {
			log(lvl - 1, "initScreens: basic initialization (%d VNCScreen(s) found)", _gdev.length);
			log(lvl, "*** monitor configuration (primary: %d) ***", _primaryScreen);
			for (int i = 0; i < _gdev.length; i++) {
				log(lvl, "%d: %s", i, screens[i].toStringShort());
			}
			log(lvl, "*** end monitor configuration ***");
		}

		//im not sure if this is valid for VNCScreens yet,
		//with multiple normal screens there is only one mouse
		//but with VNCScreens there is a separate mouse for each
		//screen
		if (getNumberScreens() > 1) {
			log(lvl, "initScreens: multi monitor mouse check");
			Location lnow = Mouse.at();
			float mmd = Settings.MoveMouseDelay;
			Settings.MoveMouseDelay = 0f;
			Location lc = null, lcn = null;
			for (VNCScreen s : screens) {
				lc = s.getCenter();
				Mouse.move(lc);
				lcn = Mouse.at();
				if (!lc.equals(lcn)) {
					log(lvl, "*** multimonitor click check: %s center: (%d, %d) --- NOT OK:  (%d, %d)",
							s.toStringShort(), lc.x, lc.y, lcn.x, lcn.y);
				}
				else {
					log(lvl, "*** checking: %s center: (%d, %d) --- OK", s.toStringShort(), lc.x, lc.y);
				}
			}
			Mouse.move(lnow);
			Settings.MoveMouseDelay = mmd;
		}
	}

	protected static VNCRobot getMouseRobot(){
		return mouseRobot[0];
	}

	public static ScreenUnion all() {
		return new ScreenUnion();
	}

	public static int getNumberScreens(){
	    return _gdev.length;
	}

	private static int getValidID(int id) {
		if (id < 0 || id >= _gdev.length) {
			Debug.error("VNCScreen: invalid screen id %d - using primary screen", id);
			return _primaryScreen;
		}
		return id;
	}

	public static int getPrimaryId() {
		return _primaryScreen;
	}


	public static VNCScreen getPrimaryScreen() {
		return screens[_primaryScreen];
	}


	public static VNCScreen getScreen(int id) {
		return screens[getValidID(id)];
	}

	public static Rectangle getBounds(int id) {
		return gdevsBounds[getValidID(id)];
	}

	public static IRobot getRobot(int id) {
		return getScreen(id).getRobot();
	}

	public static void showMonitors() {
		Debug.info("*** monitor configuration [ %s VNCScreen(s)] ***", VNCScreen.getNumberScreens());
		Debug.info("*** Primary is VNCScreen %d", _primaryScreen);
		for (int i = 0; i < _gdev.length; i++) {
			Debug.info("Screen %d: %s", i, VNCScreen.getScreen(i).toStringShort());
		}
		Debug.info("*** end monitor configuration ***");
	}

	public static void resetMonitors() {
	    Debug.error("*** BE AWARE: experimental - might not work ***");
	    Debug.error("Re-evaluation of the monitor setup has been requested");
	    Debug.error("... Current Region/Screen objects might not be valid any longer");
	    Debug.error("... Use existing Region/Screen objects only if you know what you are doing!");
	    initScreens(true);
	    Debug.info("*** new monitor configuration [ %s Screen(s)] ***", VNCScreen.getNumberScreens());
	    Debug.info("*** Primary is VNCScreen %d", _primaryScreen);
	    for (int i = 0; i < _gdev.length; i++) {
	      Debug.info("VNCScreen %d: %s", i, VNCScreen.getScreen(i).toStringShort());
	    }
	    Debug.error("*** end new monitor configuration ***");
	}

	public VNCScreen() {
		super();
		_curID = _primaryScreen;
		initScreen();
		super.initScreen(this);
	}

	public VNCScreen(int id){
		super();
		//this needs to be called because while with a normal screen it
		//is not expected that a new one will be connected during the normal
		//operation of the program, a VNCScreen can be arbitratily connected and
		//disconnected so _gdev needs to be updated to a new size
		initScreens(true);
	    if(id < 0 || id >= _gdev.length) {
	    	throw new IllegalArgumentException("VNCScreen ID " + id + " not in valid range " +
	    			"(between 0 and " + (_gdev.length - 1));
	    }
	    _curID = id;
	    initScreen();
		super.initScreen(this);
	}

	public VNCScreen(int id, boolean b) {
		super();
		_curID = id;
		initScreen();
		super.initScreen(this);
	}

	public VNCScreen(boolean isScreenUnion) {
		super();
		initScreen();
		super.initScreen(this);
	}

	private void initScreen() {
		setOtherScreen();
		_curGD = _gdev[_curID];
		Rectangle bounds = getBounds();
			x = (int) bounds.getX();
			y = (int) bounds.getY();
			w = (int) bounds.getWidth();
			h = (int) bounds.getHeight();

		try {
			robot = new VNCRobot(_curGD);
			robot.setAutoDelay(10);
		}
		catch (AWTException e) {
			Debug.error("Can't initialize Java Robot on VNCScreen " + _curID + ": " + e.getMessage());
			robot = null;
		}
	}

	public void setAsScreenUnion() {
		oldID = _curID;
		_curID = -1;
	}

	public void setAsScreen() {
		_curID = oldID;
	}

	@Override
	public void initScreen(IScreen scr){
		updateSelf();
	}

	@Override
	public IScreen getScreen(){
		return this;
	}


	@Override
	protected Region setScreen(IScreen s) {
		throw new UnsupportedOperationException("The setScreen() method cannot be called from a VNCScreen object.");
	}

	protected boolean useFullscreen() {
		return false;
	}

	@Override
	public int getID() {
		return _curID;
	}

	@Override
	public int getIdFromPoint(int x, int y) {
		return _curID;
	}

	public GraphicsDevice getGraphicsDevice() {
		return _curGD;
	}

	@Override
	public IRobot getRobot() {
		return robot;
	}

	@Override
	public Rectangle getBounds() {
		return gdevsBounds[_curID];
	}

	public Region newRegion(Location loc, int width, int height) {
		//return Region.create(loc.copyTo(this), width, height);
		return new Region(loc.x, loc.y, width, height, loc.getScreen());
	}

	@Override
	public ScreenImage getLastScreenImageFromScreen() {
		return lastScreenImage;
	}

	public Location newLocation(Location loc) {
		return loc.setOtherScreen(this);
	}

	@Override
	public ScreenImage capture() {
		return capture(getRect());
	}

	@Override
	public ScreenImage capture(int x, int y, int w, int h) {
		Rectangle rect = newRegion(new Location(x, y), w, h).getRect();
		return capture(rect);
	}

	@Override
	public ScreenImage capture(Rectangle rect) {
		log(lvl + 1, "VNCScreen.capture: (%d,%d) %dx%d", rect.x, rect.y, rect.width, rect.height);
		ScreenImage simg = robot.captureScreen(rect);
		lastScreenImage = simg;
		return simg;
	}

	@Override
	public ScreenImage capture(Region reg) {
		return capture(reg.getRect());
	}

	public ScreenImage userCapture() {
		return userCapture(promptMsg);
	}

	@Override
	public ScreenImage userCapture(final String msg) {
		waitPrompt = true;
		Thread th = new Thread() {

			@Override
			public void run() {
				if ("".equals(msg)) {
					prompt = new OverlayCapturePrompt(null, VNCScreen.this);
					prompt.prompt(promptMsg);
				}
				else {
					prompt = new OverlayCapturePrompt(VNCScreen.this, VNCScreen.this);
					prompt.prompt(msg);
				}
			}
		};

		th.start();

		try {
			int count = 0;
			while (waitPrompt) {
				Thread.sleep(100);
				if (count++ > waitForScreenshot) {
					return null;
				}
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		ScreenImage ret = prompt.getSelection();
		lastScreenImage = ret;
		prompt.close();
		return ret;
	}

	public Region selectRegion() {
		return selectRegion("Select a region on the screen");
	}

	public Region selectRegion(final String msg) {
		ScreenImage sim = userCapture(msg);
		if (sim == null) {
			return null;
		}
		Rectangle r = sim.getROI();
		return Region.create((int) r.getX(), (int) r.getY(),
				(int) r.getWidth(), (int) r.getHeight());
	}

	@Override
	public void update(EventSubject s) {
		waitPrompt = false;
	}

	@Override
	public void showTarget(Location loc) {
		showTarget(loc, Settings.SlowMotionDelay);
	}

	protected void showTarget(Location loc, double secs) {
		if (Settings.isShowActions()) {
			ScreenHighlighter overlay = new ScreenHighlighter(this, null);
			overlay.showTarget(loc, (float) secs);
		}
	}

	@Override
	public boolean isOtherScreen(){
		return otherScreen;
	}

	@Override
	public Rectangle getRect(){
		return new Rectangle(x, y, w, h);
	}

	@Override
	public int getX(){
		return (int) getBounds().getX();
	}

	@Override
	public int getY(){
		return (int) getBounds().getY();
	}

	@Override
	public int getW(){
		return (int) getBounds().getWidth();
	}

	@Override
	public int getH(){
		return (int) getBounds().getHeight();
	}

	@Override
	public String toString() {
		Rectangle r = getBounds();
		return String.format("S(%d)[%d,%d %dx%d] E:%s, T:%.1f",
				_curID, (int) r.getX(), (int) r.getY(),
				(int) r.getWidth(), (int) r.getHeight(),
				getThrowException() ? "Y" : "N", getAutoWaitTimeout());
	}

	@Override
	public String toStringShort(){
		Rectangle r = getBounds();
	    return String.format("S(%d)[%d,%d %dx%d]",
	            _curID, (int) r.getX(), (int) r.getY(),
	            (int) r.getWidth(), (int) r.getHeight());
	}
}
