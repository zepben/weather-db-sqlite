/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.weather.database.sqlite;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: This is very similar to the implementation for saving the network model. Look at merging the 2 and making a single module.
 */
@EverythingIsNonnullByDefault
@SuppressWarnings("WeakerAccess")
abstract class Table {

    private final String name;
    @Nullable private List<Column> columns = null;
    private int colIndex = 0;

    Table(String name) {
        this.name = name;
    }

    @SuppressWarnings("SameParameterValue")
    Column col(String name, String type) {
        return new Column(++colIndex, name, type, false, false);
    }

    Column primaryCol(String name, String type) {
        return new Column(++colIndex, name, type, false, true);
    }

    Column nullableCol(String name, String type) {
        return new Column(++colIndex, name, type, true, false);
    }

    public String name() {
        return name;
    }

    public List<Column> columns() {
        if (columns == null) {
            List<Column> cols = new ArrayList<>();
            for (Field field : getClass().getDeclaredFields()) {
                if (field.getType() == Column.class) {
                    try {
                        cols.add((Column) field.get(this));
                    } catch (IllegalAccessException e) {
                        System.err.println("Unable to retrieve field " + field.getName() + ". It will be missing from the database: " + e.getMessage());
                    }
                }
            }
            cols.sort(Comparator.comparing(Column::index));
            columns = Collections.unmodifiableList(cols);
        }
        return columns;
    }

    public String createTableSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(name).append(" (")
            .append(columns().stream().map(this::createTableColumn).collect(Collectors.joining(", ")));

        List<Column> primaryCols = primaryCols();
        if (!primaryCols.isEmpty())
            sb.append(", PRIMARY KEY (").append(joinNames(primaryCols)).append(")");

        sb.append(")");
        return sb.toString();
    }

    public String selectSql() {
        return "SELECT " + joinNames(columns()) +
            " FROM " + name;
    }

    public String insertSql(List<Column> columns, List<List<String>> values) {
        return "INSERT INTO " + name + " (" + joinNames(columns) + ")" +
            " VALUES " + values
            .stream()
            .map(vals -> vals.stream().map(val -> val == null ? "NULL" : val).collect(Collectors.joining(", ", "(", ")")))
            .collect(Collectors.joining(", "));
    }

    public String preparedSelectSql() throws UnsupportedOperationException {
        List<Column> primaryCols = primaryCols();
        if (primaryCols.isEmpty())
            throw new UnsupportedOperationException("Only supported on tables with primary keys");

        return "SELECT " + joinNames(columns()) +
            " FROM " + name +
            " WHERE " + joinNamesAndBindParams(primaryCols, " AND ");
    }

    public String preparedInsertSql() {
        return "INSERT INTO " + name + " (" + joinNames(columns()) + ")" +
            " VALUES (" + joinBindParams(columns()) + ")";
    }

    public String preparedUpdateSql() {
        List<Column> primaryCols = primaryCols();
        if (primaryCols.isEmpty())
            throw new UnsupportedOperationException("Only supported on tables with primary keys");

        return "UPDATE " + name +
            " SET " + joinNamesAndBindParams(columns(), ", ") +
            " WHERE " + joinNamesAndBindParams(primaryCols, " AND ");
    }

    private String createTableColumn(Column column) {
        String str = column.name() + " " + column.type();
        if (!column.isNullable())
            str += " NOT NULL";
        return str;
    }

    private List<Column> primaryCols() {
        return columns()
            .stream()
            .filter(Column::isPrimaryKey)
            .collect(Collectors.toList());
    }

    private String joinNames(List<Column> columns) {
        return columns
            .stream()
            .map(Column::name)
            .collect(Collectors.joining(", "));
    }

    private String joinBindParams(List<Column> columns) {
        return columns
            .stream()
            .map(c -> "?")
            .collect(Collectors.joining(", "));
    }

    private String joinNamesAndBindParams(List<Column> columns, String delimiter) {
        return columns
            .stream()
            .map(c -> c.name() + " = ?")
            .collect(Collectors.joining(delimiter));
    }

}
