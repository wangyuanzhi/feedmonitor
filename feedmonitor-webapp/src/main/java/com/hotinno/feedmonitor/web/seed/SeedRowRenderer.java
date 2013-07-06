package com.hotinno.feedmonitor.web.seed;

import java.util.List;

import org.springframework.stereotype.Component;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SizeEvent;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

import com.hotinno.feedmonitor.dao.btseed.BtSeed;
import com.hotinno.feedmonitor.dao.btseed.BtSeedDao;
import com.hotinno.feedmonitor.task.BtSeedsMonitor;

@Component
public class SeedRowRenderer implements RowRenderer {

	public static final String PREFIX_SEED_ROW_ID = "seed_";
	public static final String PREFIX_SEED_NAME_ID = "name_";
	public static final String PREFIX_SEED_ADDING_TIME_ID = "date_";
	public static final String PREFIX_SEED_IS_PROCESSED_DIV_ID = "processedDiv_";
	public static final String PREFIX_SEED_IS_PROCESSED_ID = "processed_";
	public static final String PREFIX_SEED_PROCESSED_TIME_ID = "processedTime_";
	public static final String PREFIX_SEED_COMMENT_ID = "comment_";
	public static final String YES = "Yes";
	public static final String NO = "No";

	public static final String LABEL_DELETE = "Delete";
	public static final String LABEL_CLEAR = "Clear Processed";

	private static final int ROW_WIDTH_WITHOUT_NAME_COLUMN =
	// Adding Time
	130
	// Processed
			+ 100
			// Processed Time
			+ 130
			// Comment
			+ 80
			// Actions
			+ 180
			// Overhead
			+ 10;

	private String lastHeartBeat;

	public void setLastHeartBeat(String lastHeartBeat) {
		this.lastHeartBeat = lastHeartBeat;
	}

	private String lastFeedFetch;

	public void setLastFeedFetch(String lastFeedFetch) {
		this.lastFeedFetch = lastFeedFetch;
	}

	private int desktopWidth;

	public void setDesktopWidth(int desktopWidth) {
		this.desktopWidth = desktopWidth;
	}

	@Override
	public void render(Row row, Object data) throws Exception {
		BtSeed seed = (BtSeed) data;

		renderWithData(row, seed);
	}

