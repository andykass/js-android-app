package com.jaspersoft.android.jaspermobile.util.resource;

import java.io.Serializable;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
public abstract class JasperResource implements Serializable, Cloneable {
    private String id;
    private String label;
    private String description;

    public JasperResource(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public abstract JasperResourceType getResourceType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JasperResource that = (JasperResource) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
