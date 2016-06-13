package com.example.eric.weather_practice.Activity;

import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eric.weather_practice.Data.Weather;
import com.example.eric.weather_practice.Data.WeatherArray;
import com.example.eric.weather_practice.Data.WeatherDB;
import com.example.eric.weather_practice.R;
import com.example.eric.weather_practice.Tools.Utility;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/5/31.
 */
public class WeatherListFragment extends ListFragment {

    public ArrayList<Weather> mWeathers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeathers = WeatherArray.getInstance(getActivity()).getArray();
        WeatherAdapter adapter = new WeatherAdapter(mWeathers);
        Log.d("ListFragment1",adapter.toString());
        setListAdapter(adapter);

        setHasOptionsMenu(true);
    }

    /**
     * Toolbar上按钮的对应动作
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_weather: {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("isSelected",false);
                editor.apply();

                if(WeatherPagerActivity.weatherPagerActivity != null)
                    WeatherPagerActivity.weatherPagerActivity.finish();

                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    /**
     * ListView点击对应的动作
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        if(WeatherPagerActivity.weatherPagerActivity != null)
            WeatherPagerActivity.weatherPagerActivity.finish();

        Weather w = ((WeatherAdapter) getListAdapter()).getItem(position);

        Intent intent = new Intent(getActivity(), WeatherPagerActivity.class);
        intent.putExtra("weather", w);
        startActivity(intent);
        getActivity().finish();
    }


    /**
     * 上下文Context的设置
     * @param inflater
     * @param parent
     * @param savedInstance
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance) {
        View v = super.onCreateView(inflater, parent, savedInstance);
        ListView listView;
        if (v != null) {
            listView = (ListView) v.findViewById(android.R.id.list);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.weather_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_weather:
                            Log.d("ListFragment","delete");
                            ArrayAdapter<Weather> adapter = (ArrayAdapter<Weather>) getListAdapter();
                            Log.d("ListFragment",adapter.toString());
                            WeatherDB weatherDB = WeatherDB.getInstance(getActivity());
                            ArrayList<Weather> mWeathers = WeatherArray.getInstance(getActivity()).getArray();
                            for(Weather w:mWeathers) {
                                weatherDB.saveWeather(w);
                            }
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    weatherDB.deleteWeather(adapter.getItem(i));
                                }
                            }
                            ArrayList<Weather> tmp = weatherDB.loadWeather();
                            mWeathers.clear();
                            mWeathers.addAll(tmp);
                            Log.d("ListFragment",mWeathers.size()+"");
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }

        return v;
    }

    private class WeatherAdapter extends ArrayAdapter<Weather> {
        public WeatherAdapter(ArrayList<Weather> weathers) {
            super(getActivity(), 0, weathers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_weather, null);
            }
            Weather w = getItem(position);

            TextView city_name = (TextView) convertView.findViewById(R.id.weather_list_item_city_name);
            TextView city_weather = (TextView) convertView.findViewById(R.id.weather_list_item_city_weather);
            ImageView icon_weather = (ImageView) convertView.findViewById(R.id.weather_list_item_icon);

            city_name.setText(w.getCity_name());
            city_weather.setText(w.getWeather());
            icon_weather.setImageResource(Utility.getImgByWeather(w.getWeather()));

            return convertView;
        }
    }

}
