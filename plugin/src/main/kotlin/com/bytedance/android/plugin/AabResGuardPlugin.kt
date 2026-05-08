package com.bytedance.android.plugin

// 替换为新的 API 导入
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.bytedance.android.plugin.extensions.AabResGuardExtension
import com.bytedance.android.plugin.tasks.AabResGuardTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class AabResGuardPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkApplicationPlugin(project)
        project.extensions.create("aabResGuard", AabResGuardExtension::class.java)

        // 使用新的 androidComponents 扩展
        val androidComponents = project.extensions.findByType(ApplicationAndroidComponentsExtension::class.java)
        androidComponents?.onVariants { variant ->
            createAabResGuardTask(project, variant)
        }
    }

    private fun createAabResGuardTask(project: Project, variant: ApplicationVariant) {
        val variantName = variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val aabResGuardTaskName = "aabresguard$variantName"
        val bundleTaskName = "bundle$variantName"

        // 使用 register 替代 create
        val aabResGuardTaskProvider = project.tasks.register(aabResGuardTaskName, AabResGuardTask::class.java) { task ->
            // 这里的配置代码只有在任务执行时才会运行
            task.setVariantScope(variant)

            // 设置任务依赖（在配置闭包内设置）
            val bundlePackageTaskName = "package${variantName}Bundle"
            task.dependsOn(bundlePackageTaskName)

            val finalizeBundleTaskName = "sign${variantName}Bundle"
            if (project.tasks.findByName(finalizeBundleTaskName) != null) {
                task.dependsOn(finalizeBundleTaskName)
            }
        }

        // 让 bundleTask 依赖于我们的 aabResGuardTask
        project.tasks.configureEach { task ->
            if (task.name == bundleTaskName) {
                task.dependsOn(aabResGuardTaskProvider)
            }
        }
    }

    private fun checkApplicationPlugin(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw GradleException("Android Application plugin required")
        }
    }
}