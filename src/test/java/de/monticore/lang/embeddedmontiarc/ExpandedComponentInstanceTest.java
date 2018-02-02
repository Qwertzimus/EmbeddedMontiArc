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
package de.monticore.lang.embeddedmontiarc;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.*;
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Tests for toString methods of EmbeddedMontiArc symbols.
 *
 * @author Michael von Wenckstern
 */
public class ExpandedComponentInstanceTest extends AbstractSymtabTest {

    @Test
    public void testFAS() throws Exception {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "fas.demo_fas_Fkt_m.fAS", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(inst);
    }


    @Test
    public void testComponentSub2() throws Exception {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "a.sub2", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(inst);
        System.out.println(inst);

        assertEquals(inst.getPorts().size(), 3);
        assertTrue(inst.getPort("in1[1]").isPresent()); // from a.Sub2
        assertTrue(inst.getPort("out1").isPresent()); // from a.Sub2

        for (ConnectorSymbol con : inst.getConnectors()) {
            Log.debug(con.toString(), "testComponentSub2");
        }
    }

    // @Ignore
    @Test
    public void testComponentSub2Sanity() throws Exception {
        Scope symTab = createSymTab("src/test/resources");
        ComponentSymbol inst = symTab.<ComponentSymbol>resolve(
                "a.Sub2", ComponentSymbol.KIND).orElse(null);
        assertNotNull(inst);
        System.out.println(inst);

        assertEquals(inst.getPorts().size(), 3);
        //assertTrue(inst.getPort("in1").isPresent()); // from a.Sub2
        //assertTrue(inst.getPort("out1").isPresent()); // from a.Sub2

        for (ConnectorSymbol con : inst.getConnectors()) {
            Log.debug(con.toString(), "testComponentSub2");
        }
    }
/* TODO add more tests*/

    // @Ignore
    @Test
    public void testSubGenericInstance() throws Exception {
        Scope symTab = createSymTab("src/test/resources/symtab");
        ComponentSymbol componentSymbol = symTab.<ComponentSymbol>resolve("generics.SubGeneric", ComponentSymbol.KIND).orElse(null);

        assertNotNull(componentSymbol);
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "generics.subGenericInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);
        System.out.println(inst);
        // test whether T is replaced by Integer
        inst.getPorts().stream().forEachOrdered(p -> assertEquals(p.getTypeReference().getName(), "Integer"));

        ExpandedComponentInstanceSymbol inst2 = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "generics.superGenericCompInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst2);
        System.out.println(inst2);
        // test whether T is replaced by Integer
        assertEquals("RangeType", inst2.getSubComponent("sgc").get().getPort("tIn").get().getTypeReference().getName());
        assertEquals("RangeType", inst2.getSubComponent("sgc").get().getPort("tOut").get().getTypeReference().getName());

        assertEquals("B", inst2.getSubComponent("sgc2").get().getPort("tIn").get().getTypeReference().getName());
        assertEquals("RangeType", inst2.getSubComponent("sgc2").get().getPort("tOut").get().getTypeReference().getName());
    }

    @Test
    public void testConnectorInstancing() {
        ConstantPortSymbol.resetLastID();
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "testing.connectorInstancing", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);
        System.out.println("inst: " + inst.toString());

        assertEquals(3, inst.getConnectors().size());

