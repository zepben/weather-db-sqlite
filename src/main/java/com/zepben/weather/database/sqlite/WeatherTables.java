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
 * Definition of all the tables
 */
@EverythingIsNonnullByDefault
public class WeatherTables {

    public static final String DATABASE_VERSION = "2";

    public static class Version extends Table {

        public Version() {
            super("version");
        }

        public final Column VERSION = primaryCol("version", "TEXT");

    }

    public static class Stations extends Table {

        public Stations() {
            super("weather_stations");
        }

        public final Column ID = primaryCol("id", "TEXT");
        public final Column NAME = col("name", "TEXT");
        public final Column TIME_ZONE = col("time_zone", "TEXT");
        public final Column LATITUDE = nullableCol("latitude", "DOUBLE");
        public final Column LONGITUDE = nullableCol("longitude", "DOUBLE");
        public final Column ALTITUDE = nullableCol("altitude", "DOUBLE");

    }

    public static class Readings extends Table {

        public Readings() {
            super("weather_readings");
        }

        public final Column WEATHER_STATION = primaryCol("weather_station_id", "TEXT");
        public final Column TIMESTAMP = primaryCol("timestamp_utc", "TEXT");
        public final Column TEMPERATURE_DRY = nullableCol("temperature_dry_bulb", "NUMBER");
        public final Column TEMPERATURE_DEW = nullableCol("temperature_dew_point", "NUMBER");
        public final Column TEMPERATURE_FEELS = nullableCol("temperature_feels_like", "NUMBER");
        public final Column HUMIDITY = nullableCol("relative_humidity", "NUMBER");
        public final Column WIND_DIRECTION = nullableCol("wind_direction", "NUMBER");
        public final Column WIND_SPEED = nullableCol("wind_speed_mean", "NUMBER");
        public final Column WIND_GUST = nullableCol("wind_speed_gust", "NUMBER");
        public final Column PRESSURE = nullableCol("pressure", "NUMBER");
        public final Column RAINFALL_10MIN = nullableCol("rainfall_last_10min", "NUMBER");
        public final Column RAINFALL_9AM = nullableCol("rainfall_since_9am", "NUMBER");

    }

    public static class ProcessedFiles extends Table {

        public ProcessedFiles() {
            super("processed_files");
        }

        public final Column FILENAME = primaryCol("filename", "TEXT");

    }

}
