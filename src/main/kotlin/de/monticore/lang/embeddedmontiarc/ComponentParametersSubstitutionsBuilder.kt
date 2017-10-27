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
package de.monticore.lang.embeddedmontiarc

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentInstanceSymbol
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._visitor.EmbeddedMontiArcParentAwareVisitor
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._visitor.EmbeddedMontiArcVisitor
import de.monticore.lang.monticar.literals2._ast.ASTBooleanLiteral
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression
import de.monticore.lang.monticar.mcexpressions._ast.ASTLiteralExpression
import de.monticore.lang.monticar.mcexpressions._ast.ASTNameExpression
import de.monticore.lang.monticar.ranges._ast.ASTUnitNumberExpression
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument
import de.monticore.lang.monticar.struct._symboltable.StructSymbol
import de.monticore.lang.monticar.struct._symboltable.StructSymbolReference
import de.monticore.lang.monticar.struct.model.type.ScalarStructFieldType
import de.monticore.lang.monticar.struct.model.type.StructFieldTypeInfo
import de.monticore.lang.monticar.struct.model.type.StructReferenceFieldType
import de.monticore.lang.monticar.struct.model.type.VectorStructFieldType
import de.monticore.lang.monticar.types2._ast.ASTType
import de.monticore.lang.monticar.types2._ast.ASTTypeArguments
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution
import de.monticore.symboltable.types.references.JTypeReference
import de.se_rwth.commons.logging.Log

