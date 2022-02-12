package com.itelg.docker.cawandu.composer.volume;

import com.itelg.docker.cawandu.composer.PopupComposer;
import com.itelg.docker.cawandu.composer.zk.WireArg;
import com.itelg.docker.cawandu.domain.volume.Volume;
import com.itelg.zkoss.helper.listbox.ListboxHelper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Collections;
import java.util.Map;

@Component
@Scope("request")
public class VolumeInspectComposer extends PopupComposer {
    private @Wire
    Label nameLabel;
    private @Wire
    Label driverLabel;
    private @Wire
    Label mountPointLabel;
    private @Wire
    Label scopeLabel;

    private @Wire
    Listbox driverOptsListBox;
    private @Wire
    Listbox labelsListBox;
    private @Wire
    Listbox statusListBox;

    private @WireArg("volume")
    Volume volume;

    @Override
    protected void afterCompose() {
        nameLabel.setValue(volume.getName());
        driverLabel.setValue(volume.getDriver());
        mountPointLabel.setValue(volume.getMountPoint());
        scopeLabel.setValue(volume.getScope());

        driverOptsListBox.setItemRenderer(new MapListItemRenderer());
        driverOptsListBox.getPaginal().setAutohide(false);
        driverOptsListBox.setModel(new ListModelList<>(volume.getDriverOpts().entrySet()));
        ListboxHelper.hideIfEmpty(driverOptsListBox, "No driver opts found. ");

        labelsListBox.setItemRenderer(new MapListItemRenderer());
        labelsListBox.getPaginal().setAutohide(false);
        labelsListBox.setModel(new ListModelList<>(volume.getLabels().entrySet()));
        ListboxHelper.hideIfEmpty(labelsListBox, "No labels found. ");

        statusListBox.setItemRenderer(new MapListItemRenderer());
        statusListBox.getPaginal().setAutohide(false);
        statusListBox.setModel(new ListModelList<>(volume.getStatus().entrySet()));
        ListboxHelper.hideIfEmpty(statusListBox, "No status found. ");

        setTitle("Inspect: " + volume.getName());
    }

    private static class MapListItemRenderer implements ListitemRenderer<Map.Entry<String, String>> {
        @Override
        public void render(Listitem item, Map.Entry<String, String> entry, int index) {
            new Listcell(entry.getKey()).setParent(item);
            new Listcell(entry.getValue()).setParent(item);
        }
    }

    public static org.zkoss.zk.ui.Component show(Volume volume) {
        return show("/volume/inspect.zul", Collections.singletonMap("volume", volume));
    }
}
