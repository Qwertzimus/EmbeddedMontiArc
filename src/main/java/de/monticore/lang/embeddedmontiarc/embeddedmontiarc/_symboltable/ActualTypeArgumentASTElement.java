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

import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.TypeReference;

/**
 * @author Sascha Schneiders
 */
public class ActualTypeArgumentASTElement extends ActualTypeArgument {

    ASTTypeArgument astTypeArguments;

    public ActualTypeArgumentASTElement(boolean isLowerBound, boolean isUpperBound, TypeReference<? extends TypeSymbol> type) {
        super(isLowerBound, isUpperBound, type);
    }

    public ActualTypeArgumentASTElement(TypeReference<? extends TypeSymbol> type) {
        super(type);
    }

    public ASTTypeArgument getAstTypeArguments() {
        return astTypeArguments;
    }

    public ActualTypeArgumentASTElement setAstTypeArguments(ASTTypeArgument astTypeArguments) {
        this.astTypeArguments = astTypeArguments;
        return this;
    }
}
