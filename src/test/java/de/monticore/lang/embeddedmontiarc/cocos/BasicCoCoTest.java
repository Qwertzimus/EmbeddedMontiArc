package de.monticore.lang.embeddedmontiarc.cocos;

import org.junit.Test;

/**
 * @author Sascha Schneiders
 */
public class BasicCoCoTest extends AbstractCoCoTest {

    @Test
    public void testBasicTests(){
        checkValid("", "testing.BasicParameter");
        checkValid("", "testing.BasicParameterInstance");
        checkValid("", "testing.BasicResolution");
        checkValid("", "testing.BasicResolutionDefaultInstance");
        checkValid("", "testing.BasicTypeInstance");
        checkValid("", "testing.BasicTypeTest");
        checkValid("", "testing.BooleanConnector");
        checkValid("", "testing.BooleanPortType");
        /*checkValid("", "testing.ComponentArray");
        checkValid("", "testing.ConnectorArray");
        checkValid("", "testing.ConnectorArraymn");
        checkValid("", "testing.ConnectorInstancing");*/
    }
}
