package org.ifcx.extractor

import org.gradle.api.internal.file.TemporaryFileProvider
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.compile.DefaultJavaCompileSpec
import org.gradle.api.internal.tasks.compile.DefaultJavaCompilerFactory
import org.gradle.api.internal.tasks.compile.DelegatingJavaCompiler
import org.gradle.api.internal.tasks.compile.InProcessJavaCompilerFactory
import org.gradle.api.internal.tasks.compile.IncrementalJavaCompiler
import org.gradle.api.internal.tasks.compile.JavaCompileSpec
import org.gradle.api.internal.tasks.compile.JavaCompilerFactory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.util.DeprecationLogger

class ExtractJavadoc extends org.gradle.api.tasks.compile.AbstractCompile
{
    private org.gradle.api.internal.tasks.compile.Compiler<JavaCompileSpec> javaCompiler;

    private final CompileOptions compileOptions = new CompileOptions();

    public ExtractJavadoc() {
//        ProjectInternal projectInternal = (ProjectInternal) getProject();
//        TemporaryFileProvider tempFileProvider = projectInternal.getServices().get(TemporaryFileProvider.class);
//        JavaCompilerFactory defaultCompilerFactory = new DefaultJavaCompilerFactory(projectInternal, tempFileProvider, antBuilderFactory, inProcessCompilerFactory);
        javaCompiler = new ExtractingCompiler()
    }

    /**
     * Returns the compilation options.
     *
     * @return The compilation options.
     */
    @Nested
    public CompileOptions getOptions() {
        return compileOptions;
    }


    @TaskAction
    protected void compile() {
        DefaultJavaCompileSpec spec = new DefaultJavaCompileSpec();
        spec.setSource(getSource());
        spec.setDestinationDir(getDestinationDir());
        spec.setClasspath(getClasspath());
//        spec.setDependencyCacheDir(getDependencyCacheDir());
        spec.setSourceCompatibility(getSourceCompatibility());
        spec.setTargetCompatibility(getTargetCompatibility());
        spec.setCompileOptions(compileOptions);
        WorkResult result = javaCompiler.execute(spec);
        setDidWork(result.getDidWork());
    }

}
