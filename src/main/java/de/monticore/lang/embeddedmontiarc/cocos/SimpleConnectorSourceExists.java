/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
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
