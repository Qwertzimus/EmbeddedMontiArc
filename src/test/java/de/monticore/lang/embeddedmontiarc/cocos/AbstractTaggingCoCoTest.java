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
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcCoCoChecker;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.middleware.ros.AbstractTaggingResolverTest;
import de.monticore.lang.embeddedmontiarc.tagging.RosConnectionSymbol;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractTaggingCoCoTest extends AbstractTaggingResolverTest {

    private void resolveTags(TaggingResolver taggingResolver, ComponentSymbol componentSymbol){
        taggingResolver.getTags(componentSymbol, RosConnectionSymbol.KIND);
        componentSymbol.getSubComponents().forEach(sub -> resolveTags(taggingResolver,sub.getComponentType().getReferencedSymbol()));
    }

    public void testCoCosOnComponent(String componentName, String... expectedErrors) {
        TaggingResolver taggingResolver = createSymTabAndTaggingResolver("src/test/resources/");

        ComponentSymbol component = taggingResolver.<ComponentSymbol>resolve(componentName, ComponentSymbol.KIND).orElse(null);
        assertNotNull(component);

        resolveTags(taggingResolver,component);

        ASTNode tmpNode = component.getAstNode().orElse(null);
        assertNotNull(tmpNode);
        ASTComponent astComponent = tmpNode instanceof ASTComponent ? (ASTComponent) tmpNode : null;
        assertNotNull(astComponent);

        EmbeddedMontiArcCoCoChecker checker = EmbeddedMontiArcCoCos.createChecker();
        checker.checkAll(astComponent);

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
