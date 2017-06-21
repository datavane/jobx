/**
 * Copyright 2016 benjobs
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.opencron.common.job;

public enum Action implements org.apache.thrift.TEnum {
  PING(0),
  PATH(1),
  MONITOR(2),
  EXECUTE(3),
  PASSWORD(4),
  KILL(5),
  PROXY(6),
  GUID(7),
  RESTART(8);

  private final int value;

  private Action(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static Action findByValue(int value) {
    switch (value) {
      case 0:
        return PING;
      case 1:
        return PATH;
      case 2:
        return MONITOR;
      case 3:
        return EXECUTE;
      case 4:
        return PASSWORD;
      case 5:
        return KILL;
      case 6:
        return PROXY;
      case 7:
        return GUID;
      case 8:
        return RESTART;
      default:
        return null;
    }
  }

  public static Action findByName(String name) {
    for (Action action: Action.values()) {
      if (action.name().equalsIgnoreCase(name)) {
        return action;
      }
    }
    return null;
  }

}