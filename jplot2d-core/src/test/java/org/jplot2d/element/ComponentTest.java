package org.jplot2d.element;

import static org.jplot2d.util.TestUtils.*;

import org.jplot2d.env.InterfaceInfo;
import org.junit.*;

/**
 * The class <code>ComponentTest</code> contains tests for the class
 * <code>{@link Component}</code>.
 * 
 * @author Jingjing Li
 */
public class ComponentTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(Component.class);
		checkSet(iinfo.getPropertyMap().keySet(), "fontName", "fontStyle",
				"fontScale", "fontSize", "location", "size", "bounds",
				"environment", "color", "selectable", "parent", "visible",
				"cacheable", "ZOrder");
	}

}
