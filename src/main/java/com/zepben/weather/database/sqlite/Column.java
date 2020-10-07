/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.weather.database.sqlite;

import com.zepben.annotations.EverythingIsNonnullByDefault;

/**
 * Defines a column to be used by a {@link Table}
 */
@EverythingIsNonnullByDefault
@SuppressWarnings("WeakerAccess")
public class Column {

    private final int index;
    private final String name;
    private final String type;
    private final boolean isNullable;
    private final boolean isPrimaryKey;

    Column(int index, String name, String type, boolean isNullable, boolean isPrimaryKey) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.isNullable = isNullable;
        this.isPrimaryKey = isPrimaryKey;
    }

    public int index() {
        return index;
    }

    public String name() {
        return name;
    }

    @SuppressWarnings("WeakerAccess")
    public String type() {
        return type;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isNullable() {
        return isNullable;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

}
