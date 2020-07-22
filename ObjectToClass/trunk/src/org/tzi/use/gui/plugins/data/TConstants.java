package org.tzi.use.gui.plugins.data;

import java.awt.BasicStroke;
import java.awt.Color;

public class TConstants {
	public final static int DEFAULT_ID = 0;
	public final static int MIN_ID_OBJECT = 1000;
	public final static int MAX_ID_OBJECT = 1999;
	public final static int MIN_ID_CLASS = 2000;
	public final static int MAX_ID_CLASS = 2999;
	public final static int MIN_ID_LINK = 3000;
	public final static int MAX_ID_LINK = 3999;
	public final static int MIN_ID_ASSOCIATION = 4000;
	public final static int MAX_ID_ASSOCIATION = 4999;

	public final static Color GRAY_NODE_COLOR = Color.decode("#DDDDDD");
	public final static Color GRAY_SELECTED_NODE_COLOR = GRAY_NODE_COLOR.darker();
	public final static Color GRAY_EDGE_COLOR = Color.decode("#000000");
	public final static Color GRAY_SELECTED_EDGE_COLOR = Color.decode("#777777");
	public final static Color DEFAULT_COLOR = Color.decode("#33CCCC");
	public final static Color DEFAULT_SELECTED_COLOR = DEFAULT_COLOR.darker();
	public final static Color COMPLETE_COLOR = Color.decode("#99CC00");
	public final static Color COMPLETE_SELECTED_COLOR = COMPLETE_COLOR.darker();
	public final static Color MISSING_COLOR = Color.decode("#FFAA00");
	public final static Color MISSING_SELECTED_COLOR = MISSING_COLOR.darker();
	public final static Color CONFLICT_COLOR = Color.decode("#DD2222");
	public final static Color CONFLICT_SELECTED_COLOR = CONFLICT_COLOR.darker();

	public final static String PLACEHOLDER = "<?>";
	public final static String CONFLICT_MARKER = "<!>";
	public final static String OPTIONAL_MARKER = "<+>";

	public final static String LINK_PLACEHOLDER_NAME = OPTIONAL_MARKER;
	public final static String LINK_PLACEHOLDER_ROLE1 = OPTIONAL_MARKER;
	public final static String LINK_PLACEHOLDER_ROLE2 = OPTIONAL_MARKER;
	public final static String ASSOCIATION_PLACEHOLDER_NAME = PLACEHOLDER;
	public final static String ASSOCIATION_PLACEHOLDER_ROLE1 = PLACEHOLDER;
	public final static String ASSOCIATION_PLACEHOLDER_ROLE2 = PLACEHOLDER;

	public final static BasicStroke defaultBasicStroke = new BasicStroke();
	public final static float boxDashingPattern[] = { 6, 3 };
	public final static BasicStroke dashedBoxBasicStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 5, boxDashingPattern, 0);
	public final static float lineDashingPattern[] = { 8, 6 };
	public final static BasicStroke dashedLineBasicStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 5, lineDashingPattern, 0);
}