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
package de.monticore.lang.embeddedmontiarc;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.AdaptableSymbol;
import de.monticore.lang.tagging._symboltable.TagSymbol;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TaggingTest extends AbstractTaggingResolverTest {

    @Test
    public void testAdaptableSymbolParsing(){
        TaggingResolver taggingResolver = createSymTabAndTaggingResolver("src/test/resources/");

        ComponentSymbol componentSymbol = taggingResolver.<ComponentSymbol>resolve("testing.ConfigPortTag",ComponentSymbol.KIND).orElse(null);
        assertNotNull(componentSymbol);

        PortSymbol in1 = componentSymbol.getIncomingPort("in1").orElse(null);
        PortSymbol in2 = componentSymbol.getIncomingPort("in2").orElse(null);
        assertNotNull(in1);
        assertNotNull(in2);

        Collection<TagSymbol> tags1 = taggingResolver.getTags(in1, AdaptableSymbol.KIND);
        Collection<TagSymbol> tags2 = taggingResolver.getTags(in2, AdaptableSymbol.KIND);
        assertTrue(tags1.size() == 1);
        assertTrue(tags2.isEmpty());

        assertTrue(in1.isConfig());
        assertFalse(in2.isConfig());
    }

}
