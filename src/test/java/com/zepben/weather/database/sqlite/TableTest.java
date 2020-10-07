/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.weather.database.sqlite;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.zepben.testutils.exception.ExpectException.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableTest {

    @SuppressWarnings("unused")
    static class MockTable extends Table {
        MockTable() {
            super("mock");
        }

        Column col1 = primaryCol("col1", "TEXT");
        Column col2 = primaryCol("col2", "NUMBER");
        Column col3 = col("col3", "TEXT");
        Column col4 = nullableCol("col4", "INTEGER");
        private Column col5 = nullableCol("col5", "INTEGER");
        String fakeCol = "col6";
    }

    @SuppressWarnings("unused")
    static class MockTableNoPrimary extends Table {
        MockTableNoPrimary() {
            super("mockNoPrimary");
        }

        Column col1 = col("col1", "TEXT");
        Column col2 = col("col2", "NUMBER");
        Column col3 = col("col3", "TEXT");
        Column col4 = nullableCol("col4", "INTEGER");
    }

    @Test
    public void name() {
        assertThat(new MockTable().name(), equalTo("mock"));
    }

    @Test
    public void createTableSql() {
        final String expected = "" +
            "CREATE TABLE mock (col1 TEXT NOT NULL, col2 NUMBER NOT NULL, " +
            "col3 TEXT NOT NULL, col4 INTEGER, PRIMARY KEY (col1, col2))";

        String sql = new MockTable().createTableSql();
        assertEquals(expected, sql);
    }

    @Test
    public void createTableSqlNoPrimary() {
        final String expected = "" +
            "CREATE TABLE mockNoPrimary (col1 TEXT NOT NULL, col2 NUMBER NOT NULL, " +
            "col3 TEXT NOT NULL, col4 INTEGER)";

        String sql = new MockTableNoPrimary().createTableSql();
        assertEquals(expected, sql);
    }

    @Test
    public void selectSql() {
        final String expected = "SELECT col1, col2, col3, col4 FROM mock";
        String sql = new MockTable().selectSql();
        assertEquals(expected, sql);
    }

    @Test
    public void insertSql() {
        final String expected = "INSERT INTO mock (col1, col4) VALUES ('VALUE1', 1), ('VALUE2', 2), ('VALUE3', NULL)";

        MockTable table = new MockTable();
        List<Column> cols = Arrays.asList(table.col1, table.col4);
        List<List<String>> values = new ArrayList<>();
        values.add(Arrays.asList("'VALUE1'", "1"));
        values.add(Arrays.asList("'VALUE2'", "2"));
        values.add(Arrays.asList("'VALUE3'", null));
        String sql = table.insertSql(cols, values);

        assertEquals(expected, sql);
    }

    @Test
    public void preparedSelectSql() {
        final String expected = "SELECT col1, col2, col3, col4 FROM mock WHERE col1 = ? AND col2 = ?";
        String sql = new MockTable().preparedSelectSql();
        assertEquals(expected, sql);
    }

    @Test
    public void preparedInsertSql() {
        final String expected = "INSERT INTO mock (col1, col2, col3, col4) VALUES (?, ?, ?, ?)";
        String sql = new MockTable().preparedInsertSql();
        assertEquals(expected, sql);
    }

    @Test
    public void preparedUpdateSql() {
        final String expected = "UPDATE mock SET col1 = ?, col2 = ?, col3 = ?, col4 = ? WHERE col1 = ? AND col2 = ?";
        String sql = new MockTable().preparedUpdateSql();
        assertEquals(expected, sql);
    }

    @Test
    public void invalidPreparedSelectSql() {
        expect(() -> new MockTableNoPrimary().preparedSelectSql())
            .toThrow(UnsupportedOperationException.class)
            .withMessage("Only supported on tables with primary keys");
    }

    @Test
    public void invalidPreparedUpdateSql() {
        expect(() -> new MockTableNoPrimary().preparedUpdateSql())
            .toThrow(UnsupportedOperationException.class)
            .withMessage("Only supported on tables with primary keys");
    }

}