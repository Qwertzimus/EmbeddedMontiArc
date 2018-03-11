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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.symboltable.Scope;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectorGetPortTest extends AbstractSymtabTest {


    //@Ignore
    @Test
    public void testGetPorts() throws Exception {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "fas.demo_fas_Fkt_m.fAS", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        testConnectorPorts(inst);
    }

    private void testConnectorPorts(ExpandedComponentInstanceSymbol inst) {
        assertNotNull(inst);

        inst.getConnectors().forEach(con -> {
            assertNotEquals(con.getSource(), con.getTarget());
            assertNotEquals(con.getSourcePort().getFullName(), con.getTargetPort().getFullName());
        });

        inst.getSubComponents().forEach(this::testConnectorPorts);
    }
}
