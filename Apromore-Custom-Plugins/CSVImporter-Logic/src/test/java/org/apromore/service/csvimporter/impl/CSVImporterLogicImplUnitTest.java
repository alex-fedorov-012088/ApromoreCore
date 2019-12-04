package org.apromore.service.csvimporter.impl;

import com.google.common.io.ByteStreams;
import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apromore.service.csvimporter.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.zkoss.zul.ListModelList;

/** Test suite for {@link CSVImporterLogicImpl}. */
public class CSVImporterLogicImplUnitTest {

    /** Test instance. */
    private CSVImporterLogic csvImporterLogic = new CSVImporterLogicImpl();

    // Test cases

    /** Test {@link CSVImporterLogic.prepareXesModel} against a valid CSV log <code>test1-valid.csv</code>. */
    @Test
    public void testPrepareXesModel_test1_valid() throws Exception {

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test1-valid.csv", "utf-8");
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test1-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader);
        csvReader = newCSVReader("/test1-valid.csv", "utf-8");
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assert logModel.getInvalidRows().isEmpty();

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test2-missing-columns.csv", "utf-8");
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test2-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader);
        csvReader = newCSVReader("/test2-missing-columns.csv", "utf-8");
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(2, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assert logModel.getInvalidRows().isEmpty();

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }


    // Internal methods

    private static CSVReader newCSVReader(String filename, String charset) {
        return new CSVReaderBuilder(new InputStreamReader(CSVImporterLogicImplUnitTest.class.getResourceAsStream(filename), Charset.forName(charset)))
            .withSkipLines(0)
            .withCSVParser((new RFC4180ParserBuilder())
                               .withSeparator(',')
                               .build())
            .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
            .build();
    }

    private static String toString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }
}