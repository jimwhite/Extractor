package org.ifcx.extractor

import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.compile.CompileOptions

class ExtractJavadoc extends AbstractCompile
{
    private org.gradle.api.internal.tasks.compile.Compiler<ExtractJavadocSpec> javaCompiler;

    private final CompileOptions compileOptions = new CompileOptions();

    public ExtractJavadoc() {
//        ProjectInternal projectInternal = (ProjectInternal) getProject();
//        TemporaryFileProvider tempFileProvider = projectInternal.getServices().get(TemporaryFileProvider.class);
//        JavaCompilerFactory defaultCompilerFactory = new DefaultJavaCompilerFactory(projectInternal, tempFileProvider, antBuilderFactory, inProcessCompilerFactory);
        javaCompiler = new ExtractingCompiler()
    }

    @OutputFile
    File htmlReport

    @OutputDirectory
    File methodAbstracts

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
        def spec = new ExtractJavadocSpec();
        spec.setSource(getSource());
        spec.setDestinationDir(getDestinationDir());
        spec.setClasspath(getClasspath());
//        spec.setDependencyCacheDir(getDependencyCacheDir());
        spec.setSourceCompatibility(getSourceCompatibility());
        spec.setTargetCompatibility(getTargetCompatibility());
        spec.setCompileOptions(compileOptions);

        spec.htmlReport = htmlReport
        spec.methodAbstracts = methodAbstracts

        WorkResult result = javaCompiler.execute(spec);

        setDidWork(result.getDidWork());
    }

}
