/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.embeddedmontiarc.helper.SymbolPrinter;
import de.monticore.lang.monticar.stream._symboltable.NamedStreamSymbol;
import de.monticore.lang.montiarc.tagging._symboltable.TaggingSymbol;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Symboltable entry for ports.
 */
public class PortSymbol extends TaggingSymbol {
    public static final EmbeddedPortKind KIND = EmbeddedPortKind.INSTANCE;

    private final Map<String, Optional<String>> stereotype = new HashMap<>();

    /**
     * Maps direction incoming to true.
     */
    public static final boolean INCOMING = true;

    /**
     * Flags, if this port is incoming.
     */
    private boolean incoming;

    private JTypeReference<? extends JTypeSymbol> typeReference;

    private MutableScope locallyDefinedStreams = new CommonScope();

    protected Optional<String> nameDependsOn = Optional.empty();

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
     * @return true,  if this is an incoming port, else false.
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
    public JTypeReference<? extends JTypeSymbol> getTypeReference() {
        return this.typeReference;
    }

    /**
     * @param typeReference reference to the type from this port
     */
    public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
        this.typeReference = typeReference;
    }

    /**
     * returns the component which defines the connector
     * this is independent from the component to which the source and target ports
     * belong to
     *
     * @return is optional, b/c a connector can belong to a component symbol or to
     * an expanded component instance symbol
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
     * returns the expanded component instance which defines the connector
     * this is independent from the component to which the source and target ports
     * belong to
     *
     * @return is optional, b/c a connector can belong to a component symbol or to
     * an expanded component instance symbol
     */
    public Optional<ExpandedComponentInstanceSymbol> getComponentInstance() {
        if (!this.getEnclosingScope().getSpanningSymbol().isPresent()) {
            return Optional.empty();
        }
        if (!(this.getEnclosingScope().getSpanningSymbol().get() instanceof ExpandedComponentInstanceSymbol)) {
            return Optional.empty();
        }
        return Optional.of((ExpandedComponentInstanceSymbol) this.getEnclosingScope().getSpanningSymbol().get());
    }

    /**
     * Adds the stereotype key=value to this entry's map of stereotypes
     *
     * @param key      the stereotype's key
     * @param optional the stereotype's value
     */
    public void addStereotype(String key, Optional<String> optional) {
        stereotype.put(key, optional);
    }

    /**
     * Adds the stereotype key=value to this entry's map of stereotypes
     *
     * @param key   the stereotype's key
     * @param value the stereotype's value
     */
    public void addStereotype(String key, @Nullable String value) {
        if (value != null && value.isEmpty()) {
            value = null;
        }
        stereotype.put(key, Optional.ofNullable(value));
    }

    /**
     * the nonunitstreams.streams are sorted, first they are sorted regarding to the id,
     * and for nonunitstreams.streams with the same id first all expected nonunitstreams.streams are coming
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
                            if (i != 0) return i;

                            return Boolean.compare(
                                    !e1.isExpected(),
                                    !e2.isExpected());
                        })
                .collect(Collectors.toList());
    }

    /**
     * creates a stream value for the port
     *
     * @param id          the id-group to which the stream belongs to
     * @param expected    {@link NamedStreamSymbol#isExpected()}
     * @param timedValues {@link NamedStreamSymbol#getValue(int)}
     * @return the created symbol which has been added to the port
     */
    public NamedStreamSymbol addStream(int id, boolean expected, final Collection<Object> timedValues) {
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

    /**
     * @return map representing the stereotype of this component
     */
    public Map<String, Optional<String>> getStereotype() {
        return stereotype;
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
                nameWithOutArrayBracketPart = nameWithOutArrayBracketPart.substring(0, nameWithOutArrayBracketPart.length() - 1);
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
        Log.debug("compName: " + getName() + "name depends: " + nameDependsOn.toString(), "Set Name Depends On");
    }

    public boolean isPartOfPortArray() {
        return getName().contains("[") && getName().contains("]");
    }
}
