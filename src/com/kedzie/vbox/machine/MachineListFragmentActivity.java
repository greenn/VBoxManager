package com.kedzie.vbox.machine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.TabSupport;
import com.kedzie.vbox.app.TabSupportFragment;
import com.kedzie.vbox.app.TabSupportViewPager;
import com.kedzie.vbox.event.EventIntentService;
import com.kedzie.vbox.machine.MachineListBaseFragment.SelectMachineListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;

public class MachineListFragmentActivity extends BaseActivity implements SelectMachineListener {
	public final static String INTENT_VERSION = "version";
	
	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	/** {@link ActionBar} tabs */
	private TabSupport _tabSupport;
	

	private class LogoffTask extends DialogTask<Void, Void>	{
		public LogoffTask(VBoxSvc vmgr) { 
			super( "LogoffTask", MachineListFragmentActivity.this, vmgr, "Logging Off");
		}
		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		_vmgr = (VBoxSvc)getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		setContentView(R.layout.machine_list);

		FrameLayout detailsFrame = (FrameLayout)findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
		if(_dualPane) {
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			if(VBoxApplication.VIEW_PAGER_TABS) {
				 ViewPager pager = new ViewPager(this);
				 pager.setId(99);
				 detailsFrame.addView(pager);
				 _tabSupport = new TabSupportViewPager(this, pager);
			} else {
				_tabSupport = new TabSupportFragment(this, R.id.details);
			}
			 if (savedInstanceState != null) 
		            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
		startService(new Intent(this, EventIntentService.class).putExtras(getIntent()));
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(_dualPane)
        	outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
    }
	
	@Override
	public void onMachineSelected(IMachine machine) {
		if (_dualPane) {
			_tabSupport.removeAllTabs();
			Bundle b = new BundleBuilder().putParcelable(VBoxSvc.BUNDLE, _vmgr)
															.putProxy(IMachine.BUNDLE, machine)
															.putBoolean("dualPane", true)
															.create();
			_tabSupport.addTab(getString(R.string.tab_actions), ActionsFragment.class, b);
			_tabSupport.addTab(getString(R.string.tab_info), InfoFragment.class, b);
			_tabSupport.addTab(getString(R.string.tab_log), LogFragment.class, b);
			_tabSupport.addTab(getString(R.string.tab_snapshots), SnapshotFragment.class, b);
		} else {
			Intent intent = new Intent(this, MachineFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr);
			BundleBuilder.addProxy(intent, IMachine.BUNDLE, machine );
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			logoff();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return false;
	}

	@Override 
	public void onBackPressed() {
		logoff();
		super.onBackPressed();
	}
	
	public void logoff() {
		stopService(new Intent(this, EventIntentService.class));
		if(_vmgr.getVBox()!=null)  
			new LogoffTask(_vmgr). execute();
	}
}
