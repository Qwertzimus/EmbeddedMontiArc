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
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types;

import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.types2._ast.*;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by dennisqiao on 3/8/17.
 */
public class TypesPrinter {
    private static TypesPrinter instance;

    private TypesPrinter() {
    }

    private static TypesPrinter getInstance() {
        if (instance == null) {
            instance = new TypesPrinter();
        }

        return instance;
    }

    public static String printType(ASTType type) {
        return getInstance().doPrintType(type);
    }

    protected String doPrintType(ASTType type) {
        return type instanceof ASTArrayType ? this.doPrintArrayType((ASTArrayType) type) : (type instanceof ASTPrimitiveType ? this.doPrintPrimitiveType((ASTPrimitiveType) type) : (type instanceof ASTReferenceType ? this.doPrintReferenceType((ASTReferenceType) type) : ""));
    }

    public static String printReferenceType(ASTReferenceType type) {
        return getInstance().doPrintReferenceType(type);
    }

    protected String doPrintReferenceType(ASTReferenceType type) {
        return type instanceof ASTSimpleReferenceType ? this.doPrintSimpleReferenceType((ASTSimpleReferenceType) type) : (type instanceof ASTComplexReferenceType ? this.doPrintComplexReferenceType((ASTComplexReferenceType) type) : "");
    }

    public static String printReturnType(ASTReturnType type) {
        return getInstance().doPrintReturnType(type);
    }

    protected String doPrintReturnType(ASTReturnType type) {
        return type instanceof ASTType ? this.doPrintType((ASTType) type) : (type instanceof ASTVoidType ? this.doPrintVoidType((ASTVoidType) type) : "");
    }

    public static String printTypeArgument(ASTTypeArgument type) {
        return getInstance().doPrintTypeArgument(type);
    }

    protected String doPrintTypeArgument(ASTTypeArgument type) {
        return type instanceof ASTWildcardType ? this.doPrintWildcardType((ASTWildcardType) type) : (type instanceof ASTType ? this.doPrintType((ASTType) type) : "");
    }

    public static String printTypeWithoutTypeArguments(ASTType type) {
        return getInstance().doPrintTypeWithoutTypeArguments(type);
    }

    protected String doPrintTypeWithoutTypeArguments(ASTType type) {
        return type instanceof ASTArrayType ? this.doPrintArrayType((ASTArrayType) type) : (type instanceof ASTPrimitiveType ? this.doPrintPrimitiveType((ASTPrimitiveType) type) : (type instanceof ASTReferenceType ? this.doPrintReferenceTypeWithoutTypeArguments((ASTReferenceType) type) : ""));
    }

    public static String printTypeWithoutTypeArgumentsAndDimension(ASTType type) {
        return getInstance().doPrintTypeWithoutTypeArgumentsAndDimension(type);
    }

    protected String doPrintTypeWithoutTypeArgumentsAndDimension(ASTType type) {
        return type instanceof ASTArrayType ? this.doPrintTypeWithoutTypeArgumentsAndDimension(((ASTArrayType) type).getComponentType()) : (type instanceof ASTPrimitiveType ? this.doPrintPrimitiveType((ASTPrimitiveType) type) : (type instanceof ASTReferenceType ? this.doPrintTypeWithoutTypeArguments((ASTReferenceType) type) : resolveNewType(type)));
    }

    protected String resolveNewType(ASTType type) {
        if (type instanceof ASTPrintType) {
            return ((ASTPrintType) type).printType();
        }else if(type instanceof ASTElementType){
            return "ElementType";
        }

        Log.info(type.toString(),"Type:");
        Log.error("Type can not be handled!");
        return "";
    }

    public static String printTypeParameters(ASTTypeParameters params) {
        return getInstance().doPrintTypeParameters(params);
    }

    protected String doPrintTypeParameters(ASTTypeParameters params) {
        return params != null && params.getTypeVariableDeclarations() != null && !params.getTypeVariableDeclarations().isEmpty() ? "<" + this.doPrintTypeVariableDeclarationList(params.getTypeVariableDeclarations()) + ">" : "";
    }

    public static String printTypeVariableDeclarationList(List<ASTTypeVariableDeclaration> decl) {
        return getInstance().doPrintTypeVariableDeclarationList(decl);
    }

