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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTPort;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTPortCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

public class OnlyIncomingPortIsConfig implements EmbeddedMontiArcASTPortCoCo {

    @Override
    public void check(ASTPort node) {
        Symbol symbol = node.getSymbol().orElse(null);
        if(symbol == null) return;

        if(symbol.isKindOf(PortSymbol.KIND)){
            check((PortSymbol)symbol);
        }
    }

    private void check(PortSymbol symbol) {
        if(symbol.isConfig() && symbol.isOutgoing())
            Log.error("0x7FF02 Config ports can only be incoming!");
    }
}
