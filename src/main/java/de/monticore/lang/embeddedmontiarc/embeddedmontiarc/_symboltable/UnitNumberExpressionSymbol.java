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
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.monticar.interfaces.TextualExpression;
import de.monticore.lang.monticar.ranges._ast.ASTUnitNumberExpression;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;

/**
 * Only used for getting expression value
 *
 * @author Sascha Schneiders
 */
public class UnitNumberExpressionSymbol extends CommonSymbol implements TextualExpression {
    protected ASTUnitNumberExpression unitNumberExpression;

    public UnitNumberExpressionSymbol() {
        super("", SymbolKind.KIND);
    }

    public UnitNumberExpressionSymbol(ASTUnitNumberExpression astUnitNumberExpression) {
        super("", SymbolKind.KIND);
        this.unitNumberExpression = astUnitNumberExpression;
    }


    @Override
    public String getTextualRepresentation() {
        String result = "";
        if (unitNumberExpression.getTUnitNumber().isPresent()) {
            result += unitNumberExpression.getTUnitNumber().get();
        } else {
            result += unitNumberExpression.getTUnitInf().get();

        }

        return result;
    }
}
