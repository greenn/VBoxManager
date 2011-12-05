package com.kedzie.vbox.server;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kedzie.vbox.R;

public class EditServerActivity extends Activity {
	protected static final String TAG = EditServerActivity.class.getSimpleName();
	
	protected Server _server;
	
	@Override public Object onRetainNonConfigurationInstance() {
		return _server;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
       	_server = (Server)(getLastNonConfigurationInstance()==null ? getIntent().getParcelableExtra("server") : getLastNonConfigurationInstance());
       	((TextView)findViewById(R.id.server_name)).setText(_server.getName());
        ((TextView)findViewById(R.id.server_host)).setText(_server.getHost());
        ((TextView)findViewById(R.id.server_port)).setText(""+_server.getPort());
        ((TextView)findViewById(R.id.server_username)).setText(_server.getUsername());
        ((TextView)findViewById(R.id.server_password)).setText(_server.getPassword());
        ((ImageButton)findViewById(R.id.button_save)).setOnClickListener( new OnClickListener() { @Override public void onClick(View v) { save(); } });
        ((ImageButton)findViewById(R.id.button_delete)).setOnClickListener( new OnClickListener() { @Override public void onClick(View v) { delete(); } });
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.server_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.server_list_option_menu_save:
			save();
			return true;
		case R.id.server_list_option_menu_delete:
			delete();
			return true;
		default: 
			return true;
		}
	}
	
	@Override public void onBackPressed() {
		setResult(ServerListActivity.RESULT_CANCELED);
		super.onBackPressed();
	}

	private void save() {
		_server.setName( ((TextView)findViewById(R.id.server_name)).getText().toString() );
		_server.setHost( ((TextView)findViewById(R.id.server_host)).getText().toString() );
		_server.setPort( Integer.parseInt( ((TextView)findViewById(R.id.server_port)).getText().toString()) );
		_server.setUsername( ((TextView)findViewById(R.id.server_username)).getText().toString() );
		_server.setPassword(((TextView)findViewById(R.id.server_password)).getText().toString() );
		getIntent().putExtra("server", _server);
		setResult(ServerListActivity.RESULT_CODE_SAVE, getIntent());
		finish();
	}
	
	private void delete() {
		setResult(ServerListActivity.RESULT_CODE_DELETE, getIntent());
		finish();
	}
}