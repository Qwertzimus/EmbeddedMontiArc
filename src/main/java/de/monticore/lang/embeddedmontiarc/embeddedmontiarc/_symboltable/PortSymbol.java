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
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.lang.embeddedmontiarc.helper.SymbolPrinter;
import de.monticore.lang.embeddedmontiarc.tagging.MiddlewareSymbol;
import de.monticore.lang.embeddedmontiarc.tagging.RosConnectionSymbol;
import de.monticore.lang.monticar.stream._symboltable.NamedStreamSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

/**
 * Symboltable entry for ports.
 */
public class PortSymbol extends CommonSymbol implements ElementInstance {
  
  public static final EmbeddedPortKind KIND = EmbeddedPortKind.INSTANCE;
  
  /**
   * Maps direction incoming to true.
   */
  public static final boolean INCOMING = true;
  
  /**
   * Flags, if this port is incoming.
   */
  private boolean incoming;
  
  private MCTypeReference<? extends MCTypeSymbol> typeReference;
  
  private MutableScope locallyDefinedStreams = new CommonScope();
  
  protected Optional<String> nameDependsOn = Optional.empty();

  private Optional<MiddlewareSymbol> middlewareSymbol = Optional.empty();

  /**
   * use {@link #builder()}
   */
  protected PortSymbol(String name) {
    super(name, KIND);
  }
  
  protected PortSymbol(String name, SymbolKind kind) {
    super(name, kind);
  }
  
  public static EMAPortBuilder builder() {
    return new EMAPortBuilder();
  }
  
  /**
   * @param isIncoming incoming = true, outgoing = false
   */
  public void setDirection(boolean isIncoming) {
    incoming = isIncoming;
  }
  
  /**
   * @return true, if this is an incoming port, else false.
   */
  public boolean isIncoming() {
    return incoming;
  }
  
  /**
   * @return true, if this is an outgoing port, else false.
   */
  public boolean isOutgoing() {
    return !isIncoming();
  }
  
  /**
   * @return typeReference reference to the type from this port
   */
  public MCTypeReference<? extends MCTypeSymbol> getTypeReference() {
    return this.typeReference;
  }
  
  /**
   * @param typeReference reference to the type from this port
   */
  public void setTypeReference(MCTypeReference<? extends MCTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }
  
  /**
   * returns the component which defines the connector this is independent from the component to
   * which the source and target ports belong to
   *
   * @return is optional, b/c a connector can belong to a component symbol or to an expanded
   * component instance symbol
   */
  public Optional<ComponentSymbol> getComponent() {
    if (!this.getEnclosingScope().getSpanningSymbol().isPresent()) {
      return Optional.empty();
    }
    if (!(this.getEnclosingScope().getSpanningSymbol().get() instanceof ComponentSymbol)) {
      return Optional.empty();
    }
    return Optional.of((ComponentSymbol) this.getEnclosingScope().getSpanningSymbol().get());
  }
  
  /**
   * returns the expanded component instance which defines the connector this is independent from
   * the component to which the source and target ports belong to
   *
   * @return is optional, b/c a connector can belong to a component symbol or to an expanded
   * component instance symbol
   */
  public Optional<ExpandedComponentInstanceSymbol> getComponentInstance() {
    if (!this.getEnclosingScope().getSpanningSymbol().isPresent()) {
      return Optional.empty();
    }
    if (!(this.getEnclosingScope().getSpanningSymbol()
        .get() instanceof ExpandedComponentInstanceSymbol)) {
      return Optional.empty();
    }
    return Optional
        .of((ExpandedComponentInstanceSymbol) this.getEnclosingScope().getSpanningSymbol().get());
  }
  
  /**
   * the nonunitstreams.streams are sorted, first they are sorted regarding to the id, and for
   * nonunitstreams.streams with the same id first all expected nonunitstreams.streams are coming
   *
   * @return nonunitstreams.streams for the port, one stream could look like <i>5 tick 6 tick 7</i>
   */
  public Collection<NamedStreamSymbol> getStreams() {
    final Collection<NamedStreamSymbol> allStreams = new ArrayList<>();
    allStreams.addAll(locallyDefinedStreams.resolveLocally(NamedStreamSymbol.KIND));
    
    allStreams.addAll(this.getEnclosingScope().resolveMany(
        this.getFullName(), NamedStreamSymbol.KIND));
    return allStreams.stream()
        .sorted(
            (e1, e2) -> {
              int i = Integer.compare(e1.getId(), e2.getId());
              if (i != 0)
                return i;
              
              return Boolean.compare(
                  !e1.isExpected(),
                  !e2.isExpected());
            })
        .collect(Collectors.toList());
  }
  
