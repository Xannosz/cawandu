package com.itelg.docker.cawandu.composer.network;

import com.itelg.docker.cawandu.composer.PopupComposer;
import com.itelg.docker.cawandu.composer.zk.WireArg;
import com.itelg.docker.cawandu.domain.network.Network;
import com.itelg.docker.cawandu.service.ContainerService;
import com.itelg.docker.cawandu.service.NetworkService;
import com.itelg.zkoss.helper.combobox.ComboboxHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class NetworkDisconnectComposer extends PopupComposer {
    private @Autowired
    NetworkService networkService;

    private @Autowired
    ContainerService containerService;

    private @Wire
    Combobox containerIdComboBox;
    private @WireArg("network")
    Network network;

    @Override
    protected void afterCompose() {
        ComboboxHelper.setDefaultItem(containerIdComboBox, "Not selected.");
        ComboboxHelper.init(containerIdComboBox, containerService.getAllContainers()
                        .stream().filter(c -> network.getContainers().containsKey(c.getId()))
                        .collect(Collectors.toList())
                , null, (item, container, index) ->
                {
                    item.setValue(container.getId());
                    item.setLabel(container.getName());
                });
        containerIdComboBox.setSelectedIndex(0);
        setTitle("Disconnect: " + network.getName());
    }

    @Listen("onClick = #disconnectButton")
    public void onDisconnect() {
        Clients.clearWrongValue(containerIdComboBox);
        if (Objects.isNull(containerIdComboBox.getSelectedItem()) || StringUtils.isBlank(containerIdComboBox.getSelectedItem().getValue())) {
            throw new WrongValueException(containerIdComboBox, "Select a container!");
        }
        String containerId = containerIdComboBox.getSelectedItem().getValue();

        networkService.disconnectFromNetwork(network.getId(), containerId);

        close(network);
    }

    public static org.zkoss.zk.ui.Component show(Network network) {
        return show("/network/disconnect.zul", Collections.singletonMap("network", network));
    }
}
