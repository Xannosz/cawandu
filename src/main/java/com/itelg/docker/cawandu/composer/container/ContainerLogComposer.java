package com.itelg.docker.cawandu.composer.container;

import com.itelg.docker.cawandu.composer.PopupComposer;
import com.itelg.docker.cawandu.composer.zk.WireArg;
import com.itelg.docker.cawandu.composer.zk.events.LogRefreshEvent;
import com.itelg.docker.cawandu.domain.container.Container;
import com.itelg.docker.cawandu.domain.container.ContainerLog;
import com.itelg.docker.cawandu.service.ContainerService;
import com.itelg.zkoss.helper.listbox.ListboxHelper;
import com.itelg.zkoss.helper.listbox.ListcellHelper;
import de.jaggl.utils.events.zk.annotations.Processing;
import hu.xannosz.microtools.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Collections;

@Component
@Scope("request")
public class ContainerLogComposer extends PopupComposer {
    private transient @Autowired
    ContainerService containerService;

    private @Wire
    Listbox logListBox;
    private @WireArg("container")
    Container container;

    @Override
    protected void afterCompose() {
        logListBox.setItemRenderer(new ListItemRenderer());
        logListBox.getPaginal().setAutohide(false);
        setTitle("Log: " + container.getName());
        refreshListBox();
    }

    @Processing(LogRefreshEvent.class)
    private void refreshListBox() {
        logListBox.setModel(new ListModelList<>(containerService.getContainerLog(container)));
        ListboxHelper.hideIfEmpty(logListBox, "No logs found");
    }

    private class ListItemRenderer implements ListitemRenderer<ContainerLog> {
        @Override
        public void render(Listitem item, ContainerLog log, int index) {
            Menupopup popup = new Menupopup();
            getSelf().appendChild(popup);
            item.setContext(popup);
            item.setPopup(popup);
            item.setValue(log);

            Menuitem downloadLogMenuItem = new Menuitem("Get logs in JSON");
            downloadLogMenuItem.setParent(popup);
            downloadLogMenuItem.setIconSclass("z-icon-download");
            downloadLogMenuItem.addEventListener(Events.ON_CLICK, event ->
                    Filedownload.save(Json.writeData(containerService.getContainerLog(container)), "json", container.getName() + ".log.json")
            );

            ListcellHelper.buildDateTimeListcell(log.getTime(), "N/A", "yyyy-MM-DD HH:mm:ss.SSS").setParent(item);
            new Listcell(log.getStream()).setParent(item);
            new Listcell(log.getLog()).setParent(item);
        }
    }

    public static org.zkoss.zk.ui.Component show(Container container) {
        return show("/container/log.zul", Collections.singletonMap("container", container));
    }
}
