package com.example.eric.weather_practice;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Eric on 2016/5/31.
 */
public class WeatherListFragment extends ListFragment {

    public ArrayList<Weather> mWeathers;
    Button button_add_weather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeathers = WeatherDB.getInstance(getActivity()).loadWeather();
        //ArrayAdapter<Weather> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mWeathers);
        WeatherAdapter adapter = new WeatherAdapter(mWeathers);
        Log.d("ListFragment1",adapter.toString());
        setListAdapter(adapter);

        button_add_weather = (Button) getActivity().findViewById(R.id.add_weather);
        button_add_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("isSelected",false);
                editor.commit();
                startActivity(intent);
            }
        });

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.d("WeatherListFragment","onListItemClick");
        Weather w = ((WeatherAdapter) getListAdapter()).getItem(position);

        String code = w.getCity_code();
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        intent.putExtra("citycode",code);
        intent.putExtra("state", 1);
        if(WeatherActivity.weatherActivity != null)
            WeatherActivity.weatherActivity.finish();
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance) {
        View v = super.onCreateView(inflater, parent, savedInstance);
        ListView listView = (ListView) v.findViewById(android.R.id.list);
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

            TextView city_name = (TextView) convertView.findViewById(R.id.crime_list_item_city_name);
            TextView city_weather = (TextView) convertView.findViewById(R.id.crime_list_item_city_weather);
            city_name.setText(w.getCity_name());
            city_weather.setText(w.getWeather());

            return convertView;
        }
    }

}