        ConstantPortSymbol portSymbol = (ConstantPortSymbol) inst.getPort("CONSTANTPORT1").get();
    }

    @Test
    public void testGenericA() {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "testing.genericAInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        ComponentSymbolReference symbol = inst.getSubComponents().iterator().next().getComponentType();
        Log.info(symbol.getReferencedSymbol().toString(), "component:");
        Log.debug(inst.getSubComponents().iterator().next().getComponentType().getReferencedSymbol().howManyResolutionDeclarationSymbol() + "", "Expanded:");

        assertNotNull(inst);
        System.out.println(inst);
        Log.debug(inst.getUnitNumberResolutionSubComponents("n") + "", "Expanded:");

    }


    @Test
    public void testSubGenericValueInstance() throws Exception {
        Scope symTab = createSymTab("src/test/resources/symtab");
        ComponentSymbol comp = symTab.<ComponentSymbol>resolve("generics.SubGenericValue", ComponentSymbol.KIND).orElse(null);
        assertNotNull(comp);
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "generics.subGenericValueInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);
        System.out.println(inst);
        // test whether T is replaced by Integer
    /*    inst.getPorts().stream().forEachOrdered(p -> assertEquals(p.getTypeReference().getName(), "Integer"));

        ExpandedComponentInstanceSymbol inst2 = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "generics.superGenericCompInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst2);
        System.out.println(inst2);
        // test whether T is replaced by Integer
        assertEquals("RangeType", inst2.getSubComponent("sgc").get().getPort("tIn").get().getTypeReference().getName());
        assertEquals("RangeType", inst2.getSubComponent("sgc").get().getPort("tOut").get().getTypeReference().getName());

        assertEquals("UnitNumberResolution", inst2.getSubComponent("sgc2").get().getPort("tIn").get().getTypeReference().getName());
        assertEquals("RangeType", inst2.getSubComponent("sgc2").get().getPort("tOut").get().getTypeReference().getName());
    */
    }

    @Test
    public void testBasicParameterInstance() {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "testing.basicParameterInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);
        System.out.println(inst);
        assertEquals(2, inst.getSubComponents().size());
        assertEquals(1, inst.getSubComponents().iterator().next().getParameters().size());
        for (ASTExpression astExpression : inst.getSubComponents().iterator().next().getArguments()) {
            Log.info(astExpression.toString(), "info:");
        }
        Iterator<ExpandedComponentInstanceSymbol> iterator = inst.getSubComponents().iterator();
        UnitNumberExpressionSymbol symbol1 = (UnitNumberExpressionSymbol) iterator.next().getArguments().get(0).getSymbol().get();
        UnitNumberExpressionSymbol symbol2 = (UnitNumberExpressionSymbol) iterator.next().getArguments().get(0).getSymbol().get();
        assertEquals("5", symbol1.getTextualRepresentation());
        assertEquals("1", symbol2.getTextualRepresentation());
    }

    @Test
    public void testAdaptableParameterInstance() {
        Scope symtab = createSymTab("src/test/resources");

        ExpandedComponentInstanceSymbol comp = symtab.<ExpandedComponentInstanceSymbol>resolve("testing.adaptableParameterInstance",ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(comp);

        ExpandedComponentInstanceSymbol subInst1 = comp.getSubComponent("adaptableParameter").orElse(null);
        assertNotNull(subInst1);

        PortSymbol configPort = subInst1.getIncomingPort("param1").orElse(null);
        assertNotNull(configPort);
        assertTrue(configPort.isConfig());

        assertNull(subInst1.getIncomingPort("param2").orElse(null));
    }

    @Test
    public void testExtensionMechanism1() {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "a.superCompExtension", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);
        System.out.println(inst);

    }

    @Test
    public void testExtensionMechanism2() {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "a.superCompGenericExtension", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);
        System.out.println(inst);

    }

    //Currently not working
    @Ignore
    @Test
    public void testExtensionMechanism3() {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "a.superCompGenericGenericExtensionInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);
        System.out.println(inst);

    }

    @Test
    public void testTypeVariableGenericsInstanciation2() {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol csInner = symTab.<ExpandedComponentInstanceSymbol>resolve("testing.basicResolutionInstance.br1", ExpandedComponentInstanceSymbol.KIND).orElse(null);
        assertNotNull(csInner);

        Log.debug(csInner.getFullName() + " " + csInner.getComponentType().getReferencedSymbol().howManyResolutionDeclarationSymbol(), "Amount ResolutionDeclarationSymbols :");


        ResolutionDeclarationSymbol jt = csInner.getResolutionDeclarationSymbol("n");
        assertNotNull(jt);
        //This works for all generic indices
        int result = InstanceInformation.getInstanceNumberFromASTSubComponent(csInner.getInstanceInformation().get().getASTSubComponent(), 0);
        assertEquals(6, result);
        //not working in expandedcomponentInstanceSymbols, use InstanceInformation.getInstanceNumber... as seen above
        //assertEquals(6, ((ASTUnitNumberResolution) jt.getASTResolution()).getNumber().get().intValue());
        assertEquals("br1", csInner.getName());
        assertEquals(6, csInner.getComponentType().getIncomingPorts().size());
        result = InstanceInformation.getInstanceNumberFromASTSubComponent(csInner.getInstanceInformation().get().getASTSubComponent(), 1);
        assertEquals(3, result);
    }

    @Test
    public void testConnectorCorrectness() {
        Scope symTab = createSymTab("src/test/resources");
        ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
                "testing.subComponentConnector", ExpandedComponentInstanceSymbol.KIND).orElse(null);

        assertNotNull(inst);

        testConnectorCorrectnessForComponent(inst);
    }

    private void testConnectorCorrectnessForComponent(ExpandedComponentInstanceSymbol inst) {
        inst.getConnectors().forEach(connectorSymbol -> {
            assertNotNull(connectorSymbol.getSourcePort());
            assertNotNull(connectorSymbol.getTargetPort());

            PortSymbol sourcePort = connectorSymbol.getSourcePort();
            PortSymbol targetPort = connectorSymbol.getTargetPort();

             System.out.println("source: " + sourcePort.getFullName());
             System.out.println("target: " + targetPort.getFullName() + "\n");

            assertNotEquals(sourcePort.getFullName(), targetPort.getFullName());
        });

        inst.getSubComponents().forEach(this::testConnectorCorrectnessForComponent);
    }

