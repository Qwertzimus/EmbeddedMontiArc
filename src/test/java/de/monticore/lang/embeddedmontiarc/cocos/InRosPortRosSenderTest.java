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
package de.monticore.lang.embeddedmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InRosPortRosSenderTest extends AbstractTaggingCoCoTest{

    @BeforeClass
    public static void init() {
        Log.enableFailQuick(false);
    }

    @Before
    public void setUp() {
        Log.getFindings().clear();
    }

    @Test
    public void testValidRosToRos() {
        testCoCosOnComponent("middleware.ros.cocos.RosToRosComp");
    }

    @Test
    public void testNoRosToRos() {
        testCoCosOnComponent("middleware.ros.cocos.NoRosToRosComp", "0x3830a");
    }

    @Test
    public void testTopicNameMismatch() {
        testCoCosOnComponent("middleware.ros.cocos.TopicNameMismatch", "0x23a0d");
    }

    @Test
    public void testTopicTypeMismatch() {
        testCoCosOnComponent("middleware.ros.cocos.TopicTypeMismatch", "0x31f6e");
    }

    @AfterClass
    public static void finish(){
        Log.getFindings().clear();
    }
}
