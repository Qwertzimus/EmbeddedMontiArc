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
package de.monticore.lang.embeddedmontiarc.tagging;

import de.monticore.lang.tagging._symboltable.TagKind;

import java.util.Optional;

public class RosConnectionSymbol extends MiddlewareSymbol {
    public static final RosConnectionKind KIND = RosConnectionKind.INSTANCE;

    public RosConnectionSymbol() {
        super(KIND, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public RosConnectionSymbol(String topicName, String topicType) {
        this(KIND, topicName, topicType);
    }

    public RosConnectionSymbol(RosConnectionKind kind, String topicName, String topicType) {
        super(kind, Optional.ofNullable(topicName), Optional.ofNullable(topicType), Optional.empty());
    }

    public RosConnectionSymbol(String topicName, String topicType, String msgField) {
        this(KIND, topicName, topicType, msgField);
    }

    protected RosConnectionSymbol(RosConnectionKind kind, String topicName, String topicType, String msgField) {
        super(kind, Optional.ofNullable(topicName), Optional.ofNullable(topicType), Optional.ofNullable(msgField));
    }

    @Override
    public String toString() {
        return String.format("RosConnection = (%s, %s), %s",
                getTopicName(), getTopicType(), getMsgField());
    }

    public Optional<String> getTopicName() {
        return getValue(0);
    }

    public Optional<String> getTopicType() {
        return getValue(1);
    }

    public Optional<String> getMsgField() {
        return getValue(2);
    }

    public void setTopicName(String topicName) {
        this.values.set(0, Optional.ofNullable(topicName));
    }

    public void setTopicType(String topicType) {
        this.values.set(1, Optional.ofNullable(topicType));
    }

    public void setMsgField(String msgField) {
        this.values.set(2, Optional.ofNullable(msgField));
    }

    public static class RosConnectionKind extends TagKind {
        public static final RosConnectionKind INSTANCE = new RosConnectionKind();

        protected RosConnectionKind() {
        }
    }
}