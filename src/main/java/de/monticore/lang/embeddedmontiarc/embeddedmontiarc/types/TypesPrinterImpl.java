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
 * @author Sascha Schneiders
 */
public class TypesPrinterImpl {


    protected String doPrintType(ASTType type) {
        return type instanceof ASTArrayType ? this.doPrintArrayType((ASTArrayType) type) : (type instanceof ASTPrimitiveType ? this.doPrintPrimitiveType((ASTPrimitiveType) type) : (type instanceof ASTReferenceType ? this.doPrintReferenceType((ASTReferenceType) type) : ""));
    }

    protected String doPrintReferenceType(ASTReferenceType type) {
        return type instanceof ASTSimpleReferenceType ? this.doPrintSimpleReferenceType((ASTSimpleReferenceType) type) : (type instanceof ASTComplexReferenceType ? this.doPrintComplexReferenceType((ASTComplexReferenceType) type) : "");
    }

    protected String doPrintReturnType(ASTReturnType type) {
        return type instanceof ASTType ? this.doPrintType((ASTType) type) : (type instanceof ASTVoidType ? this.doPrintVoidType((ASTVoidType) type) : "");
    }

    protected String doPrintTypeArgument(ASTTypeArgument type) {
        return type instanceof ASTWildcardType ? this.doPrintWildcardType((ASTWildcardType) type) : (type instanceof ASTType ? this.doPrintType((ASTType) type) : "");
    }

    protected String doPrintTypeWithoutTypeArguments(ASTType type) {
        return type instanceof ASTArrayType ? this.doPrintArrayType((ASTArrayType) type) : (type instanceof ASTPrimitiveType ? this.doPrintPrimitiveType((ASTPrimitiveType) type) : (type instanceof ASTReferenceType ? this.doPrintReferenceTypeWithoutTypeArguments((ASTReferenceType) type) : ""));
    }

    protected String doPrintTypeWithoutTypeArgumentsAndDimension(ASTType type) {
        return type instanceof ASTArrayType ? this.doPrintTypeWithoutTypeArgumentsAndDimension(((ASTArrayType) type).getComponentType()) : (type instanceof ASTPrimitiveType ? this.doPrintPrimitiveType((ASTPrimitiveType) type) : (type instanceof ASTReferenceType ? this.doPrintTypeWithoutTypeArguments((ASTReferenceType) type) : resolveNewType(type)));
    }

    protected String resolveNewType(ASTType type) {
        if (type instanceof ASTPrintType) {
            return ((ASTPrintType) type).printType();
        } else if (type instanceof ASTElementType) {
            ASTElementType t = (ASTElementType) type;
            if (t.isIsBoolean()) {
                return "B";
            }
            if (t.isIsRational()) {
                return "Q";
            }
            if (t.isIsComplex()) {
                return "C";
            }
            if (t.isIsWholeNumberNumber()) {
                return "Z";
            }
            throw new UnsupportedOperationException("unknown ElementType: " + t);
        }

        Log.info(type.toString(), "Type:");
        Log.error("Type can not be handled!");
        return "";
    }


    protected String doPrintTypeParameters(ASTTypeParameters params) {
        return params != null && params.getTypeVariableDeclarations() != null && !params.getTypeVariableDeclarations().isEmpty() ? "<" + this.doPrintTypeVariableDeclarationList(params.getTypeVariableDeclarations()) + ">" : "";
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

    protected String doPrintVoidType(ASTVoidType type) {
        return type != null ? "void" : "";
    }

    protected String doPrintPrimitiveType(ASTPrimitiveType type) {
        return type == null ? "" : (type.getPrimitive() == 1 ? "boolean" : (type.getPrimitive() == 2 ? "byte" : (type.getPrimitive() == 6 ? "char" : (type.getPrimitive() == 3 ? "short" : (type.getPrimitive() == 4 ? "int" : (type.getPrimitive() == 7 ? "float" : (type.getPrimitive() == 5 ? "long" : (type.getPrimitive() == 8 ? "double" : ""))))))));
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

    protected String doPrintSimpleReferenceType(ASTSimpleReferenceType type) {
        return type != null ? (type.getTypeArguments().isPresent() ? Names.getQualifiedName(type.getNames()) + this.doPrintTypeArguments((ASTTypeArguments) type.getTypeArguments().get()) : Names.getQualifiedName(type.getNames())) : "";
    }

    protected String doPrintComplexReferenceType(ASTComplexReferenceType type) {
        String ret = "";
        return type != null && type.getSimpleReferenceTypes() != null ? this.doPrintSimpleReferenceTypeList(type.getSimpleReferenceTypes()) : ret;
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

    protected String doPrintTypeArguments(ASTTypeArguments args) {
        return args != null && args.getTypeArguments() != null && !args.getTypeArguments().isEmpty() ? "<" + this.doPrintTypeArgumentList(args.getTypeArguments()) + ">" : "";
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

}
