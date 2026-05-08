package com.bytedance.android.plugin.internal

import com.android.build.api.variant.ApplicationVariant
import com.bytedance.android.plugin.model.SigningConfig
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.io.File

/**
 * Created by YangJing on 2020/01/06 .
 * Email: yangjing.yeoh@bytedance.com
 */
internal fun getSigningConfig(project: Project, variant: ApplicationVariant): SigningConfig {
    return getSigningConfigByAppVariant(variant)
}

private fun getSigningConfigByAppVariant(variant: ApplicationVariant): SigningConfig {
    val sc = variant.signingConfig
    
    val storeFile = when (val res = getRawPropertyValue(sc, "getStoreFile")) {
        is RegularFile -> res.asFile
        is File -> res
        else -> null
    }
        
    val storePassword = getRawPropertyValue(sc, "getStorePassword") as? String
    val keyAlias = getRawPropertyValue(sc, "getKeyAlias") as? String
    val keyPassword = getRawPropertyValue(sc, "getKeyPassword") as? String

    return SigningConfig(
        storeFile,
        storePassword,
        keyAlias,
        keyPassword
    )
}

private fun getRawPropertyValue(obj: Any?, methodName: String): Any? {
    if (obj == null) return null
    return try {
        val method = obj::class.java.methods.find { it.name == methodName }
        val result = method?.invoke(obj)
        if (result is Provider<*>) {
            result.getOrNull()
        } else {
            result
        }
    } catch (e: Exception) {
        null
    }
}
