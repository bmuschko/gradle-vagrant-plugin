package com.bmuschko.gradle.vagrant.validation

interface BackendProviderAware {
    void setProvider(String provider)
}