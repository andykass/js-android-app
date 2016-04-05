package com.jaspersoft.android.jaspermobile.domain.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jaspersoft.android.jaspermobile.util.resource.JasperResourceType;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import java.net.URI;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class JasperResource extends Resource {
    private final URI mUri;
    private final String mDescription;
    private final Type mType;

    public JasperResource(String label, URI uri, String description, Type mType) {
        super(label);

        this.mUri = uri;
        this.mDescription = description;
        this.mType = mType;
    }

    @Override
    public int getId() {
        return mUri.hashCode();
    }

    public URI getUri() {
        return mUri;
    }

    public String getDescription() {
        return mDescription;
    }

    public Type getType() {
        return mType;
    }

    public enum Type {
        report,
        dashboard,
        legacyDashboard,
        folder,
        file,
        undefined
    }
}
