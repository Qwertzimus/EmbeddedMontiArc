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

import javax.annotation.Nonnull;

public final class PassedParameter {

    @Nonnull
    private final String sourceParameterName;

    @Nonnull
    private final String targetChildName;

    @Nonnull
    private final String targetParameterName;

    @Nonnull
    public final String getSourceParameterName() {
        return this.sourceParameterName;
    }

    @Nonnull
    public final String getTargetChildName() {
        return this.targetChildName;
    }

    @Nonnull
    public final String getTargetParameterName() {
        return this.targetParameterName;
    }

    public PassedParameter(@Nonnull String sourceParameterName, @Nonnull String targetChildName, @Nonnull String targetParameterName) {
        this.sourceParameterName = sourceParameterName;
        this.targetChildName = targetChildName;
        this.targetParameterName = targetParameterName;
    }
}
