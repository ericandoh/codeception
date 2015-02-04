package com.me.panels;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.me.codeception.CodeGame;

public class CustomScrollableLabel extends CustomLabel {
	private static final int MAX_LINES = 20;
	private ScrollPane pane;
	public CustomScrollableLabel(CodeGame g, String n) {
		super(g, n);
		// TODO Auto-generated constructor stub
	}
	public CustomScrollableLabel(CodeGame g) {
		//makes a label with 20 scrolling lines
		super(g, "");
		String x = "";
		for (int i = 0; i < MAX_LINES; i++) {
			x = x + "\n";
		}
		setText(x);
	}
	public void setPane(ScrollPane p) {
		pane = p;
	}
	public void append(String t) {
		super.append(t);
		//pane.setScrollY(pane.getMaxY()+500);
		//pane.pack();
		//pane.invalidate();
		if (pane != null) {
			pane.setScrollY(pane.getMaxY());
			pane.validate();
		}
		/*System.out.println(pane.getScrollY());
		System.out.println(pane.getScrollPercentY());
		System.out.println(pane.getMaxY());*/
		//scroll.scrollTo(0, getHeight() - pane.getHeight(), pane.getWidth(), pane.getHeight()) so that that area is visible
		//try scroll.setScrollY(getHeight() - pane.getHeight());
		//try scroll.setScrollPercentX(50);
	}
}
