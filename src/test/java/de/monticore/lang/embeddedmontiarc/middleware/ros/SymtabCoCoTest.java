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

import de.monticore.lang.embeddedmontiarc.cocos.EmbeddedMontiArcCoCos;
import de.monticore.lang.embeddedmontiarc.cocos.EmbeddedMontiArcSTCoCoChecker;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.RosConnectionSymbol;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SymtabCoCoTest extends AbstractTaggingResolverTest {

    @BeforeClass
    public static void init() {
        Log.enableFailQuick(false);
    }

    @Before
    public void setUp() {
        Log.getFindings().clear();
    }

    @Test
    public void testValidRosToRos() {
        testCoCo("middleware.ros.cocos.rosToRosComp");
    }

    @Test
    public void testNoRosToRos() {
        testCoCo("middleware.ros.cocos.noRosToRosComp", "0x3830a");
    }

    @Test
    public void testTopicNameMismatch() {
        testCoCo("middleware.ros.cocos.topicNameMismatch", "0x23a0d");
    }

    @Test
    public void testTopicTypeMismatch() {
        testCoCo("middleware.ros.cocos.topicTypeMismatch", "0x31f6e");
    }

    public void resolveTags(TaggingResolver taggingResolver,ExpandedComponentInstanceSymbol expandedComponentInstanceSymbol){
        expandedComponentInstanceSymbol.getPorts().forEach(p -> taggingResolver.getTags(p,RosConnectionSymbol.KIND));
        expandedComponentInstanceSymbol.getSubComponents().forEach(sub -> resolveTags(taggingResolver,sub));
    }

    //TODO: check findings for error msgs not just presence
    public void testCoCo(String componentInstanceName, String... expectedErrors) {
        TaggingResolver taggingResolver = createSymTabAndTaggingResolver("src/test/resources/");

        ExpandedComponentInstanceSymbol component = taggingResolver.<ExpandedComponentInstanceSymbol>resolve(componentInstanceName, ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(component);

        resolveTags(taggingResolver,component);

        EmbeddedMontiArcSTCoCoChecker symtabCoCoChecker = EmbeddedMontiArcCoCos.createSTChecker();
        symtabCoCoChecker.checkAll(component);

        List<String> findings = Log.getFindings().stream().map(Finding::getMsg).collect(Collectors.toList());
        Arrays.stream(expectedErrors)
                .forEach(e -> {
                    boolean found = false;
                    for(String f : findings){
                        if(f.contains(e)){
                            found = true;
                            break;
                        }
                    }
                    assertTrue(found);
                });
    }

}
