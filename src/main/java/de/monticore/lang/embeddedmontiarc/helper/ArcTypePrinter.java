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
package de.monticore.lang.embeddedmontiarc.helper;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.TypesPrinter;
//import de.monticore.types.types._ast.ASTReferenceType;
//import de.monticore.types.types._ast.ASTType;
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.types2._ast.ASTReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTSimpleReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTType;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberTypeArgument;
import de.se_rwth.commons.logging.Log;
//import de.se_rwth.commons.logging.Log;

/**
 * TODO: Implement
 *
 * @author Robert Heim
 */
public class ArcTypePrinter {

    /**
     * Converts an ASTType to a String
     *
     * @param type ASTType to be converted
     * @return String representation of "type"
     */
    public static String printType(ASTType type) {
        // TODO ArcTypes?!
        return TypesPrinter.printType(type);
    }

    /**
     * Converts an ASTReferenceType to a String
     *
     * @param astReferenceType to be converted
     * @return String representation of "type"
     */
    public static String printReferenceType(ASTReferenceType astReferenceType) {
        // TODO ArcTypes?!
        return TypesPrinter.printReferenceType(astReferenceType);
    }


    /**
     * Converts an ASTType to a String, but omits type arguments
     *
     * @param typeBound to be converted
     * @return String representation of "type" without type arguments
     */
    public static String printTypeWithoutTypeArgumentsAndDimension(ASTType typeBound) {
        // TODO ArcTypes?!
        return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound);
    }

    public static String printSubComponentName(ASTSubComponent subComponent) {
        String result = "";
        if (subComponent.getType() instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType referenceType = (ASTSimpleReferenceType) subComponent.getType();
            String compNameWithoutPackage = referenceType.getNames().get(referenceType.getNames().size() - 1);
            if (referenceType.getTypeArguments().isPresent()) {
                for (ASTTypeArgument typeArguments : referenceType.getTypeArguments().get().getTypeArguments()) {
                    Log.debug(typeArguments.toString(), "typeArgs");
                    if (typeArguments instanceof ASTUnitNumberTypeArgument) {
                        ASTUnitNumberTypeArgument unitNumberTypeArgument = (ASTUnitNumberTypeArgument) typeArguments;
                        if (unitNumberTypeArgument.getUnitNumber().getNumber().isPresent()) {
                            compNameWithoutPackage += "_"+unitNumberTypeArgument.getUnitNumber().getNumber().get().intValue()+"_";
                            return compNameWithoutPackage;
                        } else
                            Log.debug("0xPRSUCONA1", "No Number present!");
                    } else {

                        Log.debug("No further information","0xPRSUCONA Case not handled!");

                    }
                }

            }
        }
        return printTypeWithoutTypeArgumentsAndDimension(subComponent.getType());
    }
}
