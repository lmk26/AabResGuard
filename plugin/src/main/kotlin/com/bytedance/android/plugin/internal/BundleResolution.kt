package com.bytedance.android.plugin.internal

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationVariant
import org.gradle.api.Project
import java.nio.file.Path

/**
 * Created by YangJing on 2020/01/07 .
 * Email: yangjing.yeoh@bytedance.com
 */
internal fun getBundleFilePath(project: Project, variant: ApplicationVariant): Path {
    return variant.artifacts.get(SingleArtifact.BUNDLE).get().asFile.toPath()
}