    protected String doPrintTypeVariableDeclarationList(List<ASTTypeVariableDeclaration> decl) {
        StringBuilder ret = new StringBuilder();
        if (decl != null) {
            String sep = "";

            for (Iterator var4 = decl.iterator(); var4.hasNext(); sep = ", ") {
                ASTTypeVariableDeclaration d = (ASTTypeVariableDeclaration) var4.next();
                ret.append(sep + this.doPrintTypeVariableDeclaration(d));
            }
        }

        return ret.toString();
    }

    public static String printTypeVariableDeclaration(ASTTypeVariableDeclaration decl) {
        return getInstance().doPrintTypeVariableDeclaration(decl);
    }

    protected String doPrintTypeVariableDeclaration(ASTTypeVariableDeclaration decl) {
        StringBuilder ret = new StringBuilder();
        if (decl != null) {
            ret.append(decl.getNamingResolution().get().getName());
            if (decl.getUpperBounds() != null && !decl.getUpperBounds().isEmpty()) {
                String sep = " extends ";

                for (Iterator var4 = decl.getUpperBounds().iterator(); var4.hasNext(); sep = " & ") {
                    ASTType type = (ASTType) var4.next();
                    ret.append(sep + this.doPrintType(type));
                }
            }
        }

        return ret.toString();
    }

    public static String printVoidType(ASTVoidType type) {
        return getInstance().doPrintVoidType(type);
    }

    protected String doPrintVoidType(ASTVoidType type) {
        return type != null ? "void" : "";
    }

    public static String printPrimitiveType(ASTPrimitiveType type) {
        return getInstance().doPrintPrimitiveType(type);
    }

    protected String doPrintPrimitiveType(ASTPrimitiveType type) {
        return type == null ? "" : (type.getPrimitive() == 1 ? "boolean" : (type.getPrimitive() == 2 ? "byte" : (type.getPrimitive() == 6 ? "char" : (type.getPrimitive() == 3 ? "short" : (type.getPrimitive() == 4 ? "int" : (type.getPrimitive() == 7 ? "float" : (type.getPrimitive() == 5 ? "long" : (type.getPrimitive() == 8 ? "double" : ""))))))));
    }

    public static String printArrayType(ASTArrayType type) {
        return getInstance().doPrintArrayType(type);
    }

    protected String doPrintArrayType(ASTArrayType type) {
        if (type == null) {
            return "";
        } else {
            StringBuilder dimension = new StringBuilder();
            dimension.append(this.doPrintType(type.getComponentType()));

            for (int i = 0; i < type.getDimensions(); ++i) {
                dimension.append("[]");
            }

            return dimension.toString();
        }
    }

    public static String printReferenceTypeList(List<ASTReferenceType> type) {
        return getInstance().doPrintReferenceTypeList(type);
    }

    protected String doPrintReferenceTypeList(List<ASTReferenceType> type) {
        StringBuilder ret = new StringBuilder();
        if (type != null) {
            String sep = "";

            for (Iterator var4 = type.iterator(); var4.hasNext(); sep = ", ") {
                ASTReferenceType refType = (ASTReferenceType) var4.next();
                ret.append(sep + this.doPrintReferenceType(refType));
            }
        }

        return ret.toString();
    }

    public static String printSimpleReferenceType(ASTSimpleReferenceType type) {
        return getInstance().doPrintSimpleReferenceType(type);
    }

    protected String doPrintSimpleReferenceType(ASTSimpleReferenceType type) {
        return type != null ? (type.getTypeArguments().isPresent() ? Names.getQualifiedName(type.getNames()) + this.doPrintTypeArguments((ASTTypeArguments) type.getTypeArguments().get()) : Names.getQualifiedName(type.getNames())) : "";
    }

    public static String printComplexReferenceType(ASTComplexReferenceType type) {
        return getInstance().doPrintComplexReferenceType(type);
    }

    protected String doPrintComplexReferenceType(ASTComplexReferenceType type) {
        String ret = "";
        return type != null && type.getSimpleReferenceTypes() != null ? this.doPrintSimpleReferenceTypeList(type.getSimpleReferenceTypes()) : ret;
    }

    public static String printSimpleReferenceTypeList(List<ASTSimpleReferenceType> type) {
        return getInstance().doPrintSimpleReferenceTypeList(type);
    }

    protected String doPrintSimpleReferenceTypeList(List<ASTSimpleReferenceType> argList) {
        StringBuilder ret = new StringBuilder();
        if (argList != null) {
            String sep = "";

            for (Iterator var4 = argList.iterator(); var4.hasNext(); sep = ". ") {
                ASTSimpleReferenceType arg = (ASTSimpleReferenceType) var4.next();
                ret.append(sep + this.doPrintSimpleReferenceType(arg));
            }
        }

        return ret.toString();
    }

