package com.itelg.docker.cawandu.composer.network;

import com.itelg.docker.cawandu.composer.TabComposer;
import com.itelg.docker.cawandu.composer.zk.WireArg;
import com.itelg.docker.cawandu.domain.network.Network;
import com.itelg.docker.cawandu.domain.network.NetworkFilter;
import com.itelg.docker.cawandu.service.NetworkService;
import com.itelg.zkoss.helper.listbox.ListboxHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Scope("request")
public class NetworkListComposer extends TabComposer {
    @Autowired
    private transient NetworkService networkService;

    @Wire
    private Listbox networkListBox;

    @Wire
    private Textbox idTextBox;

    @Wire
    private Textbox nameTextBox;

    @WireArg("filter")
    private NetworkFilter filter;

    @Override
    protected void afterCompose() {
        initFilter();
        initListBox();
        refreshListBox();
        setTitle(buildTitle());
    }

    private void initFilter() {
        idTextBox.setValue(filter.getId());
        nameTextBox.setValue(filter.getName());
    }

    private void initListBox() {
        networkListBox.setItemRenderer(new NetworkListItemRenderer());
        networkListBox.getPaginal().setAutohide(false);
    }

    private void refreshListBox() {
        networkListBox.setModel(new ListModelList<>(networkService.listNetworks(filter)));
        ListboxHelper.hideIfEmpty(networkListBox, "No networks found. ");
    }

    private String buildTitle() {
        String title = "Networks";
        List<String> filterProperties = new ArrayList<>();

        if (StringUtils.isNotBlank(filter.getName())) {
            filterProperties.add("Name: " + filter.getName());
        }

        if (StringUtils.isNotBlank(filter.getId())) {
            filterProperties.add("ID: " + filter.getId());
        }

        if (!filterProperties.isEmpty()) {
            title += " (" + StringUtils.join(filterProperties, ", ") + ")";
        }

        return title;
    }

    private class NetworkListItemRenderer implements ListitemRenderer<Network> {
        @Override
        public void render(Listitem item, Network network, int index) {
            Menupopup popup = new Menupopup();
            getSelf().appendChild(popup);
            item.setContext(popup);
            item.setPopup(popup);
            item.setValue(network);

            Menuitem removeNetworkMenuItem = new Menuitem("Remove network");
            removeNetworkMenuItem.setParent(popup);
            removeNetworkMenuItem.setIconSclass("z-icon-times");
            removeNetworkMenuItem.addEventListener(Events.ON_CLICK, event ->
            {
                networkService.removeNetwork(network.getId());
                refreshListBox();
                showNotification("Network removed");
            });

            Menuitem connectNetworkMenuItem = new Menuitem("Connect network");
            connectNetworkMenuItem.setParent(popup);
            connectNetworkMenuItem.setIconSclass("z-icon-plus");
            connectNetworkMenuItem.addEventListener(Events.ON_CLICK, event ->
            {
                org.zkoss.zk.ui.Component composer = NetworkConnectComposer.show(networkService.inspectNetwork(network.getId()));
                composer.addEventListener(Events.ON_CLOSE, closeEvent ->
                {
                    if (closeEvent.getData() != null) {
                        refreshListBox();
                        showNotification("Connected");
                    }
                });
            });

            Menuitem disconnectNetworkMenuItem = new Menuitem("Disconnect network");
            disconnectNetworkMenuItem.setParent(popup);
            disconnectNetworkMenuItem.setIconSclass("z-icon-minus");
            disconnectNetworkMenuItem.addEventListener(Events.ON_CLICK, event ->
            {
                org.zkoss.zk.ui.Component composer = NetworkDisconnectComposer.show(networkService.inspectNetwork(network.getId()));
                composer.addEventListener(Events.ON_CLOSE, closeEvent ->
                {
                    if (closeEvent.getData() != null) {
                        refreshListBox();
                        showNotification("Disconnected");
                    }
                });
            });

            Menuitem inspectNetworkMenuItem = new Menuitem("Inspect network");
            inspectNetworkMenuItem.setParent(popup);
            inspectNetworkMenuItem.setIconSclass("z-icon-indent");
            inspectNetworkMenuItem.addEventListener(Events.ON_CLICK, event ->
            {
                org.zkoss.zk.ui.Component composer = NetworkInspectComposer.show(networkService.inspectNetwork(network.getId()));
                composer.addEventListener(Events.ON_CLOSE, closeEvent ->
                {
                    if (closeEvent.getData() != null) {
                        refreshListBox();
                    }
                });
            });

            new Listcell(network.getId()).setParent(item);
            new Listcell(network.getName()).setParent(item);
            new Listcell(network.getScope()).setParent(item);
            new Listcell(network.getDriver()).setParent(item);
        }
    }

    @Listen("onClick = #searchSubmitButton; onOK = #nameTextbox,#idTextbox")
    public void onExecuteFilter() {
        filter.setId(idTextBox.getValue().trim());
        filter.setName(nameTextBox.getValue().trim());

        refreshListBox();
        setTitle(buildTitle());
    }

    @Listen("onClick = #searchResetButton")
    public void onResetFilter() {
        idTextBox.setValue("");
        nameTextBox.setValue("");
        onExecuteFilter();
    }

    public static void show() {
        show(new NetworkFilter());
    }

    public static void show(NetworkFilter filter) {
        show("/network/list.zul", Collections.singletonMap("filter", filter));
    }
}
