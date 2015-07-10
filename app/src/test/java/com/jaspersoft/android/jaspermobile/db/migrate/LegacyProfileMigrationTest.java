/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.db.migrate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jaspersoft.android.jaspermobile.BuildConfig;
import com.jaspersoft.android.jaspermobile.test.support.AccountUtil;
import com.jaspersoft.android.jaspermobile.test.support.TestResource;
import com.jaspersoft.android.jaspermobile.test.support.db.PermanentDatabase;
import com.jaspersoft.android.jaspermobile.test.support.db.ResourceDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author Tom Koptel
 * @since 2.1
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = 21
)
public class LegacyProfileMigrationTest {
    private static final String DB_NAME = "jasper_mobile_db_1.9";

    private ResourceDatabase resourceDatabase;
    private String insertMobileProfileSql;

    @Before
    public void setup() {
        // Dirty hack in order to revert AccountSeed side effect
        AccountUtil.get(RuntimeEnvironment.application).removeAllAccounts();
        resourceDatabase = PermanentDatabase.create(DB_NAME).prepare();

        insertMobileProfileSql = TestResource.get("insert_mobile_profile.sql").asString();
    }

    @After
    public void teardown() {
        resourceDatabase.delete();
    }

    @Test
    public void testFavoritesTableMigration() throws Exception {
        resourceDatabase.performSql(insertMobileProfileSql);
        resourceDatabase.performAction(new MigrateLegacyProfile());

        resourceDatabase.performAction(new ResourceDatabase.DbAction() {
            @Override
            public void performAction(SQLiteDatabase database) {
                assertFavoritesMigration(database);
            }
        });
    }

    private void assertFavoritesMigration(SQLiteDatabase database) {
        Cursor cursor = database.query("server_profiles",
                new String[]{"_id", "alias", "server_url"},
                null, null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));

        assertThat(cursor.getString(cursor.getColumnIndex("alias")), is("Legacy Mobile Demo"));
        assertThat(cursor.getString(cursor.getColumnIndex("server_url")), is("http://mobiledemo2.jaspersoft.com/jasperserver-pro"));

        cursor.close();
    }

    private static class MigrateLegacyProfile implements ResourceDatabase.DbAction {
        @Override
        public void performAction(SQLiteDatabase database) {
            new LegacyProfileMigration().migrate(database);
        }
    }

}