	public void renderWithData(Row row, BtSeed seed) {
		final Long id = seed.getId();

		String toolTipsText = String.format(
				"Last Heart Beat: %s\nLast FeedFetch: %s", lastHeartBeat,
				lastFeedFetch);

		int rowWidth = desktopWidth - ROW_WIDTH_WITHOUT_NAME_COLUMN;
//		int nameLength = getNameLength(rowWidth);
		int nameLength = 65;

		row.setId(PREFIX_SEED_ROW_ID + id);

		Div nameDiv = new Div();
		nameDiv.setAlign("left");
		final Label name = new Label();
		name.setId(PREFIX_SEED_NAME_ID + id);
		String nameStr = seed.getName();
		name.setTooltiptext(nameStr);
		setNameValue(name, nameLength);
		name.setStyle("align:'left';");
		name.setWidth("99%");

		nameDiv.appendChild(name);

		nameDiv.addEventListener(Events.ON_SIZE, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				SizeEvent se = (SizeEvent) event;

				try {
					int width = Integer.parseInt(se.getWidth());
					int nameLength = getNameLength(width);

					setNameValue(name, nameLength);
				} catch (NumberFormatException e) {
					// Do nothing
				}

			}
		});

		Datebox addingTime = new Datebox(seed.getDate());
		addingTime.setId(PREFIX_SEED_ADDING_TIME_ID + id);
		addingTime.setTimeZone("GMT+8");
		addingTime.setFormat("yyyy.MM.dd HH:mm:ss");
		addingTime.setReadonly(true);
		addingTime.setButtonVisible(false);
		addingTime.setWidth("99%");

		Div processedDiv = new Div();
		processedDiv.setTooltiptext(toolTipsText);
		processedDiv.setId(PREFIX_SEED_IS_PROCESSED_DIV_ID + id);

		Label processedLabel = new Label();
		processedLabel.setId(PREFIX_SEED_IS_PROCESSED_ID + id);
		processedLabel.setValue(seed.isProcessed() ? YES : NO);
		processedLabel.setWidth("99%");
		processedDiv.appendChild(processedLabel);

		ProcessEventListener.createProcessItButton(processedLabel, id);
		processedLabel.addEventListener(Events.ON_CHANGE,
				new ProcessEventListener(processedLabel, id));
		processedLabel.addEventListener(Events.ON_CREATE,
				new ProcessEventListener(processedLabel, id));

		Datebox processedTime = new Datebox(seed.getProcessedTime());
		processedTime.setId(PREFIX_SEED_PROCESSED_TIME_ID + id);
		processedTime.setTimeZone("GMT+8");
		processedTime.setFormat("yyyy.MM.dd HH:mm:ss");
		processedTime.setReadonly(true);
		processedTime.setButtonVisible(false);
		processedTime.setWidth("99%");
		processedTime.setTooltiptext(toolTipsText);

		Label comment = new Label(seed.getComment());
		comment.setId(PREFIX_SEED_COMMENT_ID + id);
		comment.setWidth("99%");

		Button deleteBtn = new Button();
		deleteBtn.setLabel(LABEL_DELETE);
		deleteBtn.addEventListener(Events.ON_CLICK,
				new DeleteFeedEventListener(deleteBtn, id));

		Button clearBtn = new Button();
		clearBtn.setLabel(LABEL_CLEAR);
		clearBtn.addEventListener(Events.ON_CLICK,
				new ClearProcessedEventListener(clearBtn, id));

		Div btnDiv = new Div();
		btnDiv.appendChild(deleteBtn);
		btnDiv.appendChild(clearBtn);
		btnDiv.setAlign("left");

		row.appendChild(nameDiv);
		row.appendChild(addingTime);
		row.appendChild(processedDiv);
		row.appendChild(processedTime);
		row.appendChild(comment);
		row.appendChild(btnDiv);
	}

	public void setNameValue(Label name, int nameLength) {
		String nameStr = name.getTooltiptext();
		if (nameStr.length() > nameLength) {
			name.setValue(nameStr.substring(0, nameLength - 3) + "...");
		} else {
			name.setValue(nameStr);
		}
	}

	public int getNameLength(int rowWidth) {
		if (rowWidth < 100) {
			rowWidth = 100;
		}
		int nameLength = rowWidth / 5;
		return nameLength;
	}
}

abstract class SeedEventListener implements EventListener {
	public static final String SEED_GRID_ID = "seedGrid";

	public static BtSeed getTargetSeed(HtmlBasedComponent component, long id) {
		Grid grid = (Grid) component.getFellow(SEED_GRID_ID, true);
		BindingListModelList model = (BindingListModelList) grid.getListModel();

		BtSeed feed = null;

		for (int i = 0; i < model.getSize(); i++) {
			BtSeed f = (BtSeed) model.getElementAt(i);
			Long feedId = f.getId();
			if ((feedId == null && id == 0) || feedId == id) {
				feed = f;
				break;
			}
		}
		return feed;
	}

	public static BindingListModelList getGridModel(HtmlBasedComponent component) {
		Grid grid = (Grid) component.getFellow(SEED_GRID_ID, true);
		BindingListModelList model = (BindingListModelList) grid.getListModel();

		return model;
	}
}

class DeleteFeedEventListener extends SeedEventListener {

	private final Button button;
	private final long id;