    public static String printTypeArguments(ASTTypeArguments args) {
        return getInstance().doPrintTypeArguments(args);
    }

    protected String doPrintTypeArguments(ASTTypeArguments args) {
        return args != null && args.getTypeArguments() != null && !args.getTypeArguments().isEmpty() ? "<" + this.doPrintTypeArgumentList(args.getTypeArguments()) + ">" : "";
    }

    public static String printTypeArgumentList(List<ASTTypeArgument> argList) {
        return getInstance().doPrintTypeArgumentList(argList);
    }

    protected String doPrintTypeArgumentList(List<ASTTypeArgument> argList) {
        StringBuilder ret = new StringBuilder();
        if (argList != null) {
            String sep = "";

            for (Iterator var4 = argList.iterator(); var4.hasNext(); sep = ", ") {
                ASTTypeArgument arg = (ASTTypeArgument) var4.next();
                ret.append(sep + this.doPrintTypeArgument(arg));
            }
        }

        return ret.toString();
    }

    public static String printWildcardType(ASTWildcardType type) {
        return getInstance().doPrintWildcardType(type);
    }

    protected String doPrintWildcardType(ASTWildcardType type) {
        StringBuilder ret = new StringBuilder();
        if (type != null) {
            ret.append("?");
            if (type.getUpperBound().isPresent()) {
                ret.append(" extends " + this.doPrintType((ASTType) type.getUpperBound().get()));
            } else if (type.getLowerBound().isPresent()) {
                ret.append(" super " + this.doPrintType((ASTType) type.getLowerBound().get()));
            }
        }

        return ret.toString();
    }

    protected String doPrintReferenceTypeWithoutTypeArguments(ASTReferenceType type) {
        return type instanceof ASTSimpleReferenceType ? this.doPrintSimpleReferenceTypeWithoutTypeArguments((ASTSimpleReferenceType) type) : (type instanceof ASTComplexReferenceType ? this.doPrintComplexReferenceTypeWithoutTypeArguments((ASTComplexReferenceType) type) : "");
    }

    protected String doPrintSimpleReferenceTypeWithoutTypeArguments(ASTSimpleReferenceType type) {
        return type != null ? Names.getQualifiedName(type.getNames()) : "";
    }

    protected String doPrintComplexReferenceTypeWithoutTypeArguments(ASTComplexReferenceType type) {
        return type != null && type.getSimpleReferenceTypes() != null ? this.doPrintSimpleReferenceTypeListWithoutTypeArguments(type.getSimpleReferenceTypes()) : "";
    }

    protected String doPrintSimpleReferenceTypeListWithoutTypeArguments(List<ASTSimpleReferenceType> argList) {
        StringBuilder ret = new StringBuilder();
        if (argList != null) {
            String sep = "";

            for (Iterator var4 = argList.iterator(); var4.hasNext(); sep = ". ") {
                ASTSimpleReferenceType arg = (ASTSimpleReferenceType) var4.next();
                ret.append(sep + this.doPrintSimpleReferenceTypeWithoutTypeArguments(arg));
            }
        }

        return ret.toString();
    }

    /**
     * Removes Optional from target name
     * so something like name[Optional[1/1]]
     * becomes name[1]
     *
     * @param targetName
     * @return
     */
    public static String fixTargetName(String targetName) {
        if (targetName.contains("[Optional[")) {
            int firstClosedBracket = targetName.indexOf("]");
            int secondClosedBracket = targetName.indexOf("]", firstClosedBracket + 1);
            if (secondClosedBracket != -1) {
                Log.info(targetName, "targetName:");
                int index = targetName.indexOf("[Optional[");
                index += "Optional[".length();
                int endIndex = targetName.indexOf("/", index);
                String bracketNumber = targetName.substring(index, endIndex);
                int endOfInnerBracket = targetName.indexOf("]", endIndex + 1);
                int endOfSecondBracket = targetName.indexOf("]", endOfInnerBracket + 1);
                return targetName.substring(0, index) + bracketNumber + "]" + targetName.substring(endOfSecondBracket + 1, targetName.length());
            } else {
                return targetName.replaceAll("Optional\\[", "");
            }
        }
        return targetName;
    }

    public static String FirstLowerCase(String name) {
        String newName;
        if(name.length() > 1) {
            newName = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        } else {
            newName = Character.toLowerCase(name.charAt(0)) + "";
        }

        return newName;
    }
}
