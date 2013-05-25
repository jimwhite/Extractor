package org.ifcx.extractor

import org.gradle.api.internal.tasks.compile.DefaultJavaCompileSpec

class ExtractJavadocSpec extends DefaultJavaCompileSpec {
    File htmlReport
    File methodAbstracts
}