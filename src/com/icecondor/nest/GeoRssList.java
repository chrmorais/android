package com.icecondor.nest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class GeoRssList extends ListActivity {
	static final String appTag = "GeoRssList";
	Intent settingsIntent, radarIntent;
	EditText url_field;
	Spinner service_spinner;
	GeoRssSqlite rssdb;
	SQLiteDatabase geoRssDb;
	Cursor rsses;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.georsslist);
		rssdb = new GeoRssSqlite(this, "georss", null, 1);
		geoRssDb = rssdb.getReadableDatabase();
		//db.execSQL("insert into urls values (null, 'service', 'https://service.com'");
		rsses = geoRssDb.query(GeoRssSqlite.SERVICES_TABLE,null, null, null, null, null, null);
        ListAdapter adapter = new SimpleCursorAdapter(
                this, // Context
                android.R.layout.two_line_list_item,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows)
                rsses,  // Pass in the cursor to bind to.
                new String[] {GeoRssSqlite.NAME, GeoRssSqlite.URL}, // Array of cursor columns to bind to.
                new int[] {android.R.id.text1, android.R.id.text2});      // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        setListAdapter(adapter);
		
        // Jump Points
        settingsIntent = new Intent(this, Settings.class);
        radarIntent = new Intent(this, Radar.class);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(appTag, "onCreateOptionsMenu");
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_geo_rss_add).setIcon(android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, 3, Menu.NONE, R.string.menu_radar).setIcon(android.R.drawable.ic_menu_compass);
		return result;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(appTag, "menu:"+item.getItemId());
		
		switch (item.getItemId()) {
		case 1:
			showDialog(1);
			break;
		case 2:
			startActivity(settingsIntent);
			break;
		case 3:
			startActivity(radarIntent);
			break;
		}
		
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);
        View add_url_dialog = factory.inflate(R.layout.georssadd, null);
        service_spinner = (Spinner) add_url_dialog.findViewById(R.id.serviceselect);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.location_service_reader_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        service_spinner.setAdapter(adapter);

		return new AlertDialog.Builder(this)
			.setView(add_url_dialog)
			.setTitle(R.string.menu_geo_rss_add)
			.setPositiveButton("Add", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichbutton) {
					String service = (String)service_spinner.getSelectedItem();
					String url = url_field.getText().toString();
					if(service.equals("RSS")) {
						// nothing left to do
					}else if (service.equals("Brightkite")) {
						service = service + " " + url;
						url = "http://brightkite.com/people/"+url+"/objects.rss";
					}else if (service.equals("Shizzow")) {
						service = service + " " + url;
						url = "http://shizzow.com/"+url+"/rss";
					}
					insert_service(service, url);
					
					// not of the right way to get the list to refresh
					finish();
					Intent refresh = new Intent(GeoRssList.this, GeoRssList.class);
					startActivity(refresh);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichbutton) {

				}
			})
			.create();
	}

	protected void insert_service(String service, String url) {
		// GeoRSS Database
		SQLiteDatabase db = rssdb.getWritableDatabase();
		ContentValues cv = new ContentValues(2);
		cv.put("name", service);
		cv.put("url", url);
		db.insert(GeoRssSqlite.SERVICES_TABLE, null, cv);
		db.close();
	}
	
	protected void onPrepareDialog(int id, Dialog dialog) {
		url_field = (EditText) dialog.findViewById(R.id.rss_url_edit);
        url_field.setText(""); // initial value
	}
		
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		Log.i(appTag, "Item clicked position: "+position);
		Intent itemIntent = new Intent(this, GeoRssDetail.class);
		rsses.moveToPosition(position);
		itemIntent.putExtra(GeoRssSqlite.ID, rsses.getInt(rsses.getColumnIndex(GeoRssSqlite.ID)));
		startActivity(itemIntent);
	}
	
}
