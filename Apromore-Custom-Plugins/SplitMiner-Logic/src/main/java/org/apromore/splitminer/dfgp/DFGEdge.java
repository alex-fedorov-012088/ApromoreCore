/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016, 2017 Adriano Augusto.
 * Copyright (C) 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.splitminer.dfgp;

import org.apromore.splitminer.log.graph.LogEdge;

/**
 * Created by Adriano on 24/10/2016.
 */
public class DFGEdge extends LogEdge {

    private int frequency;

    public DFGEdge(DFGNode source, DFGNode target){
        super(source, target);
        frequency = 0;
    }

    public DFGEdge(DFGNode source, DFGNode target, String label){
        super(source, target, label);
        frequency = 0;
    }

    public DFGEdge(DFGNode source, DFGNode target, int frequency){
        super(source, target);
        this.frequency = frequency;
    }

    public DFGEdge(DFGNode source, DFGNode target, String label, int frequency){
        super(source, target, label);
        this.frequency = frequency;
    }

    public void increaseFrequency() { frequency++; }
    public void increaseFrequency(int amount) { frequency += amount; }

    public int getFrequency(){ return frequency; }

    @Override
    public String toString() { return Integer.toString(frequency); }

    public String print() { return getSourceCode() + " > " + getTargetCode() + " [" + getFrequency() + "]"; }

    @Override
    public int compareTo(Object o) {
        if( (o instanceof DFGEdge) ) {
            if( frequency == (((DFGEdge) o).frequency) ) {
                if( getSourceCode() == ((DFGEdge) o).getSourceCode() ) return getTargetCode() - ((DFGEdge) o).getTargetCode();
                else return getSourceCode() - ((DFGEdge) o).getSourceCode();
            } else return frequency - ((DFGEdge) o).frequency;
        } else return -1;
    }
}
