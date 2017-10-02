/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 */
public class ConnectorBuilder {
  protected Optional<String> source = Optional.empty();
  protected Optional<String> target = Optional.empty();
  protected Optional<ConstantPortSymbol> portSymbol = Optional.empty();

  public static ConnectorSymbol clone(ConnectorSymbol con) {
    return new ConnectorBuilder().setSource(con.getSource()).
        setTarget(con.getTarget()).build();
  }

  public ConnectorBuilder setSource(String source) {
    this.source = Optional.of(source);
    return this;
  }

  public ConnectorBuilder setTarget(String target) {
    this.target = Optional.of(target);
    return this;
  }
 
  public ConnectorBuilder setConstantPortSymbol(ConstantPortSymbol portSymbol) {
    this.portSymbol = Optional.of(portSymbol);
    return this;
  }

  public ConnectorSymbol build() {
    if (source.isPresent() && target.isPresent()) {
      ConnectorSymbol con = new ConnectorSymbol(this.target.get());
      con.setSource(this.source.get());
      con.setTarget(this.target.get());
	  if(portSymbol.orElse(null) != null) {
        con.setConstantPortSymbol(portSymbol.get());
	  }
      return con;
    }
    Log.error("not all parameters have been set before to build the connector symbol");
    throw new Error("not all parameters have been set before to build the connector symbol");
  }
}
