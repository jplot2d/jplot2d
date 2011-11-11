package org.jplot2d.element;

import static org.jplot2d.util.TestUtils.*;

import org.jplot2d.env.InterfaceInfo;
import org.junit.*;

/**
 * The class <code>ComponentTest</code> contains tests for the class <code>{@link PComponent}</code>
 * .
 * 
 * @author Jingjing Li
 */
public class ComponentTest {

	@Test
	public void testInterfaceInfo() {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(PComponent.class);
		checkCollecionOrder(iinfo.getPropertyInfoGroupMap().keySet(), "Component");
		checkPropertyInfoNames(iinfo.getPropertyInfoGroupMap().get("Component"), "visible",
				"cacheable", "selectable", "ZOrder", "color", "fontName", "fontStyle", "fontSize",
				"fontScale", "location", "size", "bounds");
	}

}
