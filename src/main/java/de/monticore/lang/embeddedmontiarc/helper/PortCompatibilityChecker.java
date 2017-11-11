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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.references.MCTypeReference;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Checks type compatibility of connected ports.
 *
 * @author ahaber, Robert Heim
 */
public class PortCompatibilityChecker {
  /**
   * Checks whether the sourcePort's type can be connected to the targetPort's type. For example,
   * consider a generic subcomponent {@code s<Int>} of type {@code S<T>} with a port {@code p} of
   * type {@code T}. If {@code p} is the sourcePort, the sourceFormalTypeParameters list is
   * {@code [T]}, the sourceTypeArguments is {@code [Int]}.<br>
   * <br>
   * This type-check would allow a typed based auto-connection of {@code aOut} with {@code p} in the
   * following example:
   * <pre>
   * component A {
   *   port out Int aOut;
   *   component S&lt;T&gt; s&lt;Int&gt; {
   *     port out T p;
   *   }
   * }
   * </pre>
   *
   * @param sourcePort                     the port that outputs data
   * @param sourceFormalTypeParameters     the defined formal type parameters of the component that the
   *                                       sourcePort is defined in. They define additional valid types that might be bound by the
   *                                       sourceTypeArguments. This list might be empty.
   * @param sourceTypeArguments            Defines the current bindings for the formal type-parameters. This
   *                                       list might be empty.
   * @param targetPort                     the port that receives data
   * @param targetTypeFormalTypeParameters analog to source, but for the target port.
   * @param targetTypeArguments            analog to source, but for the target port.
   * @return
   */
  public static boolean doPortTypesMatch(PortSymbol sourcePort,
      List<MCTypeSymbol> sourceFormalTypeParameters,
      List<MCTypeReference<? extends MCTypeSymbol>> sourceTypeArguments,
      PortSymbol targetPort,
      List<MCTypeSymbol> targetTypeFormalTypeParameters,
      List<MCTypeReference<? extends MCTypeSymbol>> targetTypeArguments) {
    checkNotNull(sourcePort);
    checkNotNull(targetPort);
    return TypeCompatibilityChecker.doTypesMatch(sourcePort.getTypeReference(),
        sourceFormalTypeParameters,
        sourceTypeArguments, targetPort.getTypeReference(), targetTypeFormalTypeParameters,
        targetTypeArguments);
  }

}
