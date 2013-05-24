package org.ifcx.extractor.rdf;

import org.openrdf.rio.rdfa.RDFaMetaWriter;

import java.io.Writer;

public class RDFaWriter extends RDFaMetaWriter
{
    public RDFaWriter(Writer writer)
    {
        super(writer);
    }
}
