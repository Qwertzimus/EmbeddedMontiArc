/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.se_rwth.commons.Names;
import de.monticore.lang.monticar.types2._ast.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by dennisqiao on 3/8/17.
 */
public class TypesHelper {
    public static final String OPTIONAL = "Optional";

    public TypesHelper() {
    }

    public static boolean isOptional(ASTType type) {
        return isGenericTypeWithOneTypeArgument(type, "Optional");
    }

    public static boolean isPrimitive(ASTType type) {
        return type instanceof ASTPrimitiveType;
    }

    public static ASTTypeArgument getReferenceTypeFromOptional(ASTType type) {
        Preconditions.checkArgument(isOptional(type));
        return (ASTTypeArgument)((ASTTypeArguments)((ASTSimpleReferenceType)type).getTypeArguments().get()).getTypeArguments().get(0);
    }

    public static ASTSimpleReferenceType getSimpleReferenceTypeFromOptional(ASTType type) {
        Preconditions.checkArgument(isOptional(type));
        ASTTypeArgument refType = getReferenceTypeFromOptional(type);
        if(refType instanceof ASTWildcardType && ((ASTWildcardType)refType).getUpperBound().isPresent()) {
            refType = (ASTTypeArgument)((ASTWildcardType)refType).getUpperBound().get();
        }

        Preconditions.checkState(refType instanceof ASTSimpleReferenceType);
        return (ASTSimpleReferenceType)refType;
    }

    public static String getReferenceNameFromOptional(ASTType type) {
        Preconditions.checkArgument(isOptional(type));
        ASTTypeArgument reference = (ASTTypeArgument)((ASTTypeArguments)((ASTSimpleReferenceType)type).getTypeArguments().get()).getTypeArguments().get(0);
        if(reference instanceof ASTWildcardType && ((ASTWildcardType)reference).getUpperBound().isPresent()) {
            reference = (ASTTypeArgument)((ASTWildcardType)reference).getUpperBound().get();
        }

        Preconditions.checkArgument(reference instanceof ASTSimpleReferenceType);
        List names = ((ASTSimpleReferenceType)reference).getNames();
        return names.isEmpty()?"":(String)names.get(names.size() - 1);
    }

    public static String getQualifiedReferenceNameFromOptional(ASTType type) {
        Preconditions.checkArgument(isOptional(type));
        ASTTypeArgument reference = (ASTTypeArgument)((ASTTypeArguments)((ASTSimpleReferenceType)type).getTypeArguments().get()).getTypeArguments().get(0);
        if(reference instanceof ASTWildcardType && ((ASTWildcardType)reference).getUpperBound().isPresent()) {
            reference = (ASTTypeArgument)((ASTWildcardType)reference).getUpperBound().get();
        }

        Preconditions.checkArgument(reference instanceof ASTSimpleReferenceType);
        List names = ((ASTSimpleReferenceType)reference).getNames();
        return names.isEmpty()?"": Names.getQualifiedName(names);
    }

    public static boolean isGenericTypeWithOneTypeArgument(ASTType type, String simpleRefTypeName) {
        if(!(type instanceof ASTSimpleReferenceType)) {
            return false;
        } else {
            ASTSimpleReferenceType simpleRefType = (ASTSimpleReferenceType)type;
            return Names.getQualifiedName(simpleRefType.getNames()).equals(simpleRefTypeName) && simpleRefType.getTypeArguments().isPresent() && ((ASTTypeArguments)simpleRefType.getTypeArguments().get()).getTypeArguments().size() == 1;
        }
    }

    public static int getArrayDimensionIfArrayOrZero(ASTType astType) {
        return astType instanceof ASTArrayType?((ASTArrayType)astType).getDimensions():0;
    }

    public static Optional<ASTSimpleReferenceType> getFirstTypeArgumentOfGenericType(ASTType type, String simpleRefTypeName) {
        if(!isGenericTypeWithOneTypeArgument(type, simpleRefTypeName)) {
            return Optional.empty();
        } else {
            ASTSimpleReferenceType simpleRefType = (ASTSimpleReferenceType)type;
            ASTTypeArgument typeArgument = (ASTTypeArgument)((ASTTypeArguments)simpleRefType.getTypeArguments().get()).getTypeArguments().get(0);
            return !(typeArgument instanceof ASTSimpleReferenceType)?Optional.empty():Optional.of((ASTSimpleReferenceType)typeArgument);
        }
    }

    public static Optional<ASTSimpleReferenceType> getFirstTypeArgumentOfOptional(ASTType type) {
        return getFirstTypeArgumentOfGenericType(type, "Optional");
    }

    public static String getSimpleName(ASTSimpleReferenceType simpleType) {
        String name = "";
        List qualifiedName = simpleType.getNames();
        if(qualifiedName != null && !qualifiedName.isEmpty()) {
            name = (String)qualifiedName.get(qualifiedName.size() - 1);
        }

        return name;
    }

    public static List<String> createListFromDotSeparatedString(String s) {
        return Arrays.asList(s.split("\\."));
    }

    public static String printType(ASTType type) {
        if(isOptional(type)) {
            ASTTypeArgument ref = getReferenceTypeFromOptional(type);
            return printType(ref);
        } else {
            return TypesPrinter.printType(type);
        }
    }

    public static boolean isNullable(ASTType type) {
        return !isPrimitive(type);
    }

    public static String printType(ASTTypeArgument type) {
        return type instanceof ASTWildcardType?TypesPrinter.printWildcardType((ASTWildcardType)type):printType((ASTType)type);
    }

    public static String printSimpleRefType(ASTType type) {
        return isOptional(type)?printType((ASTType)getSimpleReferenceTypeFromOptional(type)):TypesPrinter.printType(type);
    }

    public static int getPrimitiveType(String typeName) {
        if(Strings.isNullOrEmpty(typeName)) {
            return -1;
        } else {
            byte var2 = -1;
            switch(typeName.hashCode()) {
                case -1325958191:
                    if(typeName.equals("double")) {
                        var2 = 4;
                    }
                    break;
                case 104431:
                    if(typeName.equals("int")) {
                        var2 = 5;
                    }
                    break;
                case 3039496:
                    if(typeName.equals("byte")) {
                        var2 = 2;
                    }
                    break;
                case 3052374:
                    if(typeName.equals("char")) {
                        var2 = 3;
                    }
                    break;
                case 3327612:
                    if(typeName.equals("long")) {
                        var2 = 7;
                    }
                    break;
                case 64711720:
                    if(typeName.equals("boolean")) {
                        var2 = 0;
                    }
                    break;
                case 97526364:
                    if(typeName.equals("float")) {
                        var2 = 1;
                    }
                    break;
                case 109413500:
                    if(typeName.equals("short")) {
                        var2 = 6;
                    }
            }

            switch(var2) {
                case 0:
                    return 1;
                case 1:
                    return 7;
                case 2:
                    return 2;
                case 3:
                    return 6;
                case 4:
                    return 8;
                case 5:
                    return 4;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return -1;
            }
        }
    }
}
