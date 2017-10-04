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
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.helper.ArcTypePrinter;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTInterface;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTPort;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTInterfaceCoCo;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that port names are unique (including implicit port names derived from ports without a
 * name).
 *
 * @author Arne Haber, Robert Heim
 */
public class UniquePorts implements EmbeddedMontiArcASTInterfaceCoCo {

  /**
   * @see EmbeddedMontiArcASTInterfaceCoCo#check(ASTInterface)
   */
  @Override
  public void check(ASTInterface node) {
    List<String> usedNames = new ArrayList<>();
    for (ASTPort port : node.getPorts()) {
      String name = "";
      if (port.getName().isPresent()) {
        name = port.getName().get();
      }
      else {
        // calc implicit name
        String implicitName = ArcTypePrinter.printType(port.getType());
        // TODO use symTab
        // PortSymbol entry = ((PortSymbol) port.getSymbol().get());
        // String implicitName = entry.getTypeReference().getReferencedSymbol().getName();
        name = StringTransformations.uncapitalize(implicitName);
      }
      if (usedNames.contains(name)) {
        Log.error(String.format("0xAC002 The name of port '%s' is ambiguos!", name),
            port.get_SourcePositionStart());
      }
      usedNames.add(name);

    }
  }

}