	public DeleteFeedEventListener(Button button, long id) {
		this.button = button;
		this.id = id;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName())) {
			if (event.getTarget() instanceof Button) {
				Button b = (Button) event.getTarget();

				if (b == button) {
					final BtSeed seed = getTargetSeed(button, id);

					Messagebox.show(String.format(
							"Are you sure to delete Seed:\n\n%s?",
							seed.getName()), "Delete Seed", Messagebox.YES
							| Messagebox.NO, Messagebox.QUESTION,
							Messagebox.NO,
							new org.zkoss.zk.ui.event.EventListener() {
								public void onEvent(Event e) {
									if (Messagebox.ON_YES.equals(e.getName())) {
										BtSeedDao seedDao = SpringUtil
												.getApplicationContext()
												.getBean(BtSeedDao.class);

										seedDao.deleteById(id);

										getGridModel(button).remove(seed);
									} else if (Messagebox.ON_CANCEL.equals(e
											.getName())) {
										// Cancel is clicked
									}
								}
							});
				}
			}
		}

	}
}

class ClearProcessedEventListener extends SeedEventListener {

	private final Button button;
	private final long id;

	public ClearProcessedEventListener(Button button, long id) {
		this.button = button;
		this.id = id;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName())) {
			if (event.getTarget() instanceof Button) {
				Button b = (Button) event.getTarget();

				if (b == button) {
					final BtSeed seed = getTargetSeed(button, id);
					seed.setProcessed(false);
					seed.setProcessedTime(null);

					BtSeedDao seedDao = SpringUtil.getApplicationContext()
							.getBean(BtSeedDao.class);
					seedDao.merge(seed);

					Row row = (Row) button
							.getFellow(SeedRowRenderer.PREFIX_SEED_ROW_ID + id);

					for (Object obj : row.getChildren()) {
						HtmlBasedComponent child = (HtmlBasedComponent) obj;
						if (child.getId().equals(
								SeedRowRenderer.PREFIX_SEED_PROCESSED_TIME_ID
										+ id)) {
							((Datebox) child).setValue(null);
							continue;
						}

						if (child.getId().equals(
								SeedRowRenderer.PREFIX_SEED_IS_PROCESSED_DIV_ID
										+ id)) {

							@SuppressWarnings("rawtypes")
							List processedVidChildren = child.getChildren();

							for (int i = 0; i < processedVidChildren.size(); i++) {
								HtmlBasedComponent c = (HtmlBasedComponent) processedVidChildren
										.get(i);

								if (c.getId()
										.equals(SeedRowRenderer.PREFIX_SEED_IS_PROCESSED_ID
												+ id)) {
									Label processedLabel = (Label) c;
									processedLabel.setValue(SeedRowRenderer.NO);
									ProcessEventListener.createProcessItButton(
											processedLabel, id);
									break;
								}
							}
							continue;
						}
					}
				}
			}
		}
	}
}

class ProcessEventListener extends SeedEventListener {
	private final Label processed;
	private final long id;

	public ProcessEventListener(Label processed, long id) {
		this.processed = processed;
		this.id = id;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		createProcessItButton(processed, id);
	}

	public static void createProcessItButton(final Label processed,
			final long id) {
		if (SeedRowRenderer.YES.equals(processed.getValue())) {
			@SuppressWarnings("rawtypes")
			List children = processed.getParent().getChildren();

			for (Object object : children) {
				if (object instanceof org.zkoss.zul.api.Button) {
					processed.removeChild((org.zkoss.zul.api.Button) object);
				}
			}

			return;
		}
		final Button btn = new Button("Process it!");

		btn.addEventListener(Events.ON_CLICK, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				BtSeedsMonitor monitor = SpringUtil.getApplicationContext()
						.getBean(BtSeedsMonitor.class);

				BtSeed seed = monitor.checkSeed(id);

				BindingListModelList model = getGridModel(processed);

				for (int i = 0; i < model.getSize(); i++) {
					BtSeed s = (BtSeed) model.get(i);
					if (s.getId() == id) {
						model.set(i, seed);
						break;
					}
				}
			}
		});

		processed.getParent().appendChild(btn);
	}
}