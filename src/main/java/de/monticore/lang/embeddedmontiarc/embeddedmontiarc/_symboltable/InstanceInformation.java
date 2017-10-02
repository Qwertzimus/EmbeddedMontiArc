package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.types2._ast.ASTSimpleReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolutionDeclaration;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberTypeArgument;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import org.jscience.mathematics.number.Rational;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class InstanceInformation {
    protected String compName;
    protected ASTSubComponent astSubComponent;

    public InstanceInformation() {

    }

    public InstanceInformation(String compName, ASTSubComponent astSubComponent) {
        this.compName = compName;
        this.astSubComponent = astSubComponent;
    }


    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public ASTSubComponent getASTSubComponent() {
        return astSubComponent;
    }

    public void setASTSubComponent(ASTSubComponent astSubComponent) {
        this.astSubComponent = astSubComponent;
    }

    public int getInstanceNumberForArgumentIndex(int index) {
        return getInstanceNumberFromASTSubComponent(astSubComponent, index);
    }

    public int getInstanceNumberForPortName(String portName) {
        Symbol symbol = getASTSubComponent().getSymbol().get();
        ComponentInstanceSymbol componentInstanceSymbol = (ComponentInstanceSymbol) symbol;
        Log.debug(componentInstanceSymbol.getComponentType().toString(), "ComponentInstanceSymbol");
        Log.debug(portName, "PortName");
        PortArraySymbol namedArray = componentInstanceSymbol.getComponentType().getPortArray(portName);
        if (namedArray.getNameSizeDependsOn().isPresent())
            Log.debug(namedArray.getNameSizeDependsOn().get(), "PortArray Depends On:");

        int counter = 0;
        for (ResolutionDeclarationSymbol resolutionDeclarationSymbol : componentInstanceSymbol.getComponentType().getResolutionDeclarationSymbols()) {
            if (componentInstanceSymbol.getComponentType().isPortDependentOnResolutionDeclarationSymbol(portName, resolutionDeclarationSymbol.getNameToResolve())) {
                Log.debug("Name: " + portName + " nameToResolve: " + resolutionDeclarationSymbol.getNameToResolve(), "Porty Depends On:");
                return getInstanceNumberFromASTSubComponent(getASTSubComponent(), counter);
            }
            ++counter;
        }


        return -1;
    }


    public List<Integer> getInstanceNumberForArguments() {
        List<Integer> intList = new ArrayList<>();

        int curIndex = 0;
        int curResult = 0;
        while (true) {
            curResult = getInstanceNumberFromASTSubComponent(getASTSubComponent(), curIndex);
            if (curResult != -1) {
                intList.add(curResult);
            } else {
                break;
            }
        }

        return intList;
    }

    public static int getInstanceNumberFromASTSubComponent(ASTSubComponent subComponent, int index) {
        if (subComponent.getType() instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) subComponent.getType();
            if (simpleReferenceType.getTypeArguments().isPresent()) {
                int counter = 0;
                for (ASTTypeArgument astTypeArgument : simpleReferenceType.getTypeArguments().get().getTypeArguments()) {
                    if (astTypeArgument instanceof ASTUnitNumberTypeArgument) {
                        if (((ASTUnitNumberTypeArgument) astTypeArgument).getUnitNumber().getNumber().isPresent()) {
                            if (counter == index)
                                return ((ASTUnitNumberTypeArgument) astTypeArgument).getUnitNumber().getNumber().get().intValue();
                            ++counter;
                        }

                    } else if (astTypeArgument instanceof ASTUnitNumberResolution) {
                        if (((ASTUnitNumberResolution) astTypeArgument).getUnitNumber().isPresent()) {
                            if (counter == index)
                                return ((ASTUnitNumberResolution) astTypeArgument).getNumber().get().intValue();
                            ++counter;
                        }
                    }
                }
            }

        }
        return -1;
    }

    public static String getInstanceNameFromASTSubComponent(ASTSubComponent subComponent, int index) {
        if (subComponent.getType() instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) subComponent.getType();
            if (simpleReferenceType.getTypeArguments().isPresent()) {
                int counter = 0;
                for (ASTTypeArgument astTypeArgument : simpleReferenceType.getTypeArguments().get().getTypeArguments()) {
                    if (astTypeArgument instanceof ASTUnitNumberResolution) {
                        if (((ASTUnitNumberResolution) astTypeArgument).getName().isPresent()) {
                            if (counter == index)
                                return ((ASTUnitNumberResolution) astTypeArgument).getName().get();
                            ++counter;
                        }

                    }
                }
            }

        }
        return null;
    }

    public static void setInstanceNumberInASTSubComponent(ASTSubComponent subComponent, String nameToSet, int numberToSet) {
        if (subComponent.getType() instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) subComponent.getType();
            if (simpleReferenceType.getTypeArguments().isPresent()) {
                int counter = 0;
                for (ASTTypeArgument astTypeArgument : simpleReferenceType.getTypeArguments().get().getTypeArguments()) {
                    if (astTypeArgument instanceof ASTUnitNumberResolution) {
                        if ((((ASTUnitNumberResolution) astTypeArgument).getName().isPresent())) {
                            String name = ((ASTUnitNumberResolution) astTypeArgument).getName().get();
                            if (name.equals(nameToSet))
                                ((ASTUnitNumberResolution) astTypeArgument).setNumber(Rational.valueOf("" + numberToSet));
                            ++counter;
                        }

                    }
                }
            }

        }
    }
}