  /**
   * creates a stream value for the port
   *
   * @param id the id-group to which the stream belongs to
   * @param expected {@link NamedStreamSymbol#isExpected()}
   * @param timedValues {@link NamedStreamSymbol#getValue(int)}
   * @return the created symbol which has been added to the port
   */
  public NamedStreamSymbol addStream(int id, boolean expected,
      final Collection<Object> timedValues) {
    NamedStreamSymbol stream = new NamedStreamSymbol(this.getName(), id, expected, timedValues);
    
    locallyDefinedStreams.add(stream);
    return stream;
  }
  
  @Override
  public void setEnclosingScope(MutableScope scope) {
    super.setEnclosingScope(scope);
    
    if (scope != null)
      locallyDefinedStreams.setResolvingFilters(scope.getResolvingFilters());
  }
  
  @Override
  public String toString() {
    return SymbolPrinter.printPort(this);
  }
  
  public boolean isConstant() {
    return false;
  }
  
  public String getNameWithoutArrayBracketPart() {
    return getNameWithoutArrayBracketPart(this.getName());
  }
  
  public static String getNameWithoutArrayBracketPart(String name) {
    String nameWithOutArrayBracketPart = name;
    if (nameWithOutArrayBracketPart.endsWith("]")) {
      char lastChar;
      do {
        lastChar = nameWithOutArrayBracketPart.charAt(nameWithOutArrayBracketPart.length() - 1);
        nameWithOutArrayBracketPart = nameWithOutArrayBracketPart.substring(0,
            nameWithOutArrayBracketPart.length() - 1);
      } while (lastChar != '[');
    }
    return nameWithOutArrayBracketPart;
  }
  
  @Deprecated
  public Optional<String> getNameDependsOn() {
    return nameDependsOn;
  }
  
  @Deprecated
  public void setNameDependsOn(Optional<String> nameDependsOn) {
    
    this.nameDependsOn = nameDependsOn;
    Log.debug("compName: " + getName() + "name depends: " + nameDependsOn.toString(),
        "Set Name Depends On");
  }
  
  public boolean isPartOfPortArray() {
    return getName().contains("[") && getName().contains("]");
  }
  
  /**
   * if model input is; component X { port ...; component A { port in Integer p1, out Integer p2; }
   * component A a1, a2, a3; connect a1.p2 -> a2.p1, a3.p1; } if I have the port symbol a1.p2 than
   * this method returns the list of port symbols {a2.p1, a3.p1}
   * 
   * @return
   */
  public List<PortSymbol> getTargetConnectedPorts(ExpandedComponentInstanceSymbol topComponent) {
    
    // It does not works for components, when one of them is top component and another not.
    
    List<PortSymbol> targetPorts = new ArrayList<>();
    
    if (!topComponent.getConnectors().equals(null)) {
      // If the port is Outgoing then return incoming ports of connected components
      if (this.isOutgoing()) {
        topComponent.getConnectors().stream()
            .filter(s -> s.getSourcePort().equals(this))
            .forEach(s -> targetPorts.add(s.getTargetPort()));
      }
      else if (this.isIncoming()) {
        // If the port is incoming then return outgoing ports of connected components
        topComponent.getConnectors().stream()
            .filter(s -> s.getTargetPort().equals(this))
            .forEach(s -> targetPorts.add(s.getSourcePort()));
      }
    }
    return targetPorts;
    
    // TODO: Find the way to get connections from the top element
  }
  
  public static boolean isConstantPortName(String name) {
    if (name.contains(".")) {
      String secondPart = name.split("\\.")[1];
      return secondPart.startsWith("CONSTANTPORT");
    }
    else {
      return name.startsWith("CONSTANTPORT");
    }
  }

  public void setMiddlewareSymbol(MiddlewareSymbol middlewareSymbol){
    this.middlewareSymbol = Optional.of(middlewareSymbol);
  }

  public Optional<MiddlewareSymbol> getMiddlewareSymbol(){
    return middlewareSymbol;
  }

  public boolean isRosPort(){
    return getMiddlewareSymbol().isPresent() && getMiddlewareSymbol().get().isKindOf(RosConnectionSymbol.KIND);
  }
}
