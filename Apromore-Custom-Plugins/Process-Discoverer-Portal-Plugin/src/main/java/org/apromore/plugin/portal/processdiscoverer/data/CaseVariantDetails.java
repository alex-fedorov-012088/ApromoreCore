package org.apromore.plugin.portal.processdiscoverer.data;

import lombok.Getter;

import java.text.DecimalFormat;
import org.apromore.commons.datetime.DurationUtils;

@Getter
public class CaseVariantDetails {

    private final int caseVariantId;
    private final long activityInstances;
    private final double avgDuration;
    private final String avgDurationStr;
    private final long numCases;
    private final double freq;
    private final String freqStr;

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");

    private CaseVariantDetails(final int caseVariantId, final long occurrences, final long numCases,
                               final double duration, final double frequency) {
        this.caseVariantId = caseVariantId;
        this.activityInstances = occurrences;
        this.numCases = numCases;
        this.avgDuration = duration;
        this.avgDurationStr = DurationUtils.humanize(duration, true);
        this.freq = frequency;
        this.freqStr = decimalFormat.format(100 * freq) + "%";
    }

    public static CaseVariantDetails valueOf(final int caseVariantId, final long occurrences, final long numCases,
                                             final double duration, final double frequency) {
        return new CaseVariantDetails(caseVariantId, occurrences, numCases, duration, frequency);
    }
}