class ComponentParametersSubstitutionsBuilder(
        private val component: ComponentSymbol
) : EmbeddedMontiArcParentAwareVisitor() {

    private var theRealThis: EmbeddedMontiArcVisitor = this
    private val formalTypeParametersSubstitutions = FormalTypeParametersSubstitutions()
    private val configurationParametersSubstitutions = ConfigurationParametersSubstitutions()
    private var currentChild: ComponentInstanceSymbol? = null
    private val childFormalTypeParameters: MutableList<String> = mutableListOf()
    private val childConfigurationParameters: MutableList<String> = mutableListOf()

    override fun getRealThis(): EmbeddedMontiArcVisitor {
        return theRealThis
    }

    override fun setRealThis(realThis: EmbeddedMontiArcVisitor) {
        theRealThis = realThis
    }

    fun build(): ComponentParametersSubstitutions {
        if (component.hasFormalTypeParameters()) {
            formalTypeParametersSubstitutions.parentFormalTypeParameterNames.addAll(
                    component.formalTypeParameters.map { it.name }
            )
        }
        if (component.hasConfigParameters()) {
            component.configParameters.forEach {
                val type = tryConvertToStructFieldTypeInfo(it.type)
                if (type != null) {
                    configurationParametersSubstitutions
                            .parentConfigurationParameters
                            .put(it.name, type)
                } else {
                    Log.error(
                            "cannot handle configuration" +
                                    " parameter $it" +
                                    " in component ${component.fullName}",
                            it.sourcePosition
                    )
                }
            }

        }
        val astNode = component.astNode.get() as ASTComponent
        astNode.accept(theRealThis)
        return ComponentParametersSubstitutions(
                formalTypeParametersSubstitutions,
                configurationParametersSubstitutions
        )
    }

    override fun visit(node: ASTSubComponent) {
        val subComp = node.symbol.get() as ComponentInstanceSymbol
        currentChild = subComp
        childFormalTypeParameters.clear()
        childFormalTypeParameters.addAll(
                subComp.componentType.referencedSymbol.formalTypeParameters.map { it.name }
        )
        childConfigurationParameters.clear()
        childConfigurationParameters.addAll(
                subComp.componentType.referencedSymbol.configParameters.map { it.name }
        )
        val astArguments = node.arguments
        if (astArguments.size != childConfigurationParameters.size) {
            Log.error(
                    "number of type arguments do not match number of type parameters",
                    node._SourcePositionStart
            )
            return
        }
        astArguments.forEachIndexed { index, astArg ->
            val name = childConfigurationParameters[index]
            if (!processArgument(name, astArg)) {
                Log.error(
                        "cannot handle" +
                                " configuration argument $astArg" +
                                " in instance $currentChild" +
                                " of component ${component.fullName}",
                        astArg._SourcePositionStart
                )
            }
        }
    }

    override fun endVisit(node: ASTSubComponent) {
        currentChild = null
        childFormalTypeParameters.clear()
        childConfigurationParameters.clear()
    }

    override fun visit(node: ASTTypeArguments) {
        if (currentChild == null) {
            return
        }
        val typeArgs = node.typeArguments.asSortedByStartSourcePosition()
        typeArgs.forEachIndexed { index, astNode ->
            val formalParameterName = childFormalTypeParameters[index]
            if (!processTypeArgument(formalParameterName, astNode)) {
                Log.error(
                        "cannot handle" +
                                " type argument $astNode" +
                                " in instance $currentChild" +
                                " of component ${component.fullName}",
                        astNode._SourcePositionStart
                )
            }
        }
    }

    private fun processTypeArgument(
            formalParameterName: String,
            typeArgument: ASTTypeArgument
    ): Boolean {
        when (typeArgument) {
            is ASTUnitNumberResolution -> {
                val actualParameterName = typeArgument.name.get()
                if (isParentHasTypeParameter(actualParameterName)) {
                    formalTypeParametersSubstitutions.parametersPassedToChildren.add(
                            PassedParameter(
                                    actualParameterName,
                                    currentChild!!.name,
                                    formalParameterName
                            )
                    )
                    return true
                } else {
                    // it must be struct
                    val struct = tryResolveStructure(actualParameterName) ?: return false
                    addLiteralTypeParameter(formalParameterName, struct)
                    return true
                }
            }
            is ASTType -> {
                val p = StructFieldTypeInfo.tryRepresentASTType(
                        typeArgument,
                        component.enclosingScope
                ) ?: return false
                addLiteralTypeParameter(formalParameterName, p)
                return true
            }
        }
        return false
    }

    private fun processArgument(
            name: String,
            arg: ASTExpression
    ): Boolean {
        when (arg) {
            is ASTNameExpression -> {
                if (isParentHasConfigurationParameter(arg.name)) {
                    configurationParametersSubstitutions.parametersPassedToChildren.add(
                            PassedParameter(
                                    arg.name,
                                    currentChild!!.name,
                                    name
                            )
                    )
                    return true
                }
            }
            is ASTUnitNumberExpression -> {
                val tUnitNumber = arg.tUnitNumber
                if (!tUnitNumber.isPresent) {
                    return false
                }
                val literalValue = tUnitNumber.get().toDoubleOrNull() ?: return false
                addLiteralConfigurationParameter(name, literalValue)
                return true
            }
            is ASTLiteralExpression -> {
                val literal = arg.literal
                if (literal is ASTBooleanLiteral) {
                    addLiteralConfigurationParameter(name, literal.value)
                    return true
                }
            }
        }
        return false
    }

    private fun addLiteralTypeParameter(
            formalParamName: String,
            type: StructFieldTypeInfo
    ) {
        val p = LiteralParameterSubstitution(formalParamName, type)
        val literalParams = formalTypeParametersSubstitutions.literalChildParameters[currentChild!!.name]
        if (literalParams != null) {
            literalParams.add(p)
        } else {
            formalTypeParametersSubstitutions.literalChildParameters.put(
                    currentChild!!.name,
                    mutableListOf(p)
            )
        }
    }

    private fun addLiteralConfigurationParameter(
            name: String,
            literalValue: Any
    ) {
        val p = LiteralParameterSubstitution(name, literalValue)
        val literalParams = configurationParametersSubstitutions.literalChildParameters[currentChild!!.name]
        if (literalParams != null) {
            literalParams.add(p)
        } else {
            configurationParametersSubstitutions.literalChildParameters.put(
                    currentChild!!.name,
                    mutableListOf(p)
            )
        }
    }

    private fun tryConvertToStructFieldTypeInfo(type: JTypeReference<*>): StructFieldTypeInfo? {
        val typeName = type.name
        val baseType = when {
            typeName == "scalar.B" -> ScalarStructFieldType.BOOL
            typeName == "scalar.C" -> ScalarStructFieldType.COMPLEX
            typeName == "scalar.Q" -> ScalarStructFieldType.RATIONAL
            typeName == "scalar.Z" -> ScalarStructFieldType.INTEGRAL
            isParentHasTypeParameter(typeName) -> FormalTypeParameterFieldType(typeName)
            else -> tryResolveStructure(typeName) ?: return null
        }
        return if (type.dimension > 0) {
            val vsft = VectorStructFieldType()
            vsft.typeOfElements = baseType
            vsft.dimensionality = type.dimension
            vsft
        } else {
            baseType
        }
    }

    private fun tryResolveStructure(name: String): StructReferenceFieldType? {
        val struct = component
                .enclosingScope
                .resolve<StructSymbol>(name, StructSymbol.KIND)
                .orElse(null) ?: return null
        val ref = StructReferenceFieldType()
        ref.reference = StructSymbolReference(
                struct.fullName,
                struct.enclosingScope
        )
        return ref
    }

    private fun isParentHasTypeParameter(name: String): Boolean {
        return formalTypeParametersSubstitutions
                .parentFormalTypeParameterNames
                .contains(name)
    }

    private fun isParentHasConfigurationParameter(name: String): Boolean {
        return configurationParametersSubstitutions.parentConfigurationParameters.containsKey(name)
    }
}