/*
  @Test
  public void testGenericInstance() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "generics.genericInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

    assertNotNull(inst);
    System.out.println(inst);

    //<editor-fold desc="How to derive types through generics">
    /*
    component GenericInstance {
      component Generic<T extends Number> {
        ports in T in1,
              in T in2,
              out T out1;

        component SuperGenericComparableComp2<String, T> sc1;
        component SuperGenericComparableComp2<Integer, T> sc2;
      }

      component Generic<Double> gDouble;
      component Generic<Integer> gInteger;
    }

    component SuperGenericComparableComp2<K, T extends Comparable<T>> {

        port
            in T tIn,
            out K tOut;
    }

    ==>
    component GenericInstance {
      component Generic<T=Double> gDouble {
        ports in Double in1,
              in Double in2,
              out Double out1;

        component SuperGenericComparableComp2<K=String, T=Double> sc1 {
          port
            in Double tIn,
            out String tOut;
        }

        component SuperGenericComparableComp2<K=Integer, T=Double> sc2 {
          port
            in Double tIn,
            out Integer tOut;
        }
      }

      component Generic<T=Integer> gInteger {
        ports in Integer in1,
              in Integer in2,
              out Integer out1;

        component SuperGenericComparableComp2<K=String, T=Integer> sc1 {
          port
            in Integer tIn,
            out String tOut;
        }

        component SuperGenericComparableComp2<K=Integer, T=Integer> sc2 {
          port
            in Integer tIn,
            out Integer tOut;
        }
      }
     */
    //</editor-fold>
 /*
    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc1")
        .get().getPort("tIn").get().getTypeReference().getName(), "Double");
    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc1")
        .get().getPort("tOut").get().getTypeReference().getName(), "String");

    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc2")
        .get().getPort("tIn").get().getTypeReference().getName(), "Double");
    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc2")
        .get().getPort("tOut").get().getTypeReference().getName(), "Integer");

    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc1")
        .get().getPort("tIn").get().getTypeReference().getName(), "Integer");
    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc1")
        .get().getPort("tOut").get().getTypeReference().getName(), "String");

    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc2")
        .get().getPort("tIn").get().getTypeReference().getName(), "Integer");
    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc2")
        .get().getPort("tOut").get().getTypeReference().getName(), "Integer");
  }

  @Test
  public void testGenericExtension() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "generics.baseClassGenerics", ExpandedComponentInstanceSymbol.KIND).orElse(null);

    assertNotNull(inst);
    System.out.println(inst);

    assertEquals(inst.getPort("boolIn").get().getTypeReference().getName(), "Boolean");
    assertEquals(inst.getPort("intOut").get().getTypeReference().getName(), "Integer");
    assertEquals(inst.getPort("sIn1").get().getTypeReference().getName(), "String"); // test if T is replaced by String
    assertEquals(inst.getPort("sIn2").get().getTypeReference().getName(), "String");
    assertEquals(inst.getPort("sOut").get().getTypeReference().getName(), "String");
  }

  @Test
  public void testLoadingInstancePort() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    PortSymbol port = symTab.<PortSymbol>resolve(
        "a.sub1.cComp.in1", PortSymbol.KIND).orElse(null);
    assertNotNull(port);
    System.out.println(port);
  }

  @Test
  public void testFAS() throws Exception {
    Scope symTab = createSymTab("src/test/resources/fas");
    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve(
        "DEMO_FAS.DEMO_FAS.DEMO_FAS_Funktion.CC_On_Off", ComponentSymbol.KIND).orElse(null);
    assertNotNull(cmp);
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "DEMO_FAS.DEMO_FAS.DEMO_FAS_Funktion.cC_On_Off", ExpandedComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(inst);

    symTab = createSymTab("src/test/resources/fas");
    cmp = symTab.<ComponentSymbol>resolve(
        "DEMO_FAS.DEMO_FAS.DEMO_FAS_Funktion.Limiter", ComponentSymbol.KIND).orElse(null);
    assertNotNull(cmp);
    inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "DEMO_FAS.DEMO_FAS.DEMO_FAS_Funktion.limiter", ExpandedComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(inst);
  }
  */
}
