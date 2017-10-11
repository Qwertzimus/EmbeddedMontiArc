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

public class ComponentLayoutSymbol extends DrawableSymbol {
  public static final ComponentLayoutKind KIND = ComponentLayoutKind.INSTANCE;

  public ComponentLayoutSymbol(int id, int x, int y, int width, int height,
      int layoutPosition, boolean isOnTop, int reservedHorizontalSpace) {
    super(KIND, id, x, y);
    addValues(width, height, layoutPosition, isOnTop, reservedHorizontalSpace);
  }

  protected ComponentLayoutSymbol(ComponentLayoutKind kind, int id, int x, int y, int width, int height,
      int layoutPosition, boolean isOnTop, int reservedHorizontalSpace) {
    super(kind, id, x, y);
    addValues(width, height, layoutPosition, isOnTop, reservedHorizontalSpace);
  }

  public int getWidth() {
    return getValue(3);
  }

  public int getHeight() {
    return getValue(4);
  }

  public int getLayoutPosition() {
    return getValue(5);
  }

  public boolean isOnTop() {
    return getValue(6);
  }

  public int getReservedHorizontalSpace() {
    return getValue(7);
  }

  @Override
  public String toString() {
    return String.format(
        "componentLayout = { id=%d, x=%d, y=%d, width=%d, height=%d, \n" +
            "                  layoutPosition=%d, %s reservedHorizontalSpace=%d }",
        getId(), getX(), getY(), getWidth(), getHeight(),
        getLayoutPosition(), isOnTop() ? "isOnTop," : "", getReservedHorizontalSpace());
  }

  public static class ComponentLayoutKind extends DrawableKind {
    public static final ComponentLayoutKind INSTANCE = new ComponentLayoutKind();

    protected ComponentLayoutKind() {
    }
  }
}
