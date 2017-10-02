/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

/**
 * TODO Do we really need this?
 *
 * @author Robert Heim
 */
public class EMAAComponentImplementationKind
  extends de.monticore.lang.montiarc.montiarc._symboltable.AComponentImplementationKind
    implements de.monticore.symboltable.SymbolKind {

  public static final EMAAComponentImplementationKind INSTANCE = new EMAAComponentImplementationKind();

  protected EMAAComponentImplementationKind() {
  }

}
