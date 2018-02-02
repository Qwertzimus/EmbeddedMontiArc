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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.RosConnectionSymbol;
import de.se_rwth.commons.logging.Log;

public class InRosPortRosSender implements EmbeddedMontiArcExpandedComponentInstanceSymbolCoCo {

    @Override
    public void check(ExpandedComponentInstanceSymbol symbol) {
        symbol.getConnectors().forEach(connectorSymbol -> {

            PortSymbol source = connectorSymbol.getSourcePort();
            PortSymbol target = connectorSymbol.getTargetPort();

            RosConnectionSymbol sourceTag = source.getRosConnectionSymbol().orElse(null);
            RosConnectionSymbol targetTag = target.getRosConnectionSymbol().orElse(null);

            if (targetTag != null) {
                if (sourceTag != null) {
                    if (!targetTag.getTopicName().equals(sourceTag.getTopicName())) {
                        Log.error("0x23a0d Topic name mismatch: " + source.getFullName() + " and " + target.getFullName());
                    }

                    if (!targetTag.getTopicType().equals(sourceTag.getTopicType())) {
                        Log.error("0x31f6e Topic type mismatch: "+source.getFullName()+" and " +target.getFullName());
                    }
                } else {
                    Log.error("0x3830a Connector: target is ros port but source " + source.getFullName() + " is not!");
                }

            }
        });
    }
}
