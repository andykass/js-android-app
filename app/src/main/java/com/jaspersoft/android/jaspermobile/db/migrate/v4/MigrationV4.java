/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.db.migrate.v4;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Base64;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.db.migrate.Migration;

import java.io.UnsupportedEncodingException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @author Tom Koptel
 * @since 2.1.2
 */
public final class MigrationV4 implements Migration {
    private final Context mContext;
    private final AccountManager accountManager;

    private static int ITERATION_COUNT = 1000;
    private  static final String UTF8 = "utf-8";
    private final char[] mSecret;


    public MigrationV4(Context context) {
        mContext = context;
        accountManager = AccountManager.get(context);

        String salt = mContext.getResources().getString(R.string.password_salt_key);
        mSecret = salt.toCharArray();
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        Account[] accounts = accountManager.getAccountsByType("com.jaspersoft");
        for (Account account : accounts) {
            encryptAccountPassword(account);
        }
    }

    private void encryptAccountPassword(Account account) {
        String oldPassword = accountManager.getPassword(account);
        String encryptedPass = encrypt(oldPassword);
        accountManager.setPassword(account, encryptedPass);
    }

    private String encrypt(String value) {
        try {
            final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            KeySpec keySpec = new PBEKeySpec(mSecret);
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            AlgorithmParameterSpec spec = new PBEParameterSpec(fetchSalt(), ITERATION_COUNT);

            pbeCipher.init(Cipher.ENCRYPT_MODE, key, spec);

            return toBase64(pbeCipher.doFinal(bytes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String toBase64(byte[] bytes) throws UnsupportedEncodingException {
        return new String(Base64.encode(bytes, Base64.NO_WRAP), UTF8);
    }

    private byte[] fetchSalt() throws UnsupportedEncodingException {
        String id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return Arrays.copyOf(id.getBytes(UTF8), 8);
    }
}
