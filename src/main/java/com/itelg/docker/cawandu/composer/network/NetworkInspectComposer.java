package com.itelg.docker.cawandu.composer.network;

import com.itelg.docker.cawandu.composer.PopupComposer;
import com.itelg.docker.cawandu.composer.zk.WireArg;
import com.itelg.docker.cawandu.domain.network.IpamConfig;
import com.itelg.docker.cawandu.domain.network.Network;
import com.itelg.docker.cawandu.service.ContainerService;
import com.itelg.zkoss.helper.listbox.ListboxHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Collections;
import java.util.Map;

@Component
@Scope("request")
public class NetworkInspectComposer extends PopupComposer {

    @Autowired
    private ContainerService containerService;

    private @Wire
    Label idLabel;
    private @Wire
    Label nameLabel;
    private @Wire
    Label scopeLabel;
    private @Wire
    Label driverLabel;

    private @Wire
    Label ipamDriverLabel;
    private @Wire
    Listbox ipamConfigListBox;

    private @Wire
    Listbox containersListBox;
    private @Wire
    Listbox optionsListBox;

    private @WireArg("network")
    Network network;

    @Override
    protected void afterCompose() {
        idLabel.setValue(network.getId());
        nameLabel.setValue(network.getName());
        scopeLabel.setValue(network.getScope());
        driverLabel.setValue(network.getDriver());

        ipamDriverLabel.setValue(network.getIpam().getDriver());
        ipamConfigListBox.setItemRenderer(new IpamListItemRenderer());
        ipamConfigListBox.getPaginal().setAutohide(false);
        ipamConfigListBox.setModel(new ListModelList<>(network.getIpam().getConfig()));
        ListboxHelper.hideIfEmpty(ipamConfigListBox, "No IPAM config found. ");

        containersListBox.setItemRenderer(new ContainerListItemRenderer());
        containersListBox.getPaginal().setAutohide(false);
        containersListBox.setModel(new ListModelList<>(network.getContainers().entrySet()));
        ListboxHelper.hideIfEmpty(containersListBox, "No containers found. ");

        optionsListBox.setItemRenderer(new OptionsListItemRenderer());
        optionsListBox.getPaginal().setAutohide(false);
        optionsListBox.setModel(new ListModelList<>(network.getOptions().entrySet()));
        ListboxHelper.hideIfEmpty(optionsListBox, "No options found. ");

        setTitle("Inspect: " + network.getName());
    }

    private static class IpamListItemRenderer implements ListitemRenderer<IpamConfig> {
        @Override
        public void render(Listitem item, IpamConfig ipamConfig, int index) {
            new Listcell(ipamConfig.getSubnet()).setParent(item);
            new Listcell(ipamConfig.getIpRange()).setParent(item);
            new Listcell(ipamConfig.getGateway()).setParent(item);
        }
    }

    private class ContainerListItemRenderer implements ListitemRenderer<Map.Entry<String, Network.Container>> {
        @Override
        public void render(Listitem item, Map.Entry<String, Network.Container> ipamConfig, int index) {
            new Listcell(containerService.getContainerById(ipamConfig.getKey()).getName()).setParent(item);
            new Listcell(ipamConfig.getValue().getEndpointId()).setParent(item);
            new Listcell(ipamConfig.getValue().getMacAddress()).setParent(item);
            new Listcell(ipamConfig.getValue().getIpv4Address()).setParent(item);
            new Listcell(ipamConfig.getValue().getIpv6Address()).setParent(item);
        }
    }

    private static class OptionsListItemRenderer implements ListitemRenderer<Map.Entry<String, String>> {
        @Override
        public void render(Listitem item, Map.Entry<String, String> ipamConfig, int index) {
            new Listcell(ipamConfig.getKey()).setParent(item);
            new Listcell(ipamConfig.getValue()).setParent(item);
        }
    }

    public static org.zkoss.zk.ui.Component show(Network network) {
        return show("/network/inspect.zul", Collections.singletonMap("network", network));
    }
}
