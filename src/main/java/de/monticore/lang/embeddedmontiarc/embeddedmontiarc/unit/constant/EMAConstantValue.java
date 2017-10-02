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

package de.monticore.lang.embeddedmontiarc.embeddedmontiarc.unit.constant;

/**
 * The base type of every constant that can be stored by a ConstantPortSymbol
 * which is used by a ConstantConnector
 */
public abstract class EMAConstantValue<T> {
    protected T value;

    public EMAConstantValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isSIUnit() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    @Deprecated
    public boolean isInteger() {
        return false;
    }

    @Deprecated
    public boolean isShort() {
        return false;
    }

    @Deprecated
    public boolean isLong() {
        return false;
    }

    @Deprecated
    public boolean isFloat() {
        return false;
    }

    @Deprecated
    public boolean isDouble() {
        return false;
    }

    @Deprecated
    public boolean isString() {
        return false;
    }

    @Deprecated
    public boolean isChar() {
        return false;
    }

    @Deprecated
    public boolean isByte() {
        return false;
    }

    public String getValueAsString(){
        return "null";
    }
}
