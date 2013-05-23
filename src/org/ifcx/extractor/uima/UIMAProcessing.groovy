package org.ifcx.extractor.uima

import org.apache.uima.collection.CollectionReader

import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader
import de.tudarmstadt.ukp.dkpro.core.io.xml.XmlReaderXPath
import org.apache.uima.analysis_engine.AnalysisEngineDescription

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription
import org.uimafit.component.xwriter.CASDumpWriter

import static org.uimafit.pipeline.SimplePipeline.runPipeline
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive
import org.uimafit.factory.ExternalResourceFactory
import de.tudarmstadt.ukp.dkpro.core.frequency.resources.Web1TFrequencyCountResource
import de.tudarmstadt.ukp.dkpro.core.io.web1t.Web1TFormatWriter
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token
import com.googlecode.jweb1t.JWeb1TIndexer
import com.googlecode.jweb1t.Searcher
import com.googlecode.jweb1t.JWeb1TSearcherInMemory

def ngramsIndexDir = "tmp/ngrams"

reader = createCollectionReader(
        XmlReaderXPath.class,
        XmlReaderXPath.PARAM_PATH, "tmp",
        XmlReaderXPath.PARAM_PATTERNS, ["[+]doc-output.html"] as String[],
        XmlReaderXPath.PARAM_XPATH_EXPRESSION, "/html/body//div[@class='docComment']"
//        XmlReaderXPath.PARAM_LANGUAGE, "en"
)

segmenter = createPrimitive(BreakIteratorSegmenter.class,
        SegmenterBase.PARAM_CREATE_TOKENS, true,
        SegmenterBase.PARAM_CREATE_SENTENCES, true,
        BreakIteratorSegmenter.PARAM_SPLIT_AT_APOSTROPHE, false);

ngramWriter = createPrimitive(
        Web1TFormatWriter.class,
        Web1TFormatWriter.PARAM_TARGET_LOCATION, ngramsIndexDir,
        Web1TFormatWriter.PARAM_INPUT_TYPES, [Token.class.getName()] as String[],
        Web1TFormatWriter.PARAM_MIN_NGRAM_LENGTH, 1,
        Web1TFormatWriter.PARAM_MAX_NGRAM_LENGTH, 3,
        Web1TFormatWriter.PARAM_MIN_FREQUENCY, 2
)

// Should find one file
writer = createPrimitive(
        CASDumpWriter.class,
        CASDumpWriter.PARAM_OUTPUT_FILE, "tmp/doc-output.txt"
)

runPipeline(reader, segmenter, ngramWriter, writer)

// create the necessary indexes
JWeb1TIndexer indexCreator = new JWeb1TIndexer(ngramsIndexDir, 3)
indexCreator.create()

//searcher = new JWeb1TSearcherInMemory(ngramsIndexDir, 3)
