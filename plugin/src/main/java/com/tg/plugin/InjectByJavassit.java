package com.tg.plugin;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class InjectByJavassit{

    public static void inject(String path, Project project) {

        try {
            File dir = new File(path);
            checkFile(dir,project,path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkFile(File dir, Project project, String ordPath) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file:files){
                if (file.isDirectory()){
                    checkFile(file, project, ordPath);
                }else{
                    System.out.println("打印：：：：：：：：："+file.getName());
                    if (file.getName().endsWith("_.class")){
                        System.out.println("打印类名:"+file.getName());
                        doInject(project,file,ordPath);
                    }
                }
            }
//                dir.eachFileRecurse { File file ->
//                    if (file.name.endsWith('Activity.class')) {
//                        doInject(project, file, path)
//                    }
//                }
        }
    }
    private static void doInject(Project project, File clsFile, String originPath) {
        String cls = null;
        try {
            cls = relativePath(new File(originPath),clsFile).replace('/', '.');
        } catch (IOException e) {
            e.printStackTrace();
            printError(e);
        }
        cls = cls.substring(0, cls.lastIndexOf(".class"));
        System.out.println("注入的class:"+cls);
        ClassPool pool = ClassPool.getDefault();
        // 加入当前路径
        try {
            pool.appendClassPath(originPath);
//            CtClass user_ = pool.getCtClass(cls);
            CtClass user_ = pool.get(cls);
//            user_.replaceClassName("User_","User");
            String newClassName = cls.replace("_", "");
            user_.setName(newClassName);
            user_.writeFile(originPath);
            user_.detach();
            System.out.println("执行完毕"+newClassName);
        } catch (NotFoundException e) {
            e.printStackTrace();
            printError(e);
        } catch (CannotCompileException e) {
            e.printStackTrace();
            printError(e);
        } catch (IOException e) {
            e.printStackTrace();
            printError(e);
        }

//        CtClass ctClass = null;
//        try {
//            ctClass = pool.getCtClass(cls);
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        }
//        // 解冻
//        if (ctClass.isFrozen()) {
//            ctClass.defrost();
//        }
//        ctClass.writeFile(originPath);

        // 释放
//        ctClass.detach();
    }

    public static String relativePath(File self, File to) throws IOException {
        String fromPath = self.getCanonicalPath();
        String toPath = to.getCanonicalPath();
        String[] fromPathStack = getPathStack(fromPath);
        String[] toPathStack = getPathStack(toPath);
        if (0 < toPathStack.length && 0 < fromPathStack.length) {
            if (!fromPathStack[0].equals(toPathStack[0])) {
                return getPath(Arrays.asList(toPathStack));
            } else {
                int minLength = Math.min(fromPathStack.length, toPathStack.length);

                int same;
                for(same = 1; same < minLength && fromPathStack[same].equals(toPathStack[same]); ++same) {
                }

                List<String> relativePathStack = new ArrayList();

                for(int i = same; i < fromPathStack.length; ++i) {
                    relativePathStack.add("..");
                }

                relativePathStack.addAll(Arrays.asList(toPathStack).subList(same, toPathStack.length));
                return getPath(relativePathStack);
            }
        } else {
            return getPath(Arrays.asList(toPathStack));
        }
    }
    private static String[] getPathStack(String path) {
        String normalizedPath = path.replace(File.separatorChar, '/');
        return normalizedPath.split("/");
    }
    private static String getPath(List pathStack) {
        return getPath(pathStack, '/');
    }
    private static String getPath(List pathStack, char separatorChar) {
        StringBuilder buffer = new StringBuilder();
        Iterator iter = pathStack.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
        }

        while(iter.hasNext()) {
            buffer.append(separatorChar);
            buffer.append(iter.next());
        }

        return buffer.toString();
    }
    private static void printError(Exception error){
        System.out.println("异常:"+error.getMessage());
        error.printStackTrace();
    }

}
