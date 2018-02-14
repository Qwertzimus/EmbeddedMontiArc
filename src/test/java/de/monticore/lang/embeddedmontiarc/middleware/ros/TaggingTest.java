/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.embeddedmontiarc.middleware.ros;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.RosConnectionSymbol;
import de.monticore.lang.tagging._symboltable.TagSymbol;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;

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
        assertEquals(tag.getTopicName().get(), "/clock");
        assertEquals(tag.getTopicType().get(), "rosgraph_msgs/Clock");
        assertEquals(tag.getMsgField().get(), "clock.toSec()");

        //rosOut
        PortSymbol rosOut = component.getPort("rosOut").orElse(null);
        assertNotNull(rosOut);

        tags = symtab.getTags(rosOut, RosConnectionSymbol.KIND);
        assertTrue(tags.size() == 1);

        tag = (RosConnectionSymbol) tags.iterator().next();
        assertEquals(tag.getTopicName().get(), "/echo");
        assertEquals(tag.getTopicType().get(), "automated_driving_msgs/StampedFloat64");
        assertEquals(tag.getMsgField().get(), "data");

        //emptyTagIn
        PortSymbol emptyTagIn = component.getPort("emptyTagIn").orElse(null);
        assertNotNull(emptyTagIn);

        tags = symtab.getTags(emptyTagIn, RosConnectionSymbol.KIND);
        assertTrue(tags.size() == 1);

        tag = (RosConnectionSymbol) tags.iterator().next();
        assertEquals(tag.getTopicName(), Optional.empty());
        assertEquals(tag.getTopicType(), Optional.empty());
        assertEquals(tag.getMsgField(), Optional.empty());
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
