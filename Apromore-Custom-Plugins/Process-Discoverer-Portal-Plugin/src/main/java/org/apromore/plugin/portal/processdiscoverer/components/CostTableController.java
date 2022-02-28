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

package org.apromore.plugin.portal.processdiscoverer.components;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.AttributeCost;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class CostTableController extends DataListController {
    private Window costTableWindow;
    private Combobox currencyCombobox;
    private boolean disabled;

    public CostTableController(PDController controller) {
        super(controller);
    }

    private void generateData() {
        Map<String, Double> currentMapper = parent.getProcessAnalyst().getCostTable();
        records = new ListModelList<AttributeCost>();
        rows = new ArrayList<>();
        for (Object role : parent.getProcessAnalyst().getRoleValues()) {
            String attrVal = (String) role;
            Double costValue = currentMapper.containsKey(attrVal) ? currentMapper.get(attrVal) : 1.0;
            AttributeCost ac = AttributeCost.valueOf(attrVal, costValue);
            records.add(ac);
            rows.add(new String[]{ac.getAttributeId(), String.format("%6.2f", ac.getCostValue())});
        }
    }

    private void initializeData(Listbox listbox) {
        generateData();
        listbox.setModel(records);
        listbox.addEventListener("onUpdateCostValue", (ForwardEvent event) -> {
            InputEvent inputEvent = (InputEvent) event.getOrigin();
            Doublebox inputBox = (Doublebox) inputEvent.getTarget();
            Double costValue = inputBox.getValue();
            Listitem listItem = (Listitem) inputBox.getParent().getParent();
            AttributeCost ac = listItem.getValue();
            ac.setCostValue(costValue);
        });
        currencyCombobox.setValue(parent.getProcessAnalyst().getCurrency());
    }

    @Override
    public String[] getDataHeaders() {
        return new String[]{"Attribute", "Cost Value"};
    }

    private void persistCostTable() {
        String currency = currencyCombobox.getValue();
        String costTable = JSONObject.toJSONString(this.getCostMapper()).replaceAll("\\r?\\n", "");
        String jsonString = "{ \"currency\": \"" + currency + "\", \"costTable\": " + costTable + " }";
        Clients.evalJavaScript("Ap.common.setLocalStorageItem('ap.cost.table', '" + jsonString + "')");
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (costTableWindow == null) {
            Map<String, Object> arg = new HashMap<>();
            arg.put("pdLabels", parent.getLabels());
            costTableWindow = (Window) Executions
                    .createComponents(getPageDefinition("processdiscoverer/zul/costTable.zul"), null, arg);

            costTableWindow.addEventListener("onClose", e -> costTableWindow = null);

            costTableWindow.addEventListener("onChangeCurrency", e -> {
                parent.getProcessAnalyst().setCurrency(currencyCombobox.getValue());
                persistCostTable();
            });

            costTableWindow.addEventListener("onApplyCost", e -> {
                parent.getProcessAnalyst().setCostTable(this.getCostMapper());
                parent.getProcessAnalyst().setCurrency(currencyCombobox.getValue());
                persistCostTable();
                costTableWindow.detach();
                costTableWindow = null;
                Messagebox.show("You need to reload the page",
                    new Messagebox.Button[] {Messagebox.Button.OK, Messagebox.Button.CANCEL},
                    (ev) -> {
                        if (Messagebox.ON_OK.equals(ev.getName())) {
                            Clients.evalJavaScript("window.location.reload()");
                        }
                    }
                );
            });

            currencyCombobox = (Combobox) costTableWindow.getFellow("costTableCurrency");
            Listbox listbox = (Listbox) costTableWindow.getFellow("costTableListbox");
            initializeData(listbox);

            try {
                // @todo Incorrect coordinate returned by ZK 9
                // org.zkoss.json.JSONObject param = (org.zkoss.json.JSONObject) event.getData();
                costTableWindow.setPosition("nocenter");
                costTableWindow.setLeft("10px");
                costTableWindow.setTop("76px");
            } catch (Exception e) {
                // ignore the exception and proceed with default centered window
            }
            costTableWindow.doOverlapped();
        }
    }

    public Map<String, Double> getCostMapper() {
        Map<String, Double> attributeCostMapper = new HashMap<>();
        for (Object rec: records) {
            AttributeCost ac = (AttributeCost) rec;
            attributeCostMapper.put(ac.getAttributeId(), ac.getCostValue());
        }
        return attributeCostMapper;
    }

    public Window getWindow() {
        return costTableWindow;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    private PageDefinition getPageDefinition(String uri) throws IOException {
        String url = "static/" + uri;
        Execution current = Executions.getCurrent();
        return current.getPageDefinitionDirectly(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(url)), "zul");
    }

}