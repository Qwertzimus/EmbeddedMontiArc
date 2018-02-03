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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTEmbeddedMontiArcNode;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTConnectorCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcCoCoChecker;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigPortTest extends AbstractCoCoTest {

    @BeforeClass
    public static void setUp() {
        Log.enableFailQuick(false);
    }

    @Test
    public void testConfigPortMustNotBeConnected(){
        checkValid("","testing.AdaptableParameterInstance");
    }

    @Test
    public void testConfigPortOnlyIncomingIsConfig(){
        checkValid("","testing.ConfigPort");
    }

    @Test
    public void testConfigPortOutgoingIsConfig(){


        ASTEmbeddedMontiArcNode astNode = getAstNode("", "testing.ConfigPort");

        //set output port to config. Mistake can only happen when using Tagging as parser will catch it
        ComponentSymbol comp = (ComponentSymbol) astNode.getSymbol().get();
        PortSymbol outPort = comp.getOutgoingPort("out1").get();
        outPort.setConfig(true);

        ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(1,"x7FF02");
        checkInvalid(EmbeddedMontiArcCoCos.createChecker(), astNode,expectedErrors);


    }
}
