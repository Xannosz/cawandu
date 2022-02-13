package com.itelg.docker.cawandu.composer.container;

import com.itelg.docker.cawandu.composer.TabComposer;
import com.itelg.docker.cawandu.service.ContainerService;
import com.itelg.docker.cawandu.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;

@Component
@Scope("request")
public class ContainerCreateComposer extends TabComposer {
    @Autowired
    private transient ContainerService containerService;

    @Autowired
    private transient ImageService imageService;

    @Wire
    private Textbox nameTextBox;

    @Wire
    private Textbox imageNameTextBox;

    @Wire
    private Combobox restartComboBox;

    @Listen("onClick = #createSubmitButton; onSelect = #restartCombobox; onOK = #nameTextbox,#imageNameTextbox")
    public void onCreate() {

    }

    public static void show() {
        show("/container/create.zul");
    }
}
