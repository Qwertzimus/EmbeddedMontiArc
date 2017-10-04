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
/*package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTMontiArcInvariant;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;*/

/**
 * Checks that names of invariants within a component are unique.
 *
 * @author Arne Haber, Robert Heim
 */
/*public class UniqueConstraint implements EmbeddedMontiArcASTComponentCoCo {

  /**
   * @see EmbeddedMontiArcASTComponentCoCo#check(ASTComponent)
   */
 /* @Override
  public void check(ASTComponent node) {
    final List<String> usedNames = new ArrayList<String>();

    for (ASTMontiArcInvariant inv : node.getBody().getElements().stream()
        .filter(a -> a instanceof ASTMontiArcInvariant).map(a -> (ASTMontiArcInvariant) a)
        .collect(Collectors.toList())) {

      if (usedNames.contains(inv.getName())) {
        Log.error(String.format("0xAC001 The name of constraint '%s' is ambiguos!", inv.getName()),
            inv.get_SourcePositionStart());
      }

      usedNames.add(inv.getName());
    }
  }

}*/
