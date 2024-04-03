package cn.valjean.rpc.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Scan packages to return all specific classes.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/3/23 01:35
 */
@Slf4j
//doubt: learn
public class ScanPackagesUtils {

    //  issue: package utils for test package ?
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static List<Class<?>> scanPackages(String[] packages, Predicate<Class<?>> predicate) {
        List<Class<?>> results = new ArrayList<>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        for (String basePackage : packages) {
            if (StringUtils.isBlank(basePackage)) {
                continue;
            }
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/" + DEFAULT_RESOURCE_PATTERN;
            log.debug("packageSearchPath=" + packageSearchPath);
            try {
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    //log.debug(" resource: " + resource.getFilename());
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    String className = classMetadata.getClassName();
                    Class<?> clazz = Class.forName(className);
                    if (predicate.test(clazz)) {
                        //log.debug(" ===> class: " + className);
                        results.add(clazz);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static void main(String[] args) {
        String packages = "cn.valjean.rpc";

        log.debug(" 1. *********** ");
        log.debug(" => scan all classes for packages: " + packages);
        List<Class<?>> classes = scanPackages(packages.split(","), p -> true);
        classes.forEach(System.out::println);

        log.debug(" 2. *********** ");
        log.debug(" => scan all classes with @Configuration for packages: " + packages);
        List<Class<?>> classesWithConfig = scanPackages(packages.split(","),
                p -> Arrays.stream(p.getAnnotations())
                        .anyMatch(a -> a.annotationType().equals(Configuration.class)));
        classesWithConfig.forEach(System.out::println);
    }

}
