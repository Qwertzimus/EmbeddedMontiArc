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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.MiddlewareSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.RosConnectionSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.logging.Log;

public class InRosPortRosSender implements EmbeddedMontiArcASTComponentCoCo {

    @Override
    public void check(ASTComponent node) {
        Symbol symbol = node.getSymbol().orElse(null);
        if(symbol.isKindOf(ComponentSymbol.KIND)){
            check((ComponentSymbol) symbol);
        }

    }

    private void check(ComponentSymbol symbol) {
        symbol.getConnectors().forEach(connector -> {

            PortSymbol source = null;
            PortSymbol target = null;
            try {
                source = connector.getSourcePort();
                target = connector.getTargetPort();
            } catch (ResolvedSeveralEntriesException ignored) {
                //needed so that other invalid coco tests dont fail(e.g. UniquePortsTest)
                Log.warn(ignored.getMessage());
            }

            if(source == null || target == null){
                Log.warn("Could not resolve target or source!");
                return;
            }

            RosConnectionSymbol sourceTag = (RosConnectionSymbol) source.getMiddlewareSymbol()
                    .filter(mws -> mws.isKindOf(RosConnectionSymbol.KIND)).orElse(null);

            RosConnectionSymbol targetTag = (RosConnectionSymbol) target.getMiddlewareSymbol()
                    .filter(mws -> mws.isKindOf(RosConnectionSymbol.KIND)).orElse(null);

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
