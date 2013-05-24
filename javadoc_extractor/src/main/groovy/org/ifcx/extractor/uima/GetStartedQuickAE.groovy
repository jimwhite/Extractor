package org.ifcx.extractor.uima

import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.jcas.JCas
import org.uimafit.descriptor.ConfigurationParameter
import org.uimafit.component.JCasAnnotator_ImplBase

/**
 * @author Philip Ogren
 */
public class GetStartedQuickAE extends JCasAnnotator_ImplBase {

    public static final String PARAM_STRING = "stringParam";
    @ConfigurationParameter(name = GetStartedQuickAE.PARAM_STRING)
    private String stringParam;

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        System.out.println("Hello world!  Say 'hi' to " + stringParam);
    }
}
