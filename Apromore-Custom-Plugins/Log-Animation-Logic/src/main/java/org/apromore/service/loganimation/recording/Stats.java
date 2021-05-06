/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.service.loganimation.recording;

import java.util.Arrays;

import org.json.JSONArray;

/**
 * <b>Stats</b> is used to contain or generate statistics for an animation movie {@link Movie}.
 * 
 * @author Bruce Nguyen
 *
 */
public class Stats {
    public static JSONArray computeCaseCountsJSON(Movie movie) {
        JSONArray json = new JSONArray();
        for (Frame frame : movie) {
            int totalCaseCount = Arrays.stream(frame.getLogIndexes()).map(i -> frame.getCaseIndexes(i).length).sum();
            json.put(totalCaseCount);
        }
        return json;
    }
}