package com.itelg.docker.cawandu.composer.volume;

import com.itelg.docker.cawandu.composer.TabComposer;
import com.itelg.docker.cawandu.composer.zk.WireArg;
import com.itelg.docker.cawandu.domain.volume.Volume;
import com.itelg.docker.cawandu.domain.volume.VolumeFilter;
import com.itelg.docker.cawandu.service.VolumeService;
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
public class VolumeListComposer extends TabComposer {
    @Autowired
    private transient VolumeService volumeService;

    @Wire
    private Listbox volumeListBox;

    @Wire
    private Textbox nameTextBox;

    @WireArg("filter")
    private VolumeFilter filter;

    @Override
    protected void afterCompose() {
        initFilter();
        initListBox();
        refreshListBox();
        setTitle(buildTitle());
    }

    private void initFilter() {
        nameTextBox.setValue(filter.getName());
    }

    private void initListBox() {
        volumeListBox.setItemRenderer(new VolumeListComposer.VolumeListItemRenderer());
        volumeListBox.getPaginal().setAutohide(false);
    }

    private void refreshListBox() {
        volumeListBox.setModel(new ListModelList<>(volumeService.listVolumes(filter).getVolumes()));
        ListboxHelper.hideIfEmpty(volumeListBox, "No volumes found");
    }

    private String buildTitle() {
        String title = "Volumes";
        List<String> filterProperties = new ArrayList<>();

        if (StringUtils.isNotBlank(filter.getName())) {
            filterProperties.add("Name: " + filter.getName());
        }

        if (!filterProperties.isEmpty()) {
            title += " (" + StringUtils.join(filterProperties, ", ") + ")";
        }

        return title;
    }

    private class VolumeListItemRenderer implements ListitemRenderer<Volume> {
        @Override
        public void render(Listitem item, Volume volume, int index) {
            Menupopup popup = new Menupopup();
            getSelf().appendChild(popup);
            item.setContext(popup);
            item.setPopup(popup);
            item.setValue(volume);

            Menuitem removeVolumeMenuItem = new Menuitem("Remove volume");
            removeVolumeMenuItem.setParent(popup);
            removeVolumeMenuItem.setIconSclass("z-icon-times");
            removeVolumeMenuItem.addEventListener(Events.ON_CLICK, event ->
            {
                volumeService.removeVolume(volume.getName());
                refreshListBox();
                showNotification("Volume removed");
            });

            Menuitem inspectVolumeMenuItem = new Menuitem("Inspect volume");
            inspectVolumeMenuItem.setParent(popup);
            inspectVolumeMenuItem.setIconSclass("z-icon-indent");
            inspectVolumeMenuItem.addEventListener(Events.ON_CLICK, event ->
            {
                org.zkoss.zk.ui.Component composer = VolumeInspectComposer.show(volumeService.inspectVolume(volume.getName()));
                composer.addEventListener(Events.ON_CLOSE, closeEvent ->
                {
                    if (closeEvent.getData() != null) {
                        refreshListBox();
                    }
                });
            });

            new Listcell(volume.getName()).setParent(item);
            new Listcell(volume.getDriver()).setParent(item);
            new Listcell(volume.getMountPoint()).setParent(item);
            new Listcell(volume.getScope()).setParent(item);
        }
    }

    @Listen("onClick = #searchSubmitButton; onOK = #nameTextbox,#idTextbox")
    public void onExecuteFilter() {
        filter.setName(nameTextBox.getValue().trim());

        refreshListBox();
        setTitle(buildTitle());
    }

    @Listen("onClick = #searchResetButton")
    public void onResetFilter() {
        nameTextBox.setValue("");
        onExecuteFilter();
    }

    public static void show() {
        show(new VolumeFilter());
    }

    public static void show(VolumeFilter filter) {
        show("/volume/list.zul", Collections.singletonMap("filter", filter));
    }
}
