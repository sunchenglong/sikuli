package org.test.mytest;
import org.sikuli.script.*;
import java.awt.*;
import java.awt.event.InputEvent;
public class testsikuli {
	public static void main(String[] args) throws InterruptedException {
		Screen s = new Screen();
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			s.mouseMove("pic/sikuli.png");
			// s.dragDrop("pic/movebegin.png","pic/moveto.png");
			// s.rightClick("pic/moveto.png");
			// s.click("pic/6san.png", 0);
			//s.find("pic/sikuli.png");
			// s.click(s.getLastMatch().right(50));
			//s.find("pic/sikuli.png");
			s.click(s.getLastMatch().left(50));
			// s.wait(2000);
			// s.hover("pic/sikuli.png");
		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}
}
