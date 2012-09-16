package com.hotinno.feedmonitor.web.seed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Grid;

import com.hotinno.feedmonitor.dao.btseed.BtSeed;
import com.hotinno.feedmonitor.dao.btseed.BtSeedDao;

//@Component
public class SeedGrid extends Grid implements Composer {

	private static final long serialVersionUID = 8481008130846652429L;

	@Autowired
	private BtSeedDao seedDao;

	List<BtSeed> seeds;

	public List<BtSeed> getSeeds() {
		return seeds;
	}

	@Override
	public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {
		seeds = seedDao.getTop(50);

		// ((Grid) comp).setModel(new ListModelList(seeds));
	}
}
