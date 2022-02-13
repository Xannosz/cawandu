package com.itelg.docker.cawandu.composer.dashboard;

import com.itelg.docker.cawandu.composer.AbstractComposer;
import com.itelg.docker.cawandu.composer.container.ContainerListComposer;
import com.itelg.docker.cawandu.domain.container.ContainerFilter;
import com.itelg.docker.cawandu.domain.container.ContainerState;
import com.itelg.docker.cawandu.service.ContainerService;
import com.itelg.zkoss.helper.i18n.Labels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Map.Entry;

@Component
@Scope("request")
public class DashboardContainerStateStatisticComposer extends AbstractComposer<Window> {
    private static final long serialVersionUID = -4998118686922227753L;

    @Autowired
    private transient ContainerService containerService;

    @Wire
    private Listbox containerStateStatisticListbox;

    @Wire
    private Listfooter overallContainerCountListfooter;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        int overallCount = 0;

        for (Entry<ContainerState, Integer> containerStateCount : containerService.getContainerStateStats().entrySet()) {
            Listitem stateListitem = new Listitem();
            stateListitem.appendChild(new Listcell(Labels.getLabel(containerStateCount.getKey())));
            stateListitem.appendChild(new Listcell(String.valueOf(containerStateCount.getValue())));
            stateListitem.addEventListener(Events.ON_CLICK, event ->
            {
                ContainerFilter filter = new ContainerFilter();
                filter.setState(containerStateCount.getKey());
                ContainerListComposer.show(filter);
            });
            containerStateStatisticListbox.appendChild(stateListitem);
            overallCount += containerStateCount.getValue();
        }

        overallContainerCountListfooter.setLabel(String.valueOf(overallCount));
    }
}