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
  EXECUTE(1),
  PASSWORD(2),
  KILL(3),
  MONITOR(4),
  PROXY(5);

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
        return EXECUTE;
      case 2:
        return PASSWORD;
      case 3:
        return KILL;
      case 4:
        return MONITOR;
      case 5:
        return PROXY;
      default:
        return null;
    }
  }

  public static Action findByName(String name) {
    for (Action action:Action.values()) {
      if (action.name().equalsIgnoreCase(name)) {
        return action;
      }
    }
    return null;
  }

}
