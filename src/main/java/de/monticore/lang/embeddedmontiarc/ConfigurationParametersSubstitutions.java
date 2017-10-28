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
package de.monticore.lang.embeddedmontiarc;

import de.monticore.lang.monticar.struct.model.type.StructFieldTypeInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigurationParametersSubstitutions {

    @Nonnull
    private final Map<String, StructFieldTypeInfo> parentConfigurationParameters;

    @Nonnull
    private final List<PassedParameter> parametersPassedToChildren;

    @Nonnull
    private final Map<String, List<LiteralParameterSubstitution<Object>>> literalChildParameters;

    @Nonnull
    public final Map<String, StructFieldTypeInfo> getParentConfigurationParameters() {
        return this.parentConfigurationParameters;
    }

    @Nonnull
    public final List<PassedParameter> getParametersPassedToChildren() {
        return this.parametersPassedToChildren;
    }

    @Nonnull
    public final Map<String, List<LiteralParameterSubstitution<Object>>> getLiteralChildParameters() {
        return this.literalChildParameters;
    }

    public ConfigurationParametersSubstitutions() {
        this(new HashMap<>(), new ArrayList<>(), new HashMap<>());
    }

    public ConfigurationParametersSubstitutions(@Nonnull Map<String, StructFieldTypeInfo> parentConfigurationParameters, @Nonnull List<PassedParameter> parametersPassedToChildren, @Nonnull Map<String, List<LiteralParameterSubstitution<Object>>> literalChildParameters) {
        this.parentConfigurationParameters = parentConfigurationParameters;
        this.parametersPassedToChildren = parametersPassedToChildren;
        this.literalChildParameters = literalChildParameters;
    }
}
