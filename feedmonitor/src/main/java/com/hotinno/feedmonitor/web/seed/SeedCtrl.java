package com.hotinno.feedmonitor.web.seed;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.spring.context.annotation.EventHandler;
import org.zkoss.spring.util.GenericSpringComposer;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.hotinno.feedmonitor.dao.btseed.BtSeed;
import com.hotinno.feedmonitor.dao.btseed.BtSeedDao;
import com.hotinno.feedmonitor.dao.feed.FeedDao;
import com.hotinno.feedmonitor.dao.heartbeat.HeartBeat;
import com.hotinno.feedmonitor.dao.heartbeat.HeartBeatDao;

@Component
public class SeedCtrl extends GenericSpringComposer {

	private static final long serialVersionUID = 8481008130846652429L;
	private static final Pattern PATTERN_MAGNET_BT = Pattern
			.compile("(xt=urn:btih:[a-zA-Z0-9]+)");

	private static final SimpleDateFormat DATE_FORMART = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss");

	static {
		DATE_FORMART.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	}

	@Autowired
	private BtSeedDao seedDao;

	@Autowired
	private FeedDao feedDao;

	@Autowired
	private HeartBeatDao heartBeatDao;

	@Autowired
	private SeedRowRenderer rowRenderer;

	@Autowired
	private Div infoDiv;
	@Autowired
	private Grid seedGrid;

	@Autowired
	private Textbox url;

	@SuppressWarnings("unused")
	@Autowired
	private Button addSeedBtn;

	public List<BtSeed> getSeeds() {
		List<BtSeed> seeds = seedDao.getTop(20);
		return seeds;
	}

	public BindingListModelList getGridModel() {
		return new BindingListModelList(getSeeds(), true);
	}

	@Override
	public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {
		super.doAfterCompose(comp);

		List<HeartBeat> hbList = heartBeatDao.getLastHeartBearts(1);

		String lastHeartBeat;
		if (hbList.size() > 0) {
			HeartBeat hb = hbList.get(0);

			lastHeartBeat = DATE_FORMART.format(hb.getLastBeat());
		} else {
			lastHeartBeat = "Unknown";
		}
		rowRenderer.setLastHeartBeat(lastHeartBeat);

		String lastFeedFetch;
		Date lastFeedFetchTime = feedDao.getLastFetchTime();
		if (lastFeedFetchTime != null) {
			lastFeedFetch = DATE_FORMART.format(lastFeedFetchTime);
		} else {
			lastFeedFetch = "Unknown";
		}
		rowRenderer.setLastFeedFetch(lastFeedFetch);

		// ((Window) comp)
		// .setTitle(String
		// .format("Buffalo's Seed Management (Last HeartBeat: %s, Last FeedFetch: %s)",
		// lastHeartBeat, lastFeedFetch));
		infoDiv.appendChild(new Label("Last "));
		A heartbeat = new A("Heart Beat");
		heartbeat.setHref("heartbeat.html");
		infoDiv.appendChild(heartbeat);
		infoDiv.appendChild(new Label(String.format(": %s", lastHeartBeat)));
		infoDiv.appendChild(new Html("<BR>"));
		infoDiv.appendChild(new Label("Last "));
		A feedfetch = new A("FeedFetch");
		feedfetch.setHref("feed.html");
		infoDiv.appendChild(feedfetch);
		infoDiv.appendChild(new Label(String.format(": %s", lastFeedFetch)));

		seedGrid.setRowRenderer(rowRenderer);
		seedGrid.setModel(getGridModel());

		comp.addEventListener(Events.ON_CLIENT_INFO, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				ClientInfoEvent cie = (ClientInfoEvent) event;

				// String message = String.format(
				// "Screen: %s, %s\nDesktop: %s, %s\nOffset: %s, %s",
				// cie.getScreenWidth(), cie.getScreenHeight(),
				// cie.getDesktopWidth(), cie.getDesktopHeight(),
				// cie.getDesktopXOffset(), cie.getDesktopYOffset());
				//
				// Messagebox.show(message);

				rowRenderer.setDesktopWidth(cie.getDesktopWidth());
				seedGrid.renderAll();
			}
		});
	}

	@EventHandler("addSeedBtn.onClick")
	public void addSeed(Event evt) throws WrongValueException,
			InterruptedException {
		String magnetUrl = url.getValue();
		if (StringUtils.isNotEmpty(magnetUrl)) {
			if (seedDao.isMagnetUrlExisted(magnetUrl)) {
				Messagebox.show("Duplicated Magnet URL!");
				return;
			}

			String name;

			Matcher m = PATTERN_MAGNET_BT.matcher(magnetUrl);

			if (m.find()) {
				name = m.group(1);
			} else {
				name = "Unknown";
			}

			BtSeed seed = new BtSeed(name, magnetUrl);
			seedDao.persist(seed);

			((BindingListModelList) seedGrid.getModel()).add(seed);
		} else {
			Messagebox.show("Magnet URL cannot be empty!");
		}
	}

}
