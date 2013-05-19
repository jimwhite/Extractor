package org.ifcx.extractor;

import com.sun.tools.javac.tree.JCTree;

import java.io.IOException;
import java.io.Writer;

public class PrettyNew extends com.sun.tools.javac.tree.Pretty
{
    public PrettyNew(Writer out, boolean sourceOutput) {
        super(out, sourceOutput);
    }

    public void visitIdent(JCTree.JCIdent tree) {
        super.visitIdent(tree);
        if (tree.sym == null) try {
            print("/*!*/");
        } catch (IOException e) {
        }
    }

}
