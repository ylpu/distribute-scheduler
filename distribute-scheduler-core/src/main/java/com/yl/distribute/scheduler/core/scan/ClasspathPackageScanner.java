package com.yl.distribute.scheduler.core.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import com.yl.distribute.scheduler.common.utils.StringUtils;

public class ClasspathPackageScanner {
    
    private static List<String> classNames = new ArrayList<String>();    
    private static String basePackage;
    private static ClassLoader cl = ClasspathPackageScanner.class.getClassLoader();
    
    static {
        try {
            doScan("com.yl.distribute.scheduler.resource");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setPackage(String basePackage) {
        ClasspathPackageScanner.basePackage = basePackage;
    }
     
    /**
     *doScan函数
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
     private static List<String> doScan(String basePackage) throws IOException {
         classNames.clear();
         String splashPath = StringUtils.dotToSplash(basePackage);
         URL url = cl.getResource(splashPath);   //file:/D:/WorkSpace/java/ScanTest/target/classes/com/scan
         String filePath = StringUtils.getRootPath(url);
         List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
         if (isJarFile(filePath)) {// 先判断是否是jar包，如果是jar包，通过JarInputStream产生的JarEntity去递归查询所有类
             names = readFromJarFile(filePath, splashPath);
         } else {
             names = readFromDirectory(filePath);
         }
         for (String name : names) {
             if (isClassFile(name)) {
                 classNames.add(toFullyQualifiedName(name, basePackage));
             } else {
                 doScan(basePackage + "." + name);
             }
         }
         return classNames;
     }
     
     private static String toFullyQualifiedName(String shortName, String basePackage) {
         StringBuilder sb = new StringBuilder(basePackage);
         sb.append('.');
         sb.append(StringUtils.trimExtension(shortName));
         //打印出结果
         System.out.println(sb.toString());
         return sb.toString();
     }
     
     private static List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
         JarInputStream jarIn = null;
         List<String> nameList = new ArrayList<String>();
         try{
             jarIn = new JarInputStream(new FileInputStream(jarPath));
             JarEntry entry = jarIn.getNextJarEntry();             
             while (null != entry) {
                String name = entry.getName();
                if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                     nameList.add(name);
                }
        
                entry = jarIn.getNextJarEntry();
              }
         }finally {
             if(jarIn != null) {
                 jarIn.close();
             }
         }   
          return nameList;
     }
    
     private static List<String> readFromDirectory(String path) {
         File file = new File(path);
         String[] names = file.list();
    
         if (null == names) {
             return null;
         }
    
         return Arrays.asList(names);
     }
    
     private static boolean isClassFile(String name) {
         return name.endsWith(".class");
     }
    
     private static boolean isJarFile(String name) {
         return name.endsWith(".jar");
     }     
    
    public static List<String> getClassNames() {
        return classNames;
    }

    public static void setClassNames(List<String> classNames) {
        ClasspathPackageScanner.classNames = classNames;
    }

    /**
      * For test purpose.
      */
     public static void main(String[] args) throws Exception {
         Class<?> cls = Class.forName("com.yl.distribute.scheduler.core.service.ResourceService");        
         List<String> classes = ClasspathPackageScanner.getClassNames();
         for(String classname : classes) {
             Class<?> cls1 = Class.forName(classname);
             if(cls.isAssignableFrom(cls1)) {
                 System.out.println("service is " + cls1.getName());
             }
         }
     }
}