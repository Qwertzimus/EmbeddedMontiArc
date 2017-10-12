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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponentHead;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentHeadCoCo;
//import de.monticore.types.types._ast.ASTTypeParameters;
//import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.lang.monticar.types2._ast.ASTTypeParameters;
import de.monticore.lang.monticar.types2._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author (last commit) Crispin Kirchner
 */
public class TypeParameterNamesUnique implements EmbeddedMontiArcASTComponentHeadCoCo {

    /**
     * @see de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo#check(de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent)
     */
    @Override
    public void check(ASTComponentHead node) {
        ASTTypeParameters typeParameters = node.getGenericTypeParameters().orElse(null);
        if (typeParameters == null) {
            return;
        }

        List<String> typeParameterNames = new ArrayList<>();
        for (ASTTypeVariableDeclaration typeParameter : typeParameters.getTypeVariableDeclarations()) {

            if (typeParameter.getNamingResolution().isPresent() && typeParameterNames.contains(typeParameter.getNamingResolution().get().getName())) {
                Log.error(String.format(
                        "0x35F1A The formal type parameter name \"%s\" is not unique",
                        typeParameter.getNamingResolution().get().getName()), typeParameter.get_SourcePositionStart());
            } else {
                //typeParameterNames.add(typeParameter.getNamingResolution().get().getName());
            }
        }
    }

}
