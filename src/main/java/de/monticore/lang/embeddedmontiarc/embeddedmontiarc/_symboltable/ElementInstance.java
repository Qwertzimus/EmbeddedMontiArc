package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;
import de.monticore.symboltable.Scope;

import java.util.Collection;

/**
 * Created by kt on 27.02.2017.
 */
public interface ElementInstance {

    public Collection<TagSymbol> getTags();
    public <T extends TagSymbol> Collection<T> getTags(final TagKind tagKind);
    public String getName();
    public Scope getEnclosingScope() ;
}
