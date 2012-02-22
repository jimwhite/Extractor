package org.ifcx.extractor.util;

import org.openrdf.rio.rdfa.RDFaMetaWriter;

import java.io.Writer;

public class RDFaWriter extends RDFaMetaWriter
{
    public RDFaWriter(Writer writer)
    {
        super(writer);
    }
}
