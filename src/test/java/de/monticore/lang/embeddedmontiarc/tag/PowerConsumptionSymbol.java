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
package de.monticore.lang.embeddedmontiarc.tag;//package de.monticore.lang.montiarc.tag;
//
//import de.monticore.lang.montiarc.helper.IndentPrinter;
//import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
//import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;
//import de.monticore.lang.montiarc.unit.Power;
//
///**
// * Created by Michael von Wenckstern on 31.05.2016.
// */
//public class PowerConsumptionSymbol extends TagSymbol {
//  public static final PowerConsumptionKind KIND = PowerConsumptionKind.INSTANCE;
//
//  /**
//   * is marker symbol so it has no value by itself
//   */
//  public PowerConsumptionSymbol(double value, Power unit) {
//    // true to set that it is marked
//    super(KIND, value, unit);
//  }
//
//  public double getNumber() {
//    return getValue(0);
//  }
//
//  public Power getUnit() {
//    return getValue(1);
//  }
//
//  @Override
//  public String toString() {
//    return IndentPrinter.groups("PowerConsumption = {0} {1}")
//        .params(getNumber(), getUnit())
//        .toString();
//  }
//
//  public static class PowerConsumptionKind extends TagKind {
//    public static final PowerConsumptionKind INSTANCE = new PowerConsumptionKind();
//
//    protected PowerConsumptionKind() {
//    }
//  }
//}
