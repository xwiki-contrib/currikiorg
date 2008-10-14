package org.curriki.xwiki.plugin.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * A simple Lucene Analyzer that defaults to having no stop words
 * for use with the xwiki plugin which does not allow for arguments
 * to be passed to the analyzer.
 */
public class NoStopWordsAnalyzer extends StandardAnalyzer {
    public NoStopWordsAnalyzer() {
        super(new String[]{});
    }
}
