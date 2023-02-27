package com.tg.plugin;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("++++++++++开始运行+++++++++++++");
        BaseExtension ext = project.getExtensions().findByType(BaseExtension.class);
        if (ext != null) {
            ext.registerTransform(new MyTransform(project));
        }
    }
}
