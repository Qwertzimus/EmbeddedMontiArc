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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.*;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Implementation of R7
 *
 * @author Crispin Kirchner
 */
public class SimpleConnectorSourceExists implements EmbeddedMontiArcASTComponentCoCo {
  
  /**
   * TODO: either check why ConnectorSymbol has no proper value for sourcePosition, or reimplement
   * using
   * 
   * @see EmbeddedMontiArcASTComponentCoCo#check(ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol symbol = (ComponentSymbol) node.getSymbol().orElse(null);
    
    if (null == symbol) {
      Log.error(String.format("0x9AF6C ASTComponent node \"%s\" has no symbol. Did you forget to "
          + "run the SymbolTableCreator before checking cocos?", node.getName()));
      return;
    }
    
    for (ComponentInstanceSymbol instanceSymbol : symbol.getSubComponents()) {
      for (ConnectorSymbol connectorSymbol : instanceSymbol.getSimpleConnectors()) {
        
        ComponentSymbolReference typeReference = instanceSymbol.getComponentType();
        
        if (!typeReference.existsReferencedSymbol()) {
          Log.error(String.format("0xBEA8B The component type \"%s\" can't be resolved.",
              typeReference.getFullName()));
          return;
        }
        
        ComponentSymbol sourceComponent = typeReference.getReferencedSymbol();
        String sourcePort = connectorSymbol.getSource();
        
        Optional<PortSymbol> outgoingPort = sourceComponent.getOutgoingPort(sourcePort);
        
        if (!outgoingPort.isPresent()) {
          Log.error(String.format("0xF4D71 Out port \"%s\" is not present in component \"%s\".",
              sourcePort, sourceComponent.getName()),
              connectorSymbol.getSourcePosition());
        }
      }
    }
  }
  
}
