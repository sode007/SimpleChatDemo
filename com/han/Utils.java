package com.han;

import java.awt.GridBagConstraints;

public class Utils {
	/**
	 * 获取网格包对象
	 * 
	 * @param gridx
	 * @param girdy
	 * @param gridwidth
	 * @param gridheight
	 * @param weightx
	 * @param weighty
	 * @param fill
	 * @param anchor
	 * @return
	 */

	public final static GridBagConstraints getGridBagconstraints(int gridx, int girdy,
			int gridwidth, int gridheight, int weightx, int weighty, int fill,
			int anchor) {
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = gridx;
		cons.gridy = girdy;
		cons.gridwidth = gridwidth;
		cons.gridheight = gridheight;
		cons.weightx = weightx;
		cons.weighty = weighty;
		cons.fill = fill;
		cons.anchor = anchor;
		return cons;
	}
}
