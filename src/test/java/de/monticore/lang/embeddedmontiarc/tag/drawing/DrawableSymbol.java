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
package de.monticore.lang.embeddedmontiarc.tag.drawing;

import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;

/**
 * Super class which defines basic properties for all drawable objects
 * Derived classes: Canvas, Component, Port and Connector
 */
public class DrawableSymbol extends TagSymbol {
  public static final DrawableKind KIND = DrawableKind.INSTANCE;

  // unique id moved to IdGenerator

  public DrawableSymbol(int id, int x, int y) {
    super(KIND, id, x, y);
    // always store all values in the super class
    // b/c this class handles equals and hashCode for you
  }

  protected DrawableSymbol(DrawableKind kind, int id, int x, int y) {
    super(kind, id, x, y);
  }

  public int getId() {
    return getValue(0);
  }

  public int getX() {
    return getValue(1);
  }

  public int getY() {
    return getValue(2);
  }

  @Override
  public String toString() {
    return String.format("drawable { id=%s, x=%s, y=%s }",  getId(), getX(), getY());
  }

  public static class DrawableKind extends TagKind {
    public static final DrawableKind INSTANCE = new DrawableKind();

    protected DrawableKind() {
    }
  }
}
