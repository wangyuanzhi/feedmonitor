package com.hotinno.feedmonitor.web.transmission;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.spring.context.annotation.EventHandler;
import org.zkoss.spring.util.GenericSpringComposer;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.hotinno.feedmonitor.dao.config.Config;
import com.hotinno.feedmonitor.dao.config.ConfigDao;
import com.hotinno.feedmonitor.dao.config.Transmission;

@Component(value = "transCtrl")
public class TransmissionZkController extends GenericSpringComposer {
	@Autowired
	private Textbox fqdn;

	@Autowired
	private Checkbox ssl;

	@Autowired
	private Intbox port;

	@Autowired
	private Textbox rpcPath;

	@Autowired
	private Textbox username;

	@Autowired
	private Textbox password1;

	@Autowired
	private Textbox password2;

	private boolean passwordChanged = false;

	@Autowired
	private Textbox comment;

	@SuppressWarnings("unused")
	@Autowired
	private Button updateBtn;

	@Autowired
	private Div updateDiv;

	@Autowired
	private ConfigDao configDao;

	@Override
	public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {
		super.doAfterCompose(comp);

		List<Config> items = configDao
				.getConfigBySection(Transmission.PARAM_SECTION_TRANSMISSION);
		boolean hasPassword = false;
		for (Config item : items) {
			if (item.getName().equals(Transmission.PARAM_NAME_FQDN)) {
				fqdn.setValue(item.getValue());
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_IS_SSL_ENABLED)) {
				ssl.setChecked(Boolean.parseBoolean(item.getValue()));
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_PORT)) {
				port.setValue(Integer.valueOf(item.getValue()));
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_RPC_PATH)) {
				rpcPath.setValue(item.getValue());
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_USERNAME)) {
				username.setValue(item.getValue());
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_COMMENT)) {
				comment.setValue(item.getValue());
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_PASSWORD)) {
				hasPassword = true;
				continue;
			}
		}

		if (hasPassword) {
			password1.setValue("I'm password");
			password2.setValue("I'm password");
		}
	}

	@EventHandler("updateBtn.onClick")
	@Transactional
	public void updateConfig(Event evt) throws WrongValueException,
			InterruptedException {
		List<Config> configList = new ArrayList<Config>(6);

		if (passwordChanged) {
			if (StringUtils.equals(password1.getValue(), password2.getValue())) {
				Config passwordConfig = getConfigItem(
						Transmission.PARAM_SECTION_TRANSMISSION,
						Transmission.PARAM_NAME_PASSWORD);
				passwordConfig.setValue(password1.getValue());

				configList.add(passwordConfig);
			} else {
				Messagebox.show("Password does not match!");
				return;
			}
		}

		Config fqdnConfig = getConfigItem(
				Transmission.PARAM_SECTION_TRANSMISSION,
				Transmission.PARAM_NAME_FQDN);
		fqdnConfig.setValue(fqdn.getValue());
		configList.add(fqdnConfig);

		Config sslConfig = getConfigItem(
				Transmission.PARAM_SECTION_TRANSMISSION,
				Transmission.PARAM_IS_SSL_ENABLED);
		sslConfig.setValue(Boolean.toString(ssl.isChecked()));
		configList.add(sslConfig);

		Config portConfig = getConfigItem(
				Transmission.PARAM_SECTION_TRANSMISSION,
				Transmission.PARAM_NAME_PORT);
		portConfig.setValue(String.valueOf(port.getValue()));
		configList.add(portConfig);

		Config rpcPathConfig = getConfigItem(
				Transmission.PARAM_SECTION_TRANSMISSION,
				Transmission.PARAM_NAME_RPC_PATH);
		rpcPathConfig.setValue(rpcPath.getValue());
		configList.add(rpcPathConfig);

		Config usernameConfig = getConfigItem(
				Transmission.PARAM_SECTION_TRANSMISSION,
				Transmission.PARAM_NAME_USERNAME);
		usernameConfig.setValue(username.getValue());
		configList.add(usernameConfig);

		Config commentConfig = getConfigItem(
				Transmission.PARAM_SECTION_TRANSMISSION,
				Transmission.PARAM_NAME_COMMENT);
		commentConfig.setValue(comment.getValue());
		configList.add(commentConfig);

		configDao.merge(configList);
		Label updated = new Label("Updated!");
		updated.setStyle("color=red");
		updateDiv.appendChild(updated);
	}

	@EventHandler("password1.onChange,password2.onChange")
	public void passworkChange(Event evt) throws WrongValueException,
			InterruptedException {
		passwordChanged = true;
	}

	private Config getConfigItem(String section, String name) {
		Config item = configDao.getConfig(section, name);

		if (item == null) {
			item = new Config();
			item.setName(name);
			item.setSection(section);

			configDao.persist(item);
		}

		return item;
	}
}
