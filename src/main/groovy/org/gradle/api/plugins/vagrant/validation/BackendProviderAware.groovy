package org.gradle.api.plugins.vagrant.validation

interface BackendProviderAware {
    void setProvider(String provider)
}