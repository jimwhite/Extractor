//  Copyright (c) 2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This file is part of "fa".
//  
//  "fa" is free software: you can redistribute it and/or modify it under the
//  terms of the GNU Lesser General Public License as published by the Free
//  Software Foundation, either version 3 of the License, or (at your option)
//  any later version.
//  
//  "fa" is distributed in the hope that it will be useful, but WITHOUT ANY
//  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  
//  You should have received a copy of the GNU Lesser General Public License
//  along with "fa". If not, see <http://www.gnu.org/licenses/>.
//  

package org.ifcx.extractor.util;

import com.sun.tools.javac.util.List;

import org.ifcx.extractor.RDFExtractor;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ProcRunner implements Runnable
{

    private Processor processor;

    private Iterable<File> getFiles()
    {
//        return Files.all("test", "*.java");
        return Files.all("jdksrc", "*Object.java");
    }

    public ProcRunner(Processor processor)
    {
        this.processor = processor;
    }


    public void run()
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileman = compiler.getStandardFileManager(null,
                null, null);
        try {
            RDFExtractor.rdfPath.get(0).mkdirs();
            fileman.setLocation(RDFExtractor.rdfLocation, RDFExtractor.rdfPath);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Iterable<? extends JavaFileObject> units = fileman
                .getJavaFileObjectsFromFiles(this.getFiles());

        CompilationTask task = compiler.getTask(null, // out
                fileman, // fileManager
                null, // diagnosticsListener
                null, // options
//                List.of("-printsource"), // options
                null, // classes
                units);

        task.setProcessors(List.of(processor));

        task.call();

    }

    public static void main(String[] args)
    {
        Runnable runner = new ProcRunner(new RDFExtractor());
        runner.run();
    }


}

