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
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc.unit.constant;

import org.jscience.mathematics.number.Rational;

import javax.measure.unit.Unit;

/**
 * Represents a constant SIUnit which is used for constant connectors
 */
public class EMAConstantSIUnit extends EMAConstantValue {
    public Unit unit;

    public EMAConstantSIUnit(Rational number, Unit unit) {
        super(number);
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public boolean isSIUnit() {
        return true;
    }

    public Rational getRational() {
        return (Rational) value;
    }

    /**
     * Does not return the unit, just the value
     *
     * @return
     */
    @Override
    public String getValueAsString() {
        String result = "";
        if (getRational().getDivisor().intValue() == 1) {
            result += getRational().intValue();
        } else {
            result += getRational().doubleValue();
        }
        return result;
    }
}
