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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTEMACompilationUnit;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTEMACompilationUnitCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcModelNameCalculator;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures, that packages of components are lower-case. This is required for inner components, see
 * {@link EmbeddedMontiArcModelNameCalculator}.
 *
 * @author Robert Heim
 */
public class PackageLowerCase implements EmbeddedMontiArcASTEMACompilationUnitCoCo {

  /**
   * @see EmbeddedMontiArcASTEMACompilationUnitCoCo#check(ASTEMACompilationUnit)
   */
  @Override
  public void check(ASTEMACompilationUnit node) {
    String pack = Names.getQualifiedName(node.getPackage());
    if (pack.toUpperCase().equals(pack)) {
      Log.error("0xAC003 The package must be lower case", node.get_SourcePositionStart());
    }
  }

}
