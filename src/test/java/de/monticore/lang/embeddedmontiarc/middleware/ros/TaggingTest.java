package de.monticore.lang.embeddedmontiarc.middleware.ros;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.RosConnectionSymbol;
import de.monticore.lang.tagging._symboltable.TagSymbol;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TaggingTest extends AbstractTaggingResolverTest {

    @Test
    public void testRosConnectionParsing() {
        TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");

        ExpandedComponentInstanceSymbol component = symtab.<ExpandedComponentInstanceSymbol>resolve("middleware.ros.basicParsing", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(component);

        //rosIn
        PortSymbol rosIn = component.getPort("rosIn").orElse(null);
        assertNotNull(rosIn);

        Collection<TagSymbol> tags = symtab.getTags(rosIn, RosConnectionSymbol.KIND);
        assertTrue(tags.size() == 1);

        RosConnectionSymbol tag = (RosConnectionSymbol) tags.iterator().next();
        assertEquals(tag.getTopicName(), "/clock");
        assertEquals(tag.getTopicType(), "rosgraph_msgs/Clock");
        assertEquals(tag.getMsgField().get(), "clock.toSec()");

        //rosOut
        PortSymbol rosOut = component.getPort("rosOut").orElse(null);
        assertNotNull(rosOut);

        tags = symtab.getTags(rosOut, RosConnectionSymbol.KIND);
        assertTrue(tags.size() == 1);

        tag = (RosConnectionSymbol) tags.iterator().next();
        assertEquals(tag.getTopicName(), "/echo");
        assertEquals(tag.getTopicType(), "automated_driving_msgs/StampedFloat64");
        assertEquals(tag.getMsgField().get(), "data");

    }

    @Test
    public void testRosConnectionParsingOptionalMsgField() {
        TaggingResolver symtab = createSymTabAndTaggingResolver("src/test/resources");

        ExpandedComponentInstanceSymbol component = symtab.<ExpandedComponentInstanceSymbol>resolve("middleware.ros.optionalMsgField", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(component);

        PortSymbol in1 = component.getPort("in1").orElse(null);
        PortSymbol out1 = component.getPort("out1").orElse(null);

        assertNotNull(in1);
        assertNotNull(out1);

        RosConnectionSymbol tagIn1 = (RosConnectionSymbol) symtab.getTags(in1, RosConnectionSymbol.KIND).stream().findFirst().orElse(null);
        RosConnectionSymbol tagOut1 = (RosConnectionSymbol) symtab.getTags(out1, RosConnectionSymbol.KIND).stream().findFirst().orElse(null);

        assertNotNull(tagIn1);
        assertNotNull(tagOut1);

        assertFalse(tagIn1.getMsgField().isPresent());
        assertTrue(tagOut1.getMsgField().isPresent());
        assertTrue(tagOut1.getMsgField().get().equals("msgField1"));

    }
}
