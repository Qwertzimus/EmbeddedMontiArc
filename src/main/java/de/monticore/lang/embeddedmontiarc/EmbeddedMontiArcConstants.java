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

import de.monticore.lang.embeddedmontiarc.helper.AutoconnectMode;
import de.monticore.lang.embeddedmontiarc.helper.Timing;

import java.io.File;

/**
 * Constants for the MontiArc language. <br>
 * <br> 
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * @author (last commit) $Author: ahaber $
 *          $Revision: 3080 $
 */
public final class EmbeddedMontiArcConstants {

  /**
   * Default auto connect mode = OFF.
   */
  public static final AutoconnectMode DEFAULT_AUTO_CONNECT = AutoconnectMode.OFF;

  /**
   * Default time paradigm = timed.
   *
   * @since 2.3.0
   */
  public static final Timing DEFAULT_TIME_PARADIGM = Timing.INSTANT;

  /**
   * Default documentation directory.
   */
  public static final String DEFAULT_DOC_DIR = "target" + File.separator + "madoc";

  /**
   * Default output directory.
   */
  public static final String DEFAULT_GEN_DIR = "target" + File.separator +
      "generated-sources" + File.separator +
      "montiarc" + File.separator +
      "sourcecode";

  /**
   * Default model directory.
   */
  public static final String DEFAULT_MODEL_DIR = "src" + File.separator + "main" + File.separator
      + "models";

}
