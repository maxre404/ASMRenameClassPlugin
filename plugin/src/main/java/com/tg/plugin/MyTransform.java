package com.tg.plugin;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class MyTransform extends Transform {
    private Project mProject;
    public MyTransform(Project project){
        this.mProject = project;
    }

    @Override
    public String getName() {
        // 最终执行时的任务名称为transformClassesWithMyTestFor[XXX] (XXX为Debug或Release)
        return "MyTest";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        System.out.println("Hello MyTransform..123321.");
        transformInvocation.getInputs().forEach(transformInput -> {
            transformInput.getDirectoryInputs().forEach(directoryInput -> {
                String path = directoryInput.getFile().getAbsolutePath();
                System.out.println("查看路径:[InjectTransform] Begin to inject:"+path);
                InjectByJavassit.inject(path, mProject);
                // 获取输出目录
                File dest = transformInvocation.getOutputProvider().getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                System.out.println("[InjectTransform] Directory output dest: "+dest.getAbsolutePath());
                try {
                    FileUtils.copyDirectory(directoryInput.getFile(), dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            transformInput.getJarInputs().forEach(jarInput -> {
                File dest = transformInvocation.getOutputProvider().getContentLocation(jarInput.getName(),
                    jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                try {
                    FileUtils.copyFile(jarInput.getFile(), dